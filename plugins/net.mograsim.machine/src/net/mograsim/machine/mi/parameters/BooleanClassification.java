package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class BooleanClassification extends MnemonicFamily
{
	String trueName, falseName;

	public BooleanClassification(boolean defaultValue, String trueName, String falseName)
	{
		super(defaultValue ? trueName : falseName, new MnemonicPair(trueName, BitVector.SINGLE_1),
				new MnemonicPair(falseName, BitVector.SINGLE_0));
		this.trueName = trueName;
		this.falseName = falseName;
	}

	public BooleanClassification(String trueName, String falseName)
	{
		super("X", new MnemonicPair("X", BitVector.SINGLE_0), new MnemonicPair(trueName, BitVector.SINGLE_1),
				new MnemonicPair(falseName, BitVector.SINGLE_0));
		this.trueName = trueName;
		this.falseName = falseName;
	}

	public Mnemonic get(boolean value)
	{
		return get(value ? trueName : falseName);
	}

	@Override
	public ParameterType getExpectedType()
	{
		return ParameterType.BOOLEAN_IMMEDIATE;
	}

	@Override
	public int getExpectedBits()
	{
		return 1;
	}
}
