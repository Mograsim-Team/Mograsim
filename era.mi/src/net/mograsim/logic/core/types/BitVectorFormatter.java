package net.mograsim.logic.core.types;

import net.mograsim.logic.core.types.ColorDefinition.BuiltInColor;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public class BitVectorFormatter
{
	public static String formatValueAsString(ReadEnd end)
	{
		return formatAsString(end == null ? null : end.getValues());
	}

	public static String formatAsString(BitVector bitVector)
	{
		if (bitVector == null)
			return "null";
		else
			return bitVector.toString();
	}

	public static ColorDefinition formatAsColor(ReadEnd end)
	{
		return formatAsColor(end == null ? null : end.getValues());
	}

	public static ColorDefinition formatAsColor(BitVector bitVector)
	{
		// TODO maybe find a color assignment for multiple-bit bit vectors?
		if (bitVector == null || bitVector.length() != 1)
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		else
			switch (bitVector.getBit(0))
			{
			case ONE:
				return new ColorDefinition(BuiltInColor.COLOR_GREEN);
			case U:
				return new ColorDefinition(BuiltInColor.COLOR_CYAN);
			case X:
				return new ColorDefinition(BuiltInColor.COLOR_RED);
			case Z:
				return new ColorDefinition(BuiltInColor.COLOR_YELLOW);
			case ZERO:
				return new ColorDefinition(BuiltInColor.COLOR_GRAY);
			default:
				throw new IllegalArgumentException("Unknown enum constant: " + bitVector.getBit(0));
			}
	}

	private BitVectorFormatter()
	{
		throw new UnsupportedOperationException("No BitVectorFormatter instances");
	}
}