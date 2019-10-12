package net.mograsim.logic.core.types;

import java.math.BigInteger;

import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorDefinition.BuiltInColor;
import net.mograsim.preferences.Preferences;

public class BitVectorFormatter
{
	public static String formatValueAsString(ReadEnd end, boolean useDashInsteadOfZ)
	{
		return formatAsString(end == null ? null : end.getValues(), useDashInsteadOfZ);
	}

	public static String toBitstring(BitVector bitVector)
	{
		return bitVector.toBitstring();
	}

	public static String formatAsString(BitVector bitVector, boolean useDashInsteadOfZ)
	{
		if (bitVector == null)
			return "null";
		if (bitVector.isBinary())
		{
			String hexdigits = bitVector.getUnsignedValue().toString(16);
			StringBuilder sb = new StringBuilder();
			sb.append("0x");
			sb.append("0".repeat((bitVector.length() + 3) / 4 - hexdigits.length()));
			sb.append(hexdigits);
			return sb.toString();
		}
		if (useDashInsteadOfZ && bitVector.isHighImpedance())
			return "-";
		return bitVector.toBitstring();
	}

	// TODO this method overlaps in functionality with AsmNumberUtil (in plugin.core)
	public static BitVector parseUserBitVector(String userInput, int width)
	{
		BitVector bitvector = null;
		if (width > 0 && userInput.matches("0x[0-9a-fA-F]+"))
			// TODO should we check for overflows?
			bitvector = BitVector.from(new BigInteger(userInput.substring(2), 16), width);
		else if (width <= 0 || userInput.length() == width)
			// TODO do this without exceptions
			try
			{
				bitvector = BitVector.parseBitstring(userInput);
			}
			catch (@SuppressWarnings("unused") NullPointerException x)
			{
				// ignore
			}
		if (bitvector == null && width > 0)
			try
			{
				// TODO should we check for overflows?
				bitvector = BitVector.from(new BigInteger(userInput), width);
			}
			catch (@SuppressWarnings("unused") NumberFormatException x)
			{
				// ignore
			}
		return bitvector;
	}

	// TODO doesn't this belong to logic.model?
	public static ColorDefinition formatAsColor(ReadEnd end)
	{
		return formatAsColor(end == null ? null : end.getValues());
	}

	public static ColorDefinition formatAsColor(BitVector bitVector)
	{
		// TODO maybe find a color assignment for multiple-bit bit vectors?
		if (bitVector == null || bitVector.length() != 1)
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		switch (bitVector.getLSBit(0))
		{
		case ONE:
			return Preferences.current().getColorDefinition("net.mograsim.logic.model.color.bit.one");
		case U:
			return Preferences.current().getColorDefinition("net.mograsim.logic.model.color.bit.u");
		case X:
			return Preferences.current().getColorDefinition("net.mograsim.logic.model.color.bit.x");
		case Z:
			return Preferences.current().getColorDefinition("net.mograsim.logic.model.color.bit.z");
		case ZERO:
			return Preferences.current().getColorDefinition("net.mograsim.logic.model.color.bit.zero");
		default:
			throw new IllegalArgumentException("Unknown enum constant: " + bitVector.getLSBit(0));
		}
	}

	private BitVectorFormatter()
	{
		throw new UnsupportedOperationException("No BitVectorFormatter instances");
	}
}