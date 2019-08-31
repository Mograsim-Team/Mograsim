package net.mograsim.machine.mi;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.Mnemonic;

public interface MicroInstruction {
	
	public MicroInstructionParameter getParameter(int index);
	public void setParameter(int index, MicroInstructionParameter param);
	
	/**
	 * @return The amount of {@link Mnemonic}s, the instruction is composed of
	 */
	public int getSize();
	
	public static MicroInstruction create(MicroInstructionParameter... parameters)
	{
		return new StandardMicroInstruction(parameters);
	}
}
