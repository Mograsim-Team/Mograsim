package net.mograsim.plugin.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.resources.IMarkerDelta;
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

import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.plugin.MograsimActivator;
import net.mograsim.plugin.launch.MachineLaunchConfigType.MachineLaunchParams;

public class MachineDebugTarget extends PlatformObject implements IDebugTarget, IMemoryBlockRetrievalExtension
{
	private final ILaunch launch;
	private final Machine machine;
	private final LogicExecuter exec;

	private boolean running;

	private final List<Consumer<Double>> executionSpeedListeners;

	private final MachineLaunchParams launchParams;

	public MachineDebugTarget(ILaunch launch, MachineLaunchParams launchParams, MachineDefinition machineDefinition)
	{
		this.launch = launch;
		this.machine = machineDefinition.createNew();
		this.exec = new LogicExecuter(machine.getTimeline());

		this.executionSpeedListeners = new ArrayList<>();
		this.launchParams = launchParams;

		exec.startLiveExecution();
		running = true;

		getLaunch().addDebugTarget(this);
		fireCreationEvent();
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

	public MachineLaunchParams getLaunchParams()
	{
		return launchParams;
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
		return false;
	}

	@Override
	public IThread[] getThreads() throws DebugException
	{
		return new IThread[0];
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
}