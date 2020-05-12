package net.mograsim.machine;

import java.util.List;

import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;
import net.mograsim.machine.registers.Register;
import net.mograsim.machine.registers.RegisterGroup;

public interface MachineDefinition
{
	/**
	 * This returns the MachineDefinitions ID. This must be consistent and coherent with the id in the extension point (Eclipse plugin xml)
	 * providing the definition.
	 * 
	 * @return a human readable, unique id representing the specified machine.
	 * @author Christian Femers
	 */
	String getId();

	/**
	 * Returns a human-readable description of the machine definition.
	 * 
	 * @author Daniel Kirschten
	 */
	String getDescription();

	/**
	 * Creates a new instance of the machine
	 * 
	 * @return a new object of an {@link Machine} implementation.
	 * @author Christian Femers
	 */
	Machine createNew();

	/**
	 * Returns the schema that all possible ISAs (Instruction Set Architecture) must be based on.
	 * 
	 * @return an {@link ISASchema} implementation fitting the machine
	 * @author Christian Femers
	 */
	ISASchema getISASchema();

	/**
	 * Returns a set of all {@link Register}s present in the machine that are not part of a register group.
	 * 
	 * @return all registers present in the machine.
	 * @author Christian Femers
	 */
	List<Register> getUnsortedRegisters();

	/**
	 * Returns a set of all RegisterGroups that the machine contains
	 * 
	 * @return all register groups present in the machine.
	 * @author Christian Femers
	 */
	List<RegisterGroup> getRegisterGroups();

	/**
	 * The number of bits used by the machine to address main memory. Note that this should be the number of bits used in the CPU, not a
	 * possibly different one used by the bus system.
	 * 
	 * @return the number of bits used by the CPU
	 * @see MainMemoryDefinition#getMemoryAddressBits()
	 * @author Christian Femers
	 */
	int getAddressBits();

	/**
	 * Returns the definition of the machines main memory.
	 * 
	 * @return the {@link MainMemoryDefinition} that defines the main memory.
	 * @author Christian Femers
	 */
	MainMemoryDefinition getMainMemoryDefinition();

	/**
	 * Returns the definition of the machines instruction memory.
	 * 
	 * @return the {@link InstructionMemoryDefinition} that defines the instruction memory.
	 */
	MicroInstructionMemoryDefinition getMicroInstructionMemoryDefinition();

}
