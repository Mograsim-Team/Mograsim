package net.mograsim.logic.core.types;

import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorDefinition.BuiltInColor;
import net.mograsim.preferences.Preferences;

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
		return bitVector.toString();
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