package net.mograsim.plugin.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.registers.RegisterGroup;
import net.mograsim.plugin.MograsimActivator;

public class MachineStackFrame extends PlatformObject implements IStackFrame
{
	private final MachineThread thread;
	private final List<MachineRegisterGroup> registerGroups;

	public MachineStackFrame(MachineThread thread)
	{
		this.thread = thread;
		List<MachineRegisterGroup> registerGroupsModifiable = new ArrayList<>();
		MachineDefinition machDef = getMachine().getDefinition();
		if (!machDef.getUnsortedRegisters().isEmpty())
			registerGroupsModifiable.add(new MachineRegisterGroup(this, "<unsorted>", machDef.getUnsortedRegisters()));
		addRegisterGroups(null, registerGroupsModifiable, machDef.getRegisterGroups());
		this.registerGroups = Collections.unmodifiableList(registerGroupsModifiable);
	}

	private void addRegisterGroups(String base, List<MachineRegisterGroup> target, List<RegisterGroup> registerGroups)
	{
		for (RegisterGroup rg : registerGroups)
		{
			String name = (base == null ? "" : base + '.') + rg.id();
			target.add(new MachineRegisterGroup(this, name, rg.getRegisters()));
			addRegisterGroups(name, target, rg.getSubGroups());
		}
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	public Machine getMachine()
	{
		return thread.getMachine();
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return thread.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch()
	{
		return thread.getLaunch();
	}

	@Override
	public boolean canStepInto()
	{
		return thread.canStepInto();
	}

	@Override
	public boolean canStepOver()
	{
		return thread.canStepOver();
	}

	@Override
	public boolean canStepReturn()
	{
		return thread.canStepReturn();
	}

	@Override
	public boolean isStepping()
	{
		return thread.isStepping();
	}

	@Override
	public void stepInto() throws DebugException
	{
		thread.stepInto();
	}

	@Override
	public void stepOver() throws DebugException
	{
		thread.stepOver();
	}

	@Override
	public void stepReturn() throws DebugException
	{
		thread.stepReturn();
	}

	@Override
	public boolean canResume()
	{
		return thread.canResume();
	}

	@Override
	public boolean canSuspend()
	{
		return thread.canSuspend();
	}

	@Override
	public boolean isSuspended()
	{
		return thread.isSuspended();
	}

	@Override
	public void resume() throws DebugException
	{
		thread.resume();
	}

	@Override
	public void suspend() throws DebugException
	{
		thread.suspend();
	}

	@Override
	public boolean canTerminate()
	{
		return thread.canTerminate();
	}

	@Override
	public boolean isTerminated()
	{
		return thread.isTerminated();
	}

	@Override
	public void terminate() throws DebugException
	{
		thread.terminate();
	}

	@Override
	public IThread getThread()
	{
		return thread;
	}

	@Override
	public IVariable[] getVariables() throws DebugException
	{
		return new IVariable[0];
	}

	@Override
	public boolean hasVariables() throws DebugException
	{
		return false;
	}

	@Override
	public int getLineNumber() throws DebugException
	{
		// TODO could we transmit the active microinstruction address using this?
		return -1;
	}

	@Override
	public int getCharStart() throws DebugException
	{
		return -1;
	}

	@Override
	public int getCharEnd() throws DebugException
	{
		return -1;
	}

	@Override
	public String getName() throws DebugException
	{
		return "pseudo stack frame";
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return registerGroups.toArray(IRegisterGroup[]::new);
	}

	@Override
	public boolean hasRegisterGroups() throws DebugException
	{
		return true;
	}
}