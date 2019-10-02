package net.mograsim.plugin.launch;

import static org.eclipse.core.resources.IResourceDelta.CHANGED;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.standard.memory.MainMemoryParser;
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
	private IFile mpmFile;
	private Machine machine;

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
		MainMemoryDefinition mainMemDef = machineDefinition.getMainMemoryDefinition();

		String mpmFileName = configuration.getAttribute(MPM_FILE_ATTR, "");
		if ("".equals(mpmFileName))
			return showErrorAndReturnFalse("No MPM file specified");

		IFile mpmFile = project.getFile(mpmFileName);
		if (mpmFile == null || !mpmFile.isAccessible())
			return showErrorAndReturnFalse("MPM file not accessible");

		try (InputStream mpmStream = mpmFile.getContents())
		{
			MicroInstructionMemoryParser.parseMemory(miMemDef, mpmStream);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading MPM file", e));
		}

		String initialRAMFileName = configuration.getAttribute(INITIAL_RAM_FILE_ATTR, "");
		if (!"".equals(initialRAMFileName))
		{
			IFile initialRAMFile = project.getFile(initialRAMFileName);
			if (initialRAMFile == null || !initialRAMFile.isAccessible())
				return showErrorAndReturnFalse("Initial RAM file not accessible");

			try (InputStream initialRAMStream = initialRAMFile.getContents())
			{
				MainMemoryParser.parseMemory(mainMemDef, initialRAMStream);
			}
			catch (IOException e)
			{
				throw new CoreException(
						new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading initial RAM file", e));
			}
		}

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
		MainMemoryDefinition mainMemDef = machineDefinition.getMainMemoryDefinition();

		mpmFile = project.getFile(configuration.getAttribute(MPM_FILE_ATTR, ""));

		String initialRAMFileName = configuration.getAttribute(INITIAL_RAM_FILE_ATTR, "");
		MainMemory mem;
		if (!"".equals(initialRAMFileName))
		{
			IFile initialRAMFile = project.getFile(initialRAMFileName);
			try (InputStream initialRAMStream = initialRAMFile.getContents())
			{
				mem = MainMemoryParser.parseMemory(mainMemDef, initialRAMStream);
			}
			catch (IOException e)
			{
				throw new CoreException(
						new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading initial RAM file", e));
			}
		} else
			mem = null;

		MachineDebugTarget debugTarget = new MachineDebugTarget(launch, machineDefinition);
		debugTarget.suspend();
		debugTarget.setExecutionSpeed(1);
		machine = debugTarget.getMachine();
		assignMicroInstructionMemory();
		if (mem != null)
			machine.getMainMemory().bind(mem);
		machine.reset();
	}

	private void assignMicroInstructionMemory() throws CoreException
	{
		try (InputStream mpmStream = mpmFile.getContents())
		{
			MicroInstructionMemory mpm = MicroInstructionMemoryParser
					.parseMemory(machine.getDefinition().getMicroInstructionMemoryDefinition(), mpmStream);
			machine.getMicroInstructionMemory().bind(mpm);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading MPM file", e));
		}
	}

	private void resourceChanged(IResourceChangeEvent event)
	{
		// TODO remove Sysout
		int type = event.getType();
		String typeStr;
		switch (type)
		{
		case IResourceChangeEvent.POST_BUILD:
			typeStr = "POST_BUILD";
			break;
		case IResourceChangeEvent.POST_CHANGE:
			typeStr = "POST_CHANGE";
			IResourceDelta mpmDelta;
			if ((mpmDelta = event.getDelta().findMember(mpmFile.getFullPath())) != null && (mpmDelta.getKind() & CHANGED) == CHANGED
					&& mpmFile.exists())
			{
				AtomicBoolean doHotReplace = new AtomicBoolean();
				PlatformUI.getWorkbench().getDisplay().syncExec(() ->
				{
					if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Hot Replace MPM?",
							String.format("The MPM %s has been modified on the file system. Replace simulated MPM with modified contents?",
									mpmFile.getName())))
						doHotReplace.set(true);
				});
				if (doHotReplace.get())
				{
					try
					{
						assignMicroInstructionMemory();
					}
					catch (CoreException e)
					{
						PlatformUI.getWorkbench().getDisplay()
								.asyncExec(() -> MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Failed Hot Replace!",
										"An error occurred trying to read the modified MPM from the file system: " + e.getMessage()));
					}
				}
			}
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