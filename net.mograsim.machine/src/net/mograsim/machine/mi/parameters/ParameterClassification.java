package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public interface ParameterClassification
{
	/**
	 * Determines whether a {@link MicroInstructionParameter} is part of this class of parameters.
	 * @return true if the classification contains the Parameter, false otherwise
	 */
	public default boolean conforms(MicroInstructionParameter param)
	{
		return param.getType().equals(getExpectedType()) && param.getValue().length() == getExpectedBits();
	}
	
	/**
	 * @return The type of the parameters in this classification.
	 */
	public ParameterType getExpectedType();
	
	/**
	 * @return The number of bits of the parameters in this classification.
	 */
	public int getExpectedBits();
}
