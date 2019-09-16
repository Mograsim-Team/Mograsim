package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;

public interface MicroInstructionParameter
{
	public BitVector getValue();

	public ParameterType getType();

	public static enum ParameterType
	{
		INTEGER_IMMEDIATE, BOOLEAN_IMMEDIATE, MNEMONIC
	}
}
