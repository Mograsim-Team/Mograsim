package net.mograsim.plugin.launch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.statushandlers.StatusManager;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.plugin.MograsimActivator;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.MograsimNature;
import net.mograsim.plugin.nature.ProjectMachineContext;

public class MachineLaunchConfigType extends LaunchConfigurationDelegate
{
	public static final String PROJECT_ATTR = MograsimActivator.PLUGIN_ID + ".project";
	public static final String MPM_FILE_ATTR = MograsimActivator.PLUGIN_ID + ".mpm";
	public static final String INITIAL_RAM_FILE_ATTR = MograsimActivator.PLUGIN_ID + ".initialram";

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
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException
	{
		String projName = configuration.getAttribute(PROJECT_ATTR, "");
		if ("".equals(projName))
			return showErrorAndReturnFalse("No project specified");

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		if (!project.isAccessible())
			return showErrorAndReturnFalse("Project not accessible");
		if (!project.hasNature(MograsimNature.NATURE_ID))
			return showErrorAndReturnFalse("Project is not a Mograsim project");

		MachineContext machineContext = ProjectMachineContext.getMachineContextOf(project);
		Optional<MachineDefinition> machDefOptional = machineContext.getMachineDefinition();
		if (machDefOptional.isEmpty())
			return showErrorAndReturnFalse("No machine definition set");

		MachineDefinition machineDefinition = machDefOptional.orElseThrow();
		MicroInstructionMemoryDefinition miMemDef = machineDefinition.getMicroInstructionMemoryDefinition();

		String mpmFileName = configuration.getAttribute(MPM_FILE_ATTR, "");
		if ("".equals(mpmFileName))
			return showErrorAndReturnFalse("No MPM file specified");

		IFile mpmFile = project.getFile(mpmFileName);
		if (!mpmFile.isAccessible())
			return showErrorAndReturnFalse("MPM file not accessible");

		try (InputStream mpmStream = mpmFile.getContents())
		{
			MicroInstructionMemoryParser.parseMemory(miMemDef, mpmStream);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading MPM file", e));
		}

		// TODO parse RAM

		return super.preLaunchCheck(configuration, mode, monitor);
	}

	private static boolean showErrorAndReturnFalse(String message)
	{
		StatusManager.getManager().handle(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, message, null), StatusManager.SHOW);
		return false;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		String projName = configuration.getAttribute(PROJECT_ATTR, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);

		MachineContext machineContext = ProjectMachineContext.getMachineContextOf(project);
		MachineDefinition machineDefinition = machineContext.getMachineDefinition().orElseThrow();
		MicroInstructionMemoryDefinition miMemDef = machineDefinition.getMicroInstructionMemoryDefinition();

		IFile mpmFile = project.getFile(configuration.getAttribute(MPM_FILE_ATTR, ""));

		MicroInstructionMemory mpm;
		try (InputStream mpmStream = mpmFile.getContents())
		{
			mpm = MicroInstructionMemoryParser.parseMemory(miMemDef, mpmStream);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading MPM file", e));
		}

		// TODO parse RAM

		MachineDebugTarget debugTarget = new MachineDebugTarget(launch, machineDefinition);
		debugTarget.suspend();
		debugTarget.setExecutionSpeed(1);
		Machine machine = debugTarget.getMachine();
		machine.getMicroInstructionMemory().bind(mpm);
		// TODO bind RAM
		machine.reset();
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