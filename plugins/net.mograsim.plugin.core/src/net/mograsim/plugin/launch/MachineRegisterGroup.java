package net.mograsim.plugin.launch;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import net.mograsim.machine.Machine;
import net.mograsim.machine.registers.Register;
import net.mograsim.plugin.MograsimActivator;

public class MachineRegisterGroup extends PlatformObject implements IRegisterGroup
{
	private final MachineStackFrame stackFrame;
	private final String name;
	private final List<MachineRegister> registers;

	public MachineRegisterGroup(MachineStackFrame stackFrame, String name, List<Register> registers)
	{
		this.stackFrame = stackFrame;
		this.name = name;
		this.registers = registers.stream().map(r -> new MachineRegister(this, r)).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	public Machine getMachine()
	{
		return stackFrame.getMachine();
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return stackFrame.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch()
	{
		return stackFrame.getLaunch();
	}

	@Override
	public String getName() throws DebugException
	{
		return name;
	}

	@Override
	public IRegister[] getRegisters() throws DebugException
	{
		return registers.toArray(IRegister[]::new);
	}

	@Override
	public boolean hasRegisters() throws DebugException
	{
		return hasRegistersSafe();
	}

	public boolean hasRegistersSafe()
	{
		return registers.size() > 0;
	}
}