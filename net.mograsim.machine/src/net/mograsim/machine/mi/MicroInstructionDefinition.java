package net.mograsim.machine.mi;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
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

	public default MicroInstruction createDefaultInstruction(Runnable updateCallback)
	{
		int size = size();
		MicroInstructionParameter[] params = new MicroInstructionParameter[size];
		ParameterClassification[] classes = getParameterClassifications();
		for (int i = 0; i < size; i++)
		{
			MicroInstructionParameter newParam;
			ParameterClassification classification = classes[i];
			ParameterType type = classification.getExpectedType();
			switch (type)
			{
			case BOOLEAN_IMMEDIATE:
			case MNEMONIC:
				newParam = ((MnemonicFamily) classification).get(0);
				break;
			case INTEGER_IMMEDIATE:
				newParam = new IntegerImmediate(BigInteger.valueOf(0), ((IntegerClassification) classification).getExpectedBits());
				break;
			default:
				throw new IllegalStateException("Unknown ParameterType " + type);
			}
			params[i] = newParam;
		}
		return new StandardMicroInstruction(updateCallback, params);
	}

	public Optional<String> getParameterDescription(int index);
}