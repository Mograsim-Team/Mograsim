package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;

public interface MicroInstructionParameter
{
	public BitVector getValue();

	public ParameterType getType();

	public boolean isDefault();

	public static enum ParameterType
	{
		INTEGER_IMMEDIATE, BOOLEAN_IMMEDIATE, MNEMONIC
	}
}
