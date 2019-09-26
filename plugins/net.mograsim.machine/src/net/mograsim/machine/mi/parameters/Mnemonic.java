package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;

public final class Mnemonic implements MicroInstructionParameter
{
	private final String text;
	private final BitVector vector;
	final MnemonicFamily owner;
	private final int ordinal;

	Mnemonic(String text, BitVector vector, MnemonicFamily owner, int ordinal)
	{
		super();
		this.text = text;
		this.vector = vector;
		this.owner = owner;
		this.ordinal = ordinal;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public BitVector getValue()
	{
		return vector;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((vector == null) ? 0 : vector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}

	@Override
	public ParameterType getType()
	{
		return owner.getExpectedType();
	}

	public int ordinal()
	{
		return ordinal;
	}

	@Override
	public String toString()
	{
		return text;
	}

	@Override
	public boolean isDefault()
	{
		return equals(owner.getDefault());
	}
}
