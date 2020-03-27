package net.mograsim.logic.model;

import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_ONE_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_U_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_X_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_ZERO_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_Z_COLOR;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorDefinition.BuiltInColor;

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
		if (useDashInsteadOfZ && bitVector.isHighImpedance())
			return "-";
		if (bitVector.length() == 1)
			return bitVector.toBitstring();
		if (bitVector.isBinary())
		{
			String hexdigits = bitVector.getUnsignedValue().toString(16);
			StringBuilder sb = new StringBuilder();
			sb.append("0x");
			sb.append("0".repeat((bitVector.length() + 3) / 4 - hexdigits.length()));
			sb.append(hexdigits);
			return sb.toString();
		}
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
	public static ColorDefinition formatAsColor(RenderPreferences renderPrefs, ReadEnd end)
	{
		return formatAsColor(renderPrefs, end == null ? null : end.getValues());
	}

	public static ColorDefinition formatAsColor(RenderPreferences renderPrefs, BitVector bitVector)
	{
		// TODO maybe find a color assignment for multiple-bit bit vectors?
		if (bitVector == null || bitVector.length() != 1)
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		switch (bitVector.getLSBit(0))
		{
		case ONE:
			return renderPrefs.getColorDefinition(BIT_ONE_COLOR);
		case U:
			return renderPrefs.getColorDefinition(BIT_U_COLOR);
		case X:
			return renderPrefs.getColorDefinition(BIT_X_COLOR);
		case Z:
			return renderPrefs.getColorDefinition(BIT_Z_COLOR);
		case ZERO:
			return renderPrefs.getColorDefinition(BIT_ZERO_COLOR);
		default:
			throw new IllegalArgumentException("Unknown enum constant: " + bitVector.getLSBit(0));
		}
	}

	private BitVectorFormatter()
	{
		throw new UnsupportedOperationException("No BitVectorFormatter instances");
	}
}