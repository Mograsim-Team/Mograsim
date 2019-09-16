package net.mograsim.machine.mi;

import java.util.Arrays;
import java.util.Optional;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public interface MicroInstructionDefinition
{
	/**
	 * @return The {@link ParameterClassification}s of which a MicroInstruction is composed.
	 */
	public ParameterClassification[] getParameterClassifications();

	/**
	 * @throws IndexOutOfBoundsException
	 */
	public ParameterClassification getParameterClassification(int index);

	/**
	 * @return The amount of {@link MicroInstructionParameter}s in a {@link MicroInstruction} that follows this definition.
	 */
	public default int size()
	{
		return getParameterClassifications().length;
	}

	/**
	 * @return The amount of {@link Bit}s in a {@link MicroInstruction} that follows this definition.
	 */
	public default int sizeInBits()
	{
		return Arrays.stream(getParameterClassifications()).mapToInt(e -> e.getExpectedBits()).reduce(0, (a, b) -> a + b);
	}

	public default MicroInstruction createDefaultInstruction()
	{
		int size = size();
		MicroInstructionParameter[] params = new MicroInstructionParameter[size];
		ParameterClassification[] classes = getParameterClassifications();
		for (int i = 0; i < size; i++)
		{
			ParameterClassification classification = classes[i];
			params[i] = classification.getDefault();
		}
		return new StandardMicroInstruction(params);
	}

	public Optional<String> getParameterDescription(int index);
}