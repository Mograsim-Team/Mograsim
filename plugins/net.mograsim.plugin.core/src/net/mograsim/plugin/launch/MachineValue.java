package net.mograsim.plugin.launch;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import net.mograsim.machine.Machine;
import net.mograsim.plugin.MograsimActivator;

public class MachineValue extends PlatformObject implements IValue
{
	private final MachineRegister register;

	public MachineValue(MachineRegister register)
	{
		this.register = register;
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	public Machine getMachine()
	{
		return register.getMachine();
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return register.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch()
	{
		return register.getLaunch();
	}

	@Override
	public String getReferenceTypeName() throws DebugException
	{
		return register.getReferenceTypeName();
	}

	@Override
	public String getValueString() throws DebugException
	{
		return register.getValueString();
	}

	@Override
	public boolean isAllocated() throws DebugException
	{
		return false;
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
}