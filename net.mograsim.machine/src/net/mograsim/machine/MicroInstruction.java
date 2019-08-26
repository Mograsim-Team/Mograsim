package net.mograsim.machine;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.Mnemonic;

public interface MicroInstruction {
	
	public MicroInstructionParameter getParameter(int index);
	
	/**
	 * @return The amount of {@link Mnemonic}s, the instruction is composed of
	 */
	public int getSize();
}
