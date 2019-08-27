package net.mograsim.machine;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public interface MicroInstructionDefinition
{
	/**
	 * @return The {@link ParameterClassification}s of which a MicroInstruction is composed.
	 */
	public ParameterClassification[] getParameterClassifications();
	
	/**
	 * @return The amount of {@link MicroInstructionParameter}s in a {@link MicroInstruction} that follows this definition.
	 */
	public default int size()
	{
		return getParameterClassifications().length;
	}
	
}
