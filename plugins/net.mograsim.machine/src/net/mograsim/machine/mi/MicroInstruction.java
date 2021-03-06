package net.mograsim.machine.mi;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.Mnemonic;

public interface MicroInstruction
{

	public MicroInstructionParameter getParameter(int index);

	public MicroInstructionParameter[] getParameters();

	/**
	 * @return The amount of {@link Mnemonic}s, the instruction is composed of
	 */
	public int getSize();

	public static MicroInstruction create(MicroInstructionParameter... parameters)
	{
		return new StandardMicroInstruction(parameters);
	}

	default BitVector toBitVector()
	{
		BitVector vector = BitVector.of();
		int size = getSize();
		for (int i = 0; i < size; i++)
			vector = vector.concat(getParameter(i).getValue());
		return vector;
	}
}
