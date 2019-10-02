package net.mograsim.plugin.launch;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import net.mograsim.machine.Machine;
import net.mograsim.machine.Register;
import net.mograsim.plugin.MograsimActivator;

public class MachineRegisterGroup extends PlatformObject implements IRegisterGroup
{
	private final MachineStackFrame stackFrame;
	private final List<MachineRegister> registers;

	public MachineRegisterGroup(MachineStackFrame stackFrame)
	{
		this.stackFrame = stackFrame;
		Set<Register> machineRegisters = getMachine().getDefinition().getRegisters();
		List<MachineRegister> registersModifiable = machineRegisters.stream().map(r -> new MachineRegister(this, r))
				.collect(Collectors.toList());
		this.registers = Collections.unmodifiableList(registersModifiable);
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
		return "pseudo register group";
	}

	@Override
	public IRegister[] getRegisters() throws DebugException
	{
		// TODO sort
		return registers.toArray(IRegister[]::new);
	}

	@Override
	public boolean hasRegisters() throws DebugException
	{
		return true;
	}
}