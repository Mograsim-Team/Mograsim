package net.mograsim.machine.isa;

import java.math.BigInteger;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.machine.isa.types.AsmNumberFormatException;

public class AsmIntegerOperand implements AsmOperand
{
	private final int size;

	public AsmIntegerOperand(int size)
	{
		this.size = size;
	}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public BitVector parse(String s) throws AsmNumberFormatException
	{
		String cleaned = s.replace("_", "").toLowerCase();
		int len = cleaned.length();
		BigInteger res;
		try
		{
			if (cleaned.startsWith("0x"))
				res = new BigInteger(cleaned.substring(2), 16);
			else if (cleaned.endsWith("h"))
				res = new BigInteger(cleaned.substring(0, len - 1), 16);
			else if (cleaned.startsWith("0b"))
				res = new BigInteger(cleaned.substring(2), 2);
			else if (cleaned.endsWith("b"))
				res = new BigInteger(cleaned.substring(2), 2);
			else if (cleaned.endsWith("q"))
				res = new BigInteger(cleaned.substring(0, len - 1), 8);
			else
				res = new BigInteger(cleaned);
		}
		catch (NumberFormatException e)
		{
			throw new AsmNumberFormatException(e, "Error parsing %s: no valid integer format", s);
		}
		return bigIntToBitVector(res);
	}

	BitVector bigIntToBitVector(BigInteger bi) throws AsmNumberFormatException
	{
		int bitLength = bi.bitLength() - (bi.signum() - 1) / 2;
		if (bitLength > size)
			throw new AsmNumberFormatException("Error parsing %s: bit count %d exceeds size %d", bi, bitLength, size);
		BitVectorMutator bvm = BitVectorMutator.ofLength(size);
		for (int i = 0; i < size; i++)
			bvm.setLSBit(i, Bit.of(bi.testBit(i)));
		return bvm.toBitVector();
	}

	public static void main(String[] args) throws AsmNumberFormatException
	{
		AsmIntegerOperand aio = new AsmIntegerOperand(4);
		System.out.println(aio.parse("0"));
		System.out.println(aio.parse("1"));
		System.out.println(aio.parse("2"));
		System.out.println(aio.parse("3"));
		System.out.println(aio.parse("4"));
		System.out.println(aio.parse("5"));
		System.out.println(aio.parse("6"));
		System.out.println(aio.parse("7"));
		System.out.println(aio.parse("8"));
		System.out.println(aio.parse("9"));
		System.out.println(aio.parse("10"));
		System.out.println(aio.parse("11"));
		System.out.println(aio.parse("12"));
		System.out.println(aio.parse("13"));
		System.out.println(aio.parse("14"));
		System.out.println(aio.parse("15"));
//		System.out.println(aio.parse("16"));
		System.out.println(aio.parse("-0"));
		System.out.println(aio.parse("-1"));
		System.out.println(aio.parse("-2"));
		System.out.println(aio.parse("-3"));
		System.out.println(aio.parse("-4"));
		System.out.println(aio.parse("-5"));
		System.out.println(aio.parse("-6"));
		System.out.println(aio.parse("-7"));
		System.out.println(aio.parse("-8"));
//		System.out.println(aio.parse("-9"));
	}
}
