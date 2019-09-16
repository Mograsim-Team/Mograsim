package net.mograsim.machine.isa;

import java.util.Objects;

public final class AsmOperation
{
	private final String mnemonic;

	public AsmOperation(String mnemonic)
	{
		this.mnemonic = Objects.requireNonNull(mnemonic.toLowerCase());
	}

	public String getMnemonic()
	{
		return mnemonic;
	}

	@Override
	public String toString()
	{
		return getMnemonic();
	}

	@Override
	public int hashCode()
	{
		return mnemonic.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof AsmOperation))
			return false;
		AsmOperation other = (AsmOperation) obj;
		return mnemonic.equals(other.mnemonic);
	}
}
