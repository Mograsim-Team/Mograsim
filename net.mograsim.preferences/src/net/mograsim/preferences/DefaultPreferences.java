package net.mograsim.preferences;

import net.mograsim.preferences.ColorDefinition.BuiltInColor;

public class DefaultPreferences extends Preferences
{
	@Override
	public ColorDefinition getColor(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.ui.wire.bit.one":
			return new ColorDefinition(BuiltInColor.COLOR_GREEN);
		case "net.mograsim.logic.ui.wire.bit.u":
			return new ColorDefinition(BuiltInColor.COLOR_CYAN);
		case "net.mograsim.logic.ui.wire.bit.x":
			return new ColorDefinition(BuiltInColor.COLOR_RED);
		case "net.mograsim.logic.ui.wire.bit.z":
			return new ColorDefinition(BuiltInColor.COLOR_YELLOW);
		case "net.mograsim.logic.ui.wire.bit.zero":
			return new ColorDefinition(BuiltInColor.COLOR_GRAY);
		default:
			// TODO proper logging here...
			System.err.println("Unknown color name: " + name);
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		}
	}
}