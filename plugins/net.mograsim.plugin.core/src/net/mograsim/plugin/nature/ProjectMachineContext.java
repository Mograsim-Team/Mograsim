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

import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.nature.ProjectContextEvent.ProjectContextEventType;

/**
 * This class is a register for {@link MachineContext} mapped by their {@link IProject}
 * <p>
 * It can be used to obtain (and thereby create if necessary) {@link MachineContext}s for projects and {@link IAdaptable}s that are somewhat
 * associated to Mograsim nature. The register is unique and static context of this class. Since it also depends on the installed machines,
 * it listens to changes of the {@link MachineRegistry}.
 *
 * @author Christian Femers
 *
 */
public class ProjectMachineContext
{
	private static Map<IProject, MachineContext> projectMachineContexts = Collections.synchronizedMap(new HashMap<>());
	private static final Set<ProjectContextListener> listeners = Collections.synchronizedSet(new HashSet<>());

	public static final String MOGRASIM_PROJECT_PREFS_NODE = "net.mograsim";
	public static final String MACHINE_PROPERTY = "net.mograsim.projectMachineId";

	private ProjectMachineContext()
	{

	}

	/**
	 * This method returns the associated machine context or created a new one if none is associated yet.
	 * 
	 * @param project the project to get the {@link MachineContext} for (or create one, if possible). It must have Mograsim nature.
	 * 
	 * @throws IllegalArgumentException if the project is not accessible or has no mograsim nature
	 * @throws NullPointerException     if the project is null
	 * 
	 */
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

	/**
	 * This method returns the associated machine context or created a new one if none is associated yet. The given resource must be
	 * adaptable to {@link IProject}.
	 * 
	 * @param mograsimProjectAdapable the {@link IProject}-{@link IAdaptable} to get the {@link MachineContext} for (or create one, if
	 *                                possible). Must be contained in a Mograsim nature project.
	 * 
	 * @throws IllegalArgumentException if the project is not accessible or has no mograsim nature
	 * @throws NullPointerException     if the {@link IAdaptable} is null or it cannot be adapted to {@link IProject}
	 * 
	 */
	public static MachineContext getMachineContextOf(IAdaptable mograsimProjectAdapable)
	{
		IProject project = Adapters.adapt(mograsimProjectAdapable, IProject.class, true);
		Objects.requireNonNull(project, "project was null / no project found for " + mograsimProjectAdapable);
		return getMachineContextOf(project);
	}

	/**
	 * Returns all {@link MachineContext} known, in the sense of all that got ever created during this runtime.
	 */
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
			Objects.requireNonNull(project, "Project was null");
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

	/**
	 * Tests for Mograsim nature. This method is null safe and will not throw any exception.
	 */
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
		MachineRegistry.addMachineRegistryListener(newMap -> updateAllStatus());
	}

	private static void updateAllStatus()
	{
		projectMachineContexts.forEach((p, mc) -> mc.updateStatus());
	}

	private static void resourceChanged(IResourceChangeEvent event)
	{
//		System.out.println(((ResourceChangeEvent) event).toDebugString());
		// We try to do as many cheap tests first as possible, because this listener is not limited to plain project actions.
		if (event.getResource() == null)
			return;
		IProject project = event.getResource().getProject();
		if (project == null)
			return;
		MachineContext mc = projectMachineContexts.get(project);
		if (mc == null)
			return;
		ProjectContextEventType eventType = ProjectContextEventType.ofResourceChangeEvent(event.getType());
//		if (eventType == ProjectContextEventType.OTHER_CHANGE && project.isOpen())
//			return; // we don't care about all small changes (TODO: research if this has any drawbacks)
		eventType.getForcedStatus().ifPresent(mc::forceUpdateStatus);
		notifyListeners(new ProjectContextEvent(mc, eventType));
	}
}
