package net.mograsim.plugin.nature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ProjectMachineContext
{
	private static Map<IProject, MachineContext> projectMachineContexts = Collections.synchronizedMap(new HashMap<>());

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
		return mc;
	}

	public static MachineContext getMachineContextOf(IAdaptable mograsimProjectAdapable)
	{
		IProject project = Adapters.adapt(mograsimProjectAdapable, IProject.class, true);
		Objects.requireNonNull(project, "project was null / no project found for " + mograsimProjectAdapable);
		return getMachineContextOf(project);
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
}
