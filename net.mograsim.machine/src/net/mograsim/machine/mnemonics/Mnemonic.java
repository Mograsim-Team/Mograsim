package net.mograsim.machine.mnemonics;

import net.mograsim.logic.core.types.BitVector;

public final class Mnemonic
{
	private final String text;
	private final BitVector vector;
	
	public Mnemonic(String text, BitVector vector)
	{
		super();
		this.text = text;
		this.vector = vector;
	}

	public String getText()
	{
		return text;
	}

	public BitVector getVector()
	{
		return vector;
	}
}
