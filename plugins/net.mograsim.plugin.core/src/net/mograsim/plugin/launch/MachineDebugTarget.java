package net.mograsim.plugin.launch;

import java.util.Arrays;

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

import net.mograsim.machine.Machine;
import net.mograsim.plugin.MograsimActivator;

public class MachineDebugTarget extends PlatformObject implements IDebugTarget, IMemoryBlockRetrievalExtension
{
	private final MachineProcess process;

	public MachineDebugTarget(MachineProcess process)
	{
		this.process = process;

		DebugPlugin.getDefault().addDebugEventListener(es -> Arrays.stream(es).filter(e -> e.getSource() == process).forEach(e ->
		{
			switch (e.getKind())
			{
			case DebugEvent.RESUME:
				fireResumeEvent(e.getDetail());
				break;
			case DebugEvent.SUSPEND:
				fireSuspendEvent(e.getDetail());
				break;
			case DebugEvent.TERMINATE:
				fireTerminateEvent();
				break;
			default:
				// ignore
			}
		}));

		getLaunch().addDebugTarget(this);
		fireCreationEvent();
	}

	public Machine getMachine()
	{
		return process.getMachine();
	}

	@Override
	public String getName() throws DebugException
	{
		return process.getName();
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
		return process.getLaunch();
	}

	public void setExecutionSpeed(double speed)
	{
		process.setExecutionSpeed(speed);
	}

	@Override
	public boolean isSuspended()
	{
		return process.isSuspended();
	}

	@Override
	public boolean canSuspend()
	{
		return process.canSuspend();
	}

	@Override
	public void suspend() throws DebugException
	{
		process.suspend();
	}

	@Override
	public boolean canResume()
	{
		return process.canResume();
	}

	@Override
	public void resume() throws DebugException
	{
		process.resume();
	}

	@Override
	public boolean isTerminated()
	{
		return process.isTerminated();
	}

	@Override
	public boolean canTerminate()
	{
		return process.canTerminate();
	}

	@Override
	public void terminate() throws DebugException
	{
		process.terminate();
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
	public MachineProcess getProcess()
	{
		return process;
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

		if (adapter == IProcess.class)
			return (T) getProcess();

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
}