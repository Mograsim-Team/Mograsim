package net.mograsim.plugin.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.ISuspendResume;

import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.plugin.MograsimActivator;

public class MachineProcess extends PlatformObject implements IProcess, ISuspendResume
{
	private final ILaunch launch;
	private final Machine machine;
	private final LogicExecuter exec;

	private boolean running;

	public MachineProcess(ILaunch launch, MachineDefinition machineDefinition)
	{
		this.launch = launch;
		this.machine = machineDefinition.createNew();
		this.exec = new LogicExecuter(machine.getTimeline());

		exec.startLiveExecution();
		running = true;

		launch.addProcess(this);
		fireCreationEvent();
	}

	public Machine getMachine()
	{
		return machine;
	}

	@Override
	public ILaunch getLaunch()
	{
		return launch;
	}

	public String getName()
	{
		return "Mograsim machine \"" + machine.getDefinition().getId() + '"';
	}

	@Override
	public String getLabel()
	{
		return getName() + " (" + getStatusString() + ')';
	}

	public String getStatusString()
	{
		return isTerminated() ? "terminated" : isSuspended() ? "paused" : "running";
	}

	public void setExecutionSpeed(double speed)
	{
		exec.setSpeedFactor(speed);
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
	public int getExitValue() throws DebugException
	{
		if (!isTerminated())
			throwDebugException("Can't get the exit value of a running process");

		return 0;
	}

	@Override
	public IStreamsProxy getStreamsProxy()
	{
		return null;
	}

	private Map<String, String> attributes;

	@Override
	public void setAttribute(String key, String value)
	{
		if (attributes == null)
			attributes = new HashMap<>(5);
		Object origVal = attributes.get(key);
		if (origVal != null && origVal.equals(value))
			return;
		attributes.put(key, value);
		fireChangeEvent();
	}

	@Override
	public String getAttribute(String key)
	{
		if (attributes == null)
			return null;
		return attributes.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter)
	{
		if (adapter.equals(IProcess.class))
			return (T) this;

		if (adapter.equals(IDebugTarget.class))
		{
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++)
				if (this.equals(targets[i].getProcess()))
					return (T) targets[i];
			return null;
		}

		if (adapter.equals(ILaunch.class))
			return (T) getLaunch();

		if (adapter.equals(ILaunchConfiguration.class))
			return (T) getLaunch().getLaunchConfiguration();

		return super.getAdapter(adapter);
	}

	/**
	 * Fires a creation event.
	 */
	private void fireCreationEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * Fires a change event.
	 */
	private void fireChangeEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
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
	 * Fires a terminate event.
	 */
	private void fireTerminateEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	/**
	 * Fires the given debug event.
	 *
	 * @param event debug event to fire
	 */
	private static void fireEvent(DebugEvent event)
	{
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null)
			manager.fireDebugEventSet(new DebugEvent[] { event });
	}

	private static void throwDebugException(String message) throws DebugException
	{
		throw new DebugException(
				new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED, message, null));
	}
}