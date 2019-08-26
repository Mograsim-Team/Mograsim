package net.mograsim.machine.isa;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.machine.isa.types.AsmNumberFormatException;

public class AsmFloatOperand implements AsmOperand
{
	private final int size;
	private final int mantissa;
	private final int exponent;

	public AsmFloatOperand(int size)
	{
		this.size = size;
		switch (size)
		{
		case 8:
			exponent = 4;
			mantissa = 3;
			break;
		case 16:
			exponent = 5;
			mantissa = 10;
			break;
		case 32:
			exponent = 8;
			mantissa = 23;
			break;
		case 64:
			exponent = 11;
			mantissa = 52;
			break;
		case 128:
			exponent = 15;
			mantissa = 112;
			break;
		default:
			if (size > 128 && size % 32 == 0)
			{
				exponent = (int) Math.round(Math.log(size) / Math.log(2)) - 13;
				mantissa = size - exponent - 1;
			} else
			{
				throw new IllegalArgumentException("Illegal floating point size: " + size);
			}
		}
	}

	public AsmFloatOperand(int exponent, int mantissa)
	{
		if (exponent < 1 || mantissa < 1)
			throw new IllegalArgumentException("illegal floating point specification: e=" + exponent + ", m=" + mantissa);
		this.exponent = exponent;
		this.mantissa = mantissa;
		this.size = exponent + mantissa + 1;
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
			return bigDecToBitVector(new BigDecimal(cleaned));
		}
		catch (NumberFormatException e)
		{
			throw new AsmNumberFormatException(e, "Error parsing %s: no valid float format", s);
		}
	}

	BitVector bigDecToBitVector(BigDecimal bi) throws AsmNumberFormatException
	{
		BigInteger raw = bi.unscaledValue();
		int mantLen = raw.bitLength();
//		bi.
		int bitLength = raw.bitLength() - (bi.signum() - 1) / 2;
		if (bitLength > size)
			throw new AsmNumberFormatException("Error parsing %s: bit count %d exceeds size %d", bi, bitLength, size);
		BitVectorMutator bvm = BitVectorMutator.ofWidth(size);
		for (int i = 0; i < size; i++)
			bvm.setLSBit(i, Bit.of(raw.testBit(i)));
		return bvm.toBitVector();
	}
}
