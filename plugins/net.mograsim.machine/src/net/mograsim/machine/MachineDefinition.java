package net.mograsim.machine;

import java.util.Set;

import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public interface MachineDefinition
{

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
	 * Returns a set of all {@link Register}s present in the machine.
	 * 
	 * @return all registers present in the machine.
	 * @author Christian Femers
	 */
	Set<Register> getRegisters();

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
