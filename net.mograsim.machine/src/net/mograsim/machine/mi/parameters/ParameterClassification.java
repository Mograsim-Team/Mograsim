package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public interface ParameterClassification
{
	/**
	 * Determines whether a {@link MicroInstructionParameter} is part of this class of parameters.
	 * @return true if the classification contains the Parameter, false otherwise
	 */
	public boolean conforms(MicroInstructionParameter param);
	
	/**
	 * @return The type of the parameters in this classification.
	 */
	public ParameterType getExpectedType();
}
