package net.mograsim.plugin.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import net.mograsim.machine.MachineDefinition;
import net.mograsim.plugin.MograsimActivator;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.ProjectMachineContext;

public class MachineLaunchConfigType extends LaunchConfigurationDelegate
{
	public static final String PROJECT_ATTR = MograsimActivator.PLUGIN_ID + "project";

	private final IResourceChangeListener resChangedListener;

	public MachineLaunchConfigType()
	{
		this.resChangedListener = this::resourceChanged;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resChangedListener,
				// IResourceChangeEvent.POST_BUILD |
				IResourceChangeEvent.POST_CHANGE |
				// IResourceChangeEvent.PRE_BUILD |
				// IResourceChangeEvent.PRE_CLOSE |
				// IResourceChangeEvent.PRE_DELETE |
				// IResourceChangeEvent.PRE_REFRESH |
						0);
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		String projName = configuration.getAttribute(PROJECT_ATTR, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		MachineContext machineContext = ProjectMachineContext.getMachineContextOf(project);
		MachineDefinition machineDefinition = machineContext.getMachineDefinition().orElseThrow();

		MachineDebugTarget debugTarget = new MachineDebugTarget(new MachineProcess(launch, machineDefinition));
		debugTarget.setExecutionSpeed(10d);
	}

	private void resourceChanged(IResourceChangeEvent event)
	{
		// TODO react to MPM changes
		int type = event.getType();
		String typeStr;
		switch (type)
		{
		case IResourceChangeEvent.POST_BUILD:
			typeStr = "POST_BUILD";
			break;
		case IResourceChangeEvent.POST_CHANGE:
			typeStr = "POST_CHANGE";
			break;
		case IResourceChangeEvent.PRE_BUILD:
			typeStr = "PRE_BUILD";
			break;
		case IResourceChangeEvent.PRE_CLOSE:
			typeStr = "PRE_CLOSE";
			break;
		case IResourceChangeEvent.PRE_DELETE:
			typeStr = "PRE_DELETE";
			break;
		case IResourceChangeEvent.PRE_REFRESH:
			typeStr = "PRE_REFRESH";
			break;
		default:
			typeStr = "<unknown: " + type + ">";
		}
		System.out.println(typeStr + ": " + event);
	}
}