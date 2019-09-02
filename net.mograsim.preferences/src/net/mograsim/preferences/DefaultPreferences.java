package net.mograsim.preferences;

import net.mograsim.preferences.ColorDefinition.BuiltInColor;

public class DefaultPreferences extends Preferences
{
	@Override
	public int getInt(String name)
	{
		switch (name)
		{
		default:
			throw new IllegalArgumentException("Unknown int preference name: " + name);
		}
	}

	@Override
	public double getDouble(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.model.linewidth":
			return 0.5;
		default:
			throw new IllegalArgumentException("Unknown double preference name: " + name);
		}
	}

	@Override
	public ColorDefinition getColorDefinition(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.model.color.bit.one":
			return new ColorDefinition(BuiltInColor.COLOR_GREEN);
		case "net.mograsim.logic.model.color.bit.u":
			return new ColorDefinition(BuiltInColor.COLOR_CYAN);
		case "net.mograsim.logic.model.color.bit.x":
			return new ColorDefinition(BuiltInColor.COLOR_RED);
		case "net.mograsim.logic.model.color.bit.z":
			return new ColorDefinition(BuiltInColor.COLOR_YELLOW);
		case "net.mograsim.logic.model.color.bit.zero":
			return new ColorDefinition(BuiltInColor.COLOR_GRAY);
		case "net.mograsim.logic.model.color.background":
			return new ColorDefinition(BuiltInColor.COLOR_WHITE);
		case "net.mograsim.logic.model.color.foreground":
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		case "net.mograsim.logic.model.color.text":
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		default:
			throw new IllegalArgumentException("Unknown color preference name: " + name);
		}
	}
}