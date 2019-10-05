package net.mograsim.plugin.launch;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import net.mograsim.machine.Machine;
import net.mograsim.plugin.MograsimActivator;

public class MachineThread extends PlatformObject implements IThread
{
	private final MachineDebugTarget debugTarget;
	private final MachineStackFrame stackFrame;

	public MachineThread(MachineDebugTarget debugTarget)
	{
		this.debugTarget = debugTarget;
		this.stackFrame = new MachineStackFrame(this);

		DebugPlugin.getDefault().addDebugEventListener(es -> Arrays.stream(es).filter(e -> e.getSource() == debugTarget).forEach(e ->
		{
			switch (e.getKind())
			{
			case DebugEvent.RESUME:
				fireResumeEvent(e.getDetail());
				break;
			case DebugEvent.SUSPEND:
				fireSuspendEvent(e.getDetail());
				break;
			default:
			}
		}));

		fireCreationEvent();
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	public Machine getMachine()
	{
		return debugTarget.getMachine();
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return debugTarget;
	}

	@Override
	public ILaunch getLaunch()
	{
		return debugTarget.getLaunch();
	}

	@Override
	public boolean canResume()
	{
		return debugTarget.canResume();
	}

	@Override
	public boolean canSuspend()
	{
		return debugTarget.canSuspend();
	}

	@Override
	public boolean isSuspended()
	{
		return debugTarget.isSuspended();
	}

	@Override
	public void resume() throws DebugException
	{
		debugTarget.resume();
	}

	@Override
	public void suspend() throws DebugException
	{
		debugTarget.suspend();
	}

	@Override
	public boolean canStepInto()
	{
		return false;
	}

	// TODO step over sounds like single-step execution...
	@Override
	public boolean canStepOver()
	{
		return false;
	}

	@Override
	public boolean canStepReturn()
	{
		return false;
	}

	@Override
	public boolean isStepping()
	{
		return false;
	}

	@Override
	public void stepInto() throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Can't step into for a machine thread"));
	}

	@Override
	public void stepOver() throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Can't step over for a machine thread"));
	}

	@Override
	public void stepReturn() throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "Can't step return for a machine thread"));
	}

	@Override
	public boolean canTerminate()
	{
		return debugTarget.canTerminate();
	}

	@Override
	public boolean isTerminated()
	{
		return debugTarget.isTerminated();
	}

	@Override
	public void terminate() throws DebugException
	{
		debugTarget.terminate();
	}

	@Override
	public IStackFrame[] getStackFrames() throws DebugException
	{
		return isSuspended() ? new IStackFrame[] { stackFrame } : new IStackFrame[0];
	}

	@Override
	public boolean hasStackFrames() throws DebugException
	{
		// required by Eclipse: see javadoc for org.eclipse.debug.core.DebugEvent
		return isSuspended();
	}

	@Override
	public int getPriority() throws DebugException
	{
		return 0;
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException
	{
		return stackFrame;
	}

	@Override
	public String getName() throws DebugException
	{
		return "pseudo thread";
	}

	@Override
	public IBreakpoint[] getBreakpoints()
	{
		return new IBreakpoint[0];
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
	 * Fires a debug event.
	 *
	 * @param event debug event to fire
	 */
	private static void fireEvent(DebugEvent event)
	{
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}
}