package net.mograsim.plugin.launch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.statushandlers.StatusManager;

import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.mi.MPROMDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.standard.memory.BitVectorBasedMemoryParser;
import net.mograsim.machine.standard.memory.StandardBitVectorMemory;
import net.mograsim.plugin.MograsimActivator;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.MograsimNature;
import net.mograsim.plugin.nature.ProjectMachineContext;

public class MachineLaunchConfigType extends LaunchConfigurationDelegate
{
	public static final String PROJECT_ATTR = MograsimActivator.PLUGIN_ID + ".project";
	public static final String MPM_FILE_ATTR = MograsimActivator.PLUGIN_ID + ".mpm";
	public static final String MPROM_FILE_ATTR = MograsimActivator.PLUGIN_ID + ".mprom";
	public static final String INITIAL_RAM_FILE_ATTR = MograsimActivator.PLUGIN_ID + ".initialram";

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
		MPROMDefinition mpromDef = machineDefinition.getMPROMDefinition();

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

		String mpromFileName = configuration.getAttribute(MPROM_FILE_ATTR, "");
		if (!checkMemoryFileReadable(project, mpromDef, mpromFileName, "MPROM"))
			return false;

		String initialRAMFileName = configuration.getAttribute(INITIAL_RAM_FILE_ATTR, "");
		if (!checkMemoryFileReadable(project, mainMemDef, initialRAMFileName, "initial RAM"))
			return false;

		return super.preLaunchCheck(configuration, mode, monitor);
	}

	private static boolean checkMemoryFileReadable(IProject project, BitVectorMemoryDefinition mainMemDef, String fileName, String fileType)
			throws CoreException
	{
		if (!"".equals(fileName))
		{
			IFile initialRAMFile = project.getFile(fileName);
			if (initialRAMFile == null || !initialRAMFile.isAccessible())
				return showErrorAndReturnFalse(fileType + " file not accessible");

			try (InputStream initialRAMStream = initialRAMFile.getContents())
			{
				BitVectorBasedMemoryParser.parseMemory(new StandardBitVectorMemory<>(mainMemDef), initialRAMStream);
			}
			catch (IOException e)
			{
				throw new CoreException(
						new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading " + fileType + " file", e));
			}
		}
		return true;
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

		IFile mpmFile = project.getFile(configuration.getAttribute(MPM_FILE_ATTR, ""));
		Optional<IFile> mpromFile = fileOptional(project, configuration.getAttribute(MPROM_FILE_ATTR, ""));
		Optional<IFile> memFile = fileOptional(project, configuration.getAttribute(INITIAL_RAM_FILE_ATTR, ""));
		MachineDebugTarget debugTarget = new MachineDebugTarget(launch, mpmFile, mpromFile, memFile, machineDefinition);
		// TODO make selectable whether the machine starts paused or not
		debugTarget.suspend();
		debugTarget.setExecutionSpeed(1);
		Machine machine = debugTarget.getMachine();
		machine.reset();

		// Add the default Mograsim memory block to make it less confusing and more comfortable.
		DebugPlugin.getDefault().getMemoryBlockManager()
				.addMemoryBlocks(new IMemoryBlock[] { new MainMemoryBlockExtension(debugTarget, "0", null) });
	}

	private static Optional<IFile> fileOptional(IProject project, String filename)
	{
		if ("".equals(filename))
			return Optional.empty();
		return Optional.of(project.getFile(filename));
	}

}