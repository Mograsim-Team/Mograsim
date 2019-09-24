package net.mograsim.plugin.nature;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import net.mograsim.plugin.nature.ProjectContextEvent.ProjectContextEventType;

public class ProjectMachineContext
{
	private static Map<IProject, MachineContext> projectMachineContexts = Collections.synchronizedMap(new HashMap<>());
	private static final Set<ProjectContextListener> listeners = Collections.synchronizedSet(new HashSet<>());

	public static final String MOGRASIM_PROJECT_PREFS_NODE = "net.mograsim";
	public static final String MACHINE_PROPERTY = "net.mograsim.projectMachineId";

	public static MachineContext getMachineContextOf(IProject project)
	{
		MachineContext mc = projectMachineContexts.get(project);
		if (mc != null)
			return mc;
		validateMograsimNatureProject(project);
		mc = new MachineContext(project);
		projectMachineContexts.put(project, mc);
		notifyListeners(new ProjectContextEvent(mc, ProjectContextEventType.NEW));
		return mc;
	}

	public static MachineContext getMachineContextOf(IAdaptable mograsimProjectAdapable)
	{
		IProject project = Adapters.adapt(mograsimProjectAdapable, IProject.class, true);
		Objects.requireNonNull(project, "project was null / no project found for " + mograsimProjectAdapable);
		return getMachineContextOf(project);
	}

	public static Map<IProject, MachineContext> getAllProjectMachineContexts()
	{
		return Collections.unmodifiableMap(projectMachineContexts);
	}

	static ScopedPreferenceStore getProjectPrefs(IProject mograsimProject)
	{
		return new ScopedPreferenceStore(new ProjectScope(mograsimProject), MOGRASIM_PROJECT_PREFS_NODE);
	}

	static IProject validateMograsimNatureProject(IAdaptable mograsimProjectAdapable)
	{
		IProject project;
		if (mograsimProjectAdapable instanceof IProject)
		{
			project = (IProject) mograsimProjectAdapable;
		} else
		{
			project = Adapters.adapt(mograsimProjectAdapable, IProject.class, true);
			Objects.requireNonNull(project, () -> mograsimProjectAdapable + " is not adaptable to IProject");
		}
		try
		{
			if (!project.isNatureEnabled(MograsimNature.NATURE_ID))
				throw new IllegalArgumentException(mograsimProjectAdapable + "is not (in) a Mograsim project");
		}
		catch (CoreException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException(mograsimProjectAdapable + " project nature could not be evaluated", e);
		}
		return project;
	}

	static boolean hasMograsimNature(IProject project)
	{
		if (project == null)
			return false;
		try
		{
			return project.isNatureEnabled(MograsimNature.NATURE_ID);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	static Optional<String> getMachineIdFrom(ScopedPreferenceStore preferenceStore)
	{
		if (preferenceStore.contains(MACHINE_PROPERTY))
			return Optional.of(preferenceStore.getString(MACHINE_PROPERTY));
		return Optional.empty();
	}

	static void notifyListeners(ProjectContextEvent projectContextEvent)
	{
		listeners.forEach(l -> l.onProjectContextChange(projectContextEvent));
	}

	public static void addProjectContextListener(ProjectContextListener listener)
	{
		listeners.add(listener);
	}

	public static void removeProjectContextListener(ProjectContextListener listener)
	{
		listeners.remove(listener);
	}

	static
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(ProjectMachineContext::resourceChanged);
	}

	private static void resourceChanged(IResourceChangeEvent event)
	{
//		System.out.println(((ResourceChangeEvent) event).toDebugString());
		ProjectContextEventType eventType = ProjectContextEventType.ofResourceChangeEvent(event.getType());
		if (eventType == null)
			return;
		if (event.getResource() == null || event.getResource().getProject() == null)
			return;
		MachineContext mc = projectMachineContexts.get(event.getResource().getProject());
		if (mc == null)
			return;
//		System.out.println("  " + eventType + " - " + mc.getProject());
		notifyListeners(new ProjectContextEvent(mc, eventType));
	}
}
