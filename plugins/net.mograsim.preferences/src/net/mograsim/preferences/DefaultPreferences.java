package net.mograsim.preferences;

import net.mograsim.preferences.ColorDefinition.BuiltInColor;

public class DefaultPreferences extends Preferences
{
	@Override
	public boolean getBoolean(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.model.improvetext":
			return true;
		case "net.mograsim.logic.model.debug.openhlsshell":
			return false;
		case "net.mograsim.plugin.core.editors.mpm.descriptionascolumnname":
			return false;
		default:
			throw new IllegalArgumentException("Unknown boolean preference name: " + name);
		}
	}

	@Override
	public int getInt(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.model.debug.hlsshelldepth":
			return 0;
		case "net.mograsim.logic.model.button.action":
			return 3;
		case "net.mograsim.logic.model.button.drag":
			return 1;
		case "net.mograsim.logic.model.button.zoom":
			return 2;
		case "net.mograsim.plugin.core.simspeedprecision":
			return 6;
		case "net.mograsim.plugin.core.maxmemchangeinterval":
			return 1000;
		default:
			throw new IllegalArgumentException("Unknown int preference name: " + name);
		}
	}

	@Override
	public double getDouble(String name)
	{
		switch (name)
		{
		case "net.mograsim.logic.model.linewidth.default":
			return 0.5;
		case "net.mograsim.logic.model.linewidth.wire.singlebit":
			return 0.5;
		case "net.mograsim.logic.model.linewidth.wire.multibit":
			return 0.85;
		case "net.mograsim.logic.model.submodel.zoomalpha0":
			return 0.8;
		case "net.mograsim.logic.model.submodel.zoomalpha1":
			return 0.9;
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