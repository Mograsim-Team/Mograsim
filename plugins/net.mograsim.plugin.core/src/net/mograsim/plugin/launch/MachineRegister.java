package net.mograsim.plugin.launch;

import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.swt.SWT;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.Machine;
import net.mograsim.machine.registers.Register;
import net.mograsim.plugin.MograsimActivator;

public class MachineRegister extends PlatformObject implements IRegister
{
	private final MachineRegisterGroup registerGroup;
	private final Register machineRegister;

	private final Consumer<BitVector> registerListener;

	public MachineRegister(MachineRegisterGroup registerGroup, Register machineRegister)
	{
		this.registerGroup = registerGroup;
		this.machineRegister = machineRegister;

		this.registerListener = v -> fireChangeEvent();
		getMachine().addRegisterListener(machineRegister, registerListener);

		DebugPlugin.getDefault().addDebugEventListener(es -> Arrays.stream(es).filter(e -> e.getKind() == DebugEvent.TERMINATE).filter(e ->
		{
			Object source = e.getSource();
			if (!(source instanceof IDebugElement))
				return false;
			return ((IDebugElement) source).getDebugTarget() == getDebugTarget();
		}).forEach(e -> getMachine().removeRegisterListener(machineRegister, registerListener)));
	}

	public Machine getMachine()
	{
		return registerGroup.getMachine();
	}

	@Override
	public IValue getValue() throws DebugException
	{
		return new MachineValue(this);
	}

	@Override
	public String getName() throws DebugException
	{
		return machineRegister.id();// TODO name
	}

	@Override
	public String getReferenceTypeName() throws DebugException
	{
		return "BitVector";
	}

	@Override
	public boolean hasValueChanged() throws DebugException
	{
		// TODO
		return false;
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return registerGroup.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch()
	{
		return registerGroup.getLaunch();
	}

	public String getValueString()
	{
		// TODO view in hex
		return getMachine().getRegister(machineRegister).toString();
	}

	@Override
	public void setValue(String expression) throws DebugException
	{
		// TODO support hex
		// TODO exception handling
		getMachine().setRegister(machineRegister, BitVector.parse(expression));
	}

	@Override
	public void setValue(IValue value) throws DebugException
	{
		if (!"Bitvector".equals(value.getReferenceTypeName()))
			throw new DebugException(new Status(SWT.ERROR, MograsimActivator.PLUGIN_ID, ""));
		setValue(value.getValueString());
	}

	@Override
	public boolean supportsValueModification()
	{
		return true;
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException
	{
		// TODO do this prettier; also check length too
		try
		{
			BitVector.parse(expression);
		}
		catch (@SuppressWarnings("unused") Exception e)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException
	{
		return verifyValue(value.getValueString());
	}

	@Override
	public IRegisterGroup getRegisterGroup() throws DebugException
	{
		return registerGroup;
	}

	/**
	 * Fires a change event for this debug element.
	 */
	private void fireChangeEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
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