package net.mograsim.plugin.launch;

import static org.eclipse.core.resources.IResourceDelta.CHANGED;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockExtension;
import org.eclipse.debug.core.model.IMemoryBlockRetrievalExtension;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStepFilters;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.StandardMainMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.mi.StandardMPROM;
import net.mograsim.machine.standard.memory.AbstractAssignableBitVectorMemory;
import net.mograsim.machine.standard.memory.BitVectorBasedMemoryParser;
import net.mograsim.plugin.MograsimActivator;

public class MachineDebugTarget extends PlatformObject implements IDebugTarget, IMemoryBlockRetrievalExtension
{
	private final static boolean USE_PSEUDO_THREAD = true;

	private final ILaunch launch;
	private final Machine machine;
	private final LogicExecuter exec;
	private final MachineThread thread;
	private final IFile mpmFile;
	private final Optional<IFile> mpromFile;
	private final Optional<IFile> memFile;

	private boolean running;

	private final List<Consumer<Double>> executionSpeedListeners;

	private final IResourceChangeListener resChangedListener;

	public MachineDebugTarget(ILaunch launch, IFile mpmFile, Optional<IFile> mpromFile, Optional<IFile> memFile,
			MachineDefinition machineDefinition) throws CoreException
	{
		this.launch = launch;
		this.machine = machineDefinition.createNew();
		this.exec = new LogicExecuter(machine.getTimeline());

		this.executionSpeedListeners = new ArrayList<>();
		this.mpmFile = mpmFile;
		this.mpromFile = mpromFile;
		this.memFile = memFile;

		assignMicroInstructionMemory();
		assignMPROM();
		assignMainMemory();

		exec.startLiveExecution();
		running = true;

		getLaunch().addDebugTarget(this);
		fireCreationEvent();

		this.resChangedListener = this::resourceChanged;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resChangedListener, IResourceChangeEvent.POST_CHANGE);

		// create after creating ourself
		this.thread = USE_PSEUDO_THREAD ? new MachineThread(this) : null;
	}

	public Machine getMachine()
	{
		return machine;
	}

	@Override
	public String getName() throws DebugException
	{
		return "Mograsim machine \"" + machine.getDefinition().getId() + '"';
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return this;
	}

	@Override
	public ILaunch getLaunch()
	{
		return launch;
	}

	public double getExecutionSpeed()
	{
		return exec.getSpeedFactor();
	}

	public void setExecutionSpeed(double speed)
	{
		if (getExecutionSpeed() != speed)
		{
			exec.setSpeedFactor(speed);
			callExecutionSpeedListener(speed);
		}
	}

	@Override
	public boolean isSuspended()
	{
		return exec.isPaused();
	}

	@Override
	public boolean canSuspend()
	{
		return !isTerminated() && !isSuspended();
	}

	@Override
	public void suspend() throws DebugException
	{
		if (isTerminated())
			throwDebugException("Can't suspend a terminated MachineProcess");
		if (isSuspended())
			throwDebugException("Can't suspend a suspended MachineProcess");

		exec.pauseLiveExecution();
		fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
	}

	@Override
	public boolean canResume()
	{
		return !isTerminated() && isSuspended();
	}

	@Override
	public void resume() throws DebugException
	{
		if (isTerminated())
			throwDebugException("Can't resume a terminated MachineProcess");
		if (!isSuspended())
			throwDebugException("Can't resume a non-suspended MachineProcess");

		exec.unpauseLiveExecution();
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}

	@Override
	public boolean isTerminated()
	{
		return !running;
	}

	@Override
	public boolean canTerminate()
	{
		return !isTerminated();
	}

	@Override
	public void terminate() throws DebugException
	{
		if (isTerminated())
			return;

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resChangedListener);
		exec.stopLiveExecution();
		running = false;
		fireTerminateEvent();
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		return false;
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint)
	{
		// ignore; we don't support breakpoints
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		// ignore; we don't support breakpoints
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		// ignore; we don't support breakpoints
	}

	@Override
	public boolean isDisconnected()
	{
		return false;
	}

	@Override
	public boolean canDisconnect()
	{
		return false;
	}

	@Override
	public void disconnect() throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, DebugException.NOT_SUPPORTED,
				"Can't disconnect from a MachineDebugTarget", null));
	}

	@Override
	public boolean supportsStorageRetrieval()
	{
		return true;
	}

	@SuppressWarnings("deprecation") // TODO can we throw a DebugException instead?
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException
	{
		return new MainMemoryBlock(this, startAddress, length);
	}

	@Override
	public IMemoryBlockExtension getExtendedMemoryBlock(String expression, Object context) throws DebugException
	{
		return new MainMemoryBlockExtension(this, expression, context);
	}

	@Override
	public IProcess getProcess()
	{
		return null;
	}

	@Override
	public boolean hasThreads() throws DebugException
	{
		return USE_PSEUDO_THREAD;
	}

	@Override
	public IThread[] getThreads() throws DebugException
	{
		return thread == null ? new IThread[0] : new IThread[] { thread };
	}

	public void addExecutionSpeedListener(Consumer<Double> executionSpeedListener)
	{
		executionSpeedListeners.add(executionSpeedListener);
	}

	public void removeExecutionSpeedListener(Consumer<Double> executionSpeedListener)
	{
		executionSpeedListeners.remove(executionSpeedListener);
	}

	private void callExecutionSpeedListener(double executionSpeed)
	{
		executionSpeedListeners.forEach(l -> l.accept(executionSpeed));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter)
	{
		if (adapter == IDebugElement.class)
			return (T) this;

		// leave this here; maybe we implement IStepFilters someday
		if (adapter == IStepFilters.class)
			if (this instanceof IStepFilters)
				return (T) getDebugTarget();

		if (adapter == IDebugTarget.class)
			return (T) getDebugTarget();

		if (adapter == ILaunch.class)
			return (T) getLaunch();

		// CONTEXTLAUNCHING
		if (adapter == ILaunchConfiguration.class)
			return (T) getLaunch().getLaunchConfiguration();

		return super.getAdapter(adapter);
	}

	/**
	 * Fires a creation event for this debug element.
	 */
	private void fireCreationEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * Fires a resume for this debug element with the specified detail code.
	 *
	 * @param detail detail code for the resume event, such as <code>DebugEvent.STEP_OVER</code>
	 */
	private void fireResumeEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a suspend event for this debug element with the specified detail code.
	 *
	 * @param detail detail code for the suspend event, such as <code>DebugEvent.BREAKPOINT</code>
	 */
	private void fireSuspendEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}

	/**
	 * Fires a terminate event for this debug element.
	 */
	private void fireTerminateEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	/**
	 * Fires a debug event.
	 *
	 * @param event debug event to fire
	 */
	private static void fireEvent(DebugEvent event)
	{
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	private static void throwDebugException(String message) throws DebugException
	{
		throw new DebugException(
				new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED, message, null));
	}

	private void resourceChanged(IResourceChangeEvent event)
	{
		if (event.getType() == IResourceChangeEvent.POST_CHANGE)
		{
			tryHotReplaceIfChanged(event, mpmFile, this::assignMicroInstructionMemory, "MPM");

			if (mpromFile.isPresent())
				tryHotReplaceIfChanged(event, mpromFile.get(), this::assignMPROM, "MPROM");
		}
	}

	private static void tryHotReplaceIfChanged(IResourceChangeEvent event, IFile memFile, RunnableThrowingCoreException assign, String type)
	{
		IResourceDelta mpmDelta = event.getDelta().findMember(memFile.getFullPath());
		if (mpmDelta != null && (mpmDelta.getKind() & CHANGED) == CHANGED && memFile.exists())
			tryHotReplace(memFile, assign, type);
	}

	private static void tryHotReplace(IFile memFile, RunnableThrowingCoreException assign, String type)
	{
		AtomicBoolean doHotReplace = new AtomicBoolean();
		PlatformUI.getWorkbench().getDisplay().syncExec(() ->
		{
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Hot Replace " + type + "?",
					String.format("The " + type + " %s has been modified on the file system. Replace simulated " + type
							+ " with modified contents?", memFile.getName())))
				doHotReplace.set(true);
		});
		if (doHotReplace.get())
		{
			try
			{
				assign.run();
			}
			catch (CoreException e)
			{
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(() -> MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Failed Hot Replace!",
								"An error occurred trying to read the modified " + type + " from the file system: " + e.getMessage()));
			}
		}
	}

	private static interface RunnableThrowingCoreException
	{
		public void run() throws CoreException;
	}

	private void assignMicroInstructionMemory() throws CoreException
	{
		try (InputStream mpmStream = mpmFile.getContents())
		{
			machine.getMicroInstructionMemory().bind(
					MicroInstructionMemoryParser.parseMemory(machine.getDefinition().getMicroInstructionMemoryDefinition(), mpmStream));
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading MPM file", e));
		}
	}

	private void assignMPROM() throws CoreException
	{
		assignMemory(mpromFile, machine.getMPROM(), machine.getDefinition().getMPROMDefinition(), StandardMPROM::new, "MPROM");
	}

	private void assignMainMemory() throws CoreException
	{
		assignMemory(memFile, machine.getMainMemory(), machine.getDefinition().getMainMemoryDefinition(), StandardMainMemory::new,
				"initial RAM");
	}

	private static <D extends BitVectorMemoryDefinition, M extends BitVectorMemory> void assignMemory(Optional<IFile> memFile,
			AbstractAssignableBitVectorMemory<M> memoryToAssign, D definition, Function<D, M> newMemory, String type) throws CoreException
	{
		if (memFile.isPresent())
		{
			try (InputStream initialRAMStream = memFile.get().getContents())
			{
				M mem = newMemory.apply(definition);
				BitVectorBasedMemoryParser.parseMemory(mem, initialRAMStream);
				memoryToAssign.bind(mem);
			}
			catch (IOException e)
			{
				throw new CoreException(
						new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Unexpected IO exception reading " + type + " file", e));
			}
		}
	}

	public IFile getMPMFile()
	{
		return mpmFile;
	}

	public Optional<IFile> getMPROMFile()
	{
		return mpromFile;
	}

	public Optional<IFile> getMEMFile()
	{
		return memFile;
	}
}