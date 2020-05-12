package net.mograsim.logic.model.am2900.machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mograsim.logic.model.am2900.machine.registers.InstrRegister;
import net.mograsim.logic.model.am2900.machine.registers.PCRegister;
import net.mograsim.logic.model.am2900.machine.registers.muInstrRegister;
import net.mograsim.logic.model.am2900.machine.registers.am2901.Am2901RegisterGroup;
import net.mograsim.logic.model.am2900.machine.registers.am2904.Am2904RegisterGroup;
import net.mograsim.logic.model.am2900.machine.registers.am2910.Am2910RegisterGroup;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.machine.ISASchema;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.registers.Register;
import net.mograsim.machine.registers.RegisterGroup;

//we can't use the Singleton pattern here because a MachineDefinition needs a public parameterless constructor
//(used for detecting installed machines in plugin.core)
public class AbstractAm2900MachineDefinition implements MachineDefinition
{
	public static final String SIMPLE_AM2900_MACHINE_ID = "Am2900Simple";
	public static final String STRICT_AM2900_MACHINE_ID = "Am2900Strict";
	public static final String SIMPLE_AM2900_DESCRIPTION = "Am2900Simple\nTODO Description";
	public static final String STRICT_AM2900_DESCRIPTION = "Am2900Strict\nTODO Description";

	public static final List<Register> unsortedRegisters;
	public static final List<RegisterGroup> registerGroups;

	static
	{
		List<Register> unsortedRegistersModifiable = new ArrayList<>();
		unsortedRegistersModifiable.add(muInstrRegister.instance);
		unsortedRegistersModifiable.add(InstrRegister.instance);
		unsortedRegistersModifiable.add(PCRegister.instance);
		unsortedRegisters = Collections.unmodifiableList(unsortedRegistersModifiable);
		List<RegisterGroup> registerGroupsModifiable = new ArrayList<>();
		registerGroupsModifiable.add(Am2901RegisterGroup.instance);
		registerGroupsModifiable.add(Am2904RegisterGroup.instance);
		registerGroupsModifiable.add(Am2910RegisterGroup.instance);
		registerGroups = Collections.unmodifiableList(registerGroupsModifiable);
	}

	public final boolean strict;

	protected AbstractAm2900MachineDefinition(boolean strict)
	{
		this.strict = strict;
	}

	@Override
	public String getId()
	{
		return strict ? STRICT_AM2900_MACHINE_ID : SIMPLE_AM2900_MACHINE_ID;
	}

	@Override
	public String getDescription()
	{
		return strict ? STRICT_AM2900_DESCRIPTION : SIMPLE_AM2900_DESCRIPTION;
	}

	@Override
	public Am2900Machine createNew()
	{
		return createNew(new LogicModelModifiable());
	}

	public Am2900Machine createNew(LogicModelModifiable model)
	{
		return new Am2900Machine(model, this);
	}

	@Override
	public ISASchema getISASchema()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Register> getUnsortedRegisters()
	{
		return unsortedRegisters;
	}

	@Override
	public List<RegisterGroup> getRegisterGroups()
	{
		return registerGroups;
	}

	@Override
	public int getAddressBits()
	{
		return 16;
	}

	@Override
	public Am2900MainMemoryDefinition getMainMemoryDefinition()
	{
		return Am2900MainMemoryDefinition.instance;
	}

	@Override
	public int hashCode()
	{
		return strict ? 12345 : 54321;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof AbstractAm2900MachineDefinition
				&& ((AbstractAm2900MachineDefinition) obj).strict == this.strict;
	}

	@Override
	public Am2900MicroInstructionMemoryDefinition getMicroInstructionMemoryDefinition()
	{
		return Am2900MicroInstructionMemoryDefinition.instance;
	}

}
