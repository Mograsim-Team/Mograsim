package net.mograsim.logic.model.preferences;

import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorDefinition.BuiltInColor;
import net.mograsim.preferences.DefaultPreferences;

public class DefaultRenderPreferences extends DefaultPreferences implements RenderPreferences
{
	@Override
	public boolean getBoolean(String name)
	{
		switch (name)
		{
		case IMPROVE_SCALING:
			return true;
		case DEBUG_OPEN_HLSSHELL:
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
		case DEBUG_HLSSHELL_DEPTH:
			return 0;
		case ACTION_BUTTON:
			return 1;
		case DRAG_BUTTON:
			return 3;
		case ZOOM_BUTTON:
			return 2;
		default:
			throw new IllegalArgumentException("Unknown int preference name: " + name);
		}
	}

	@Override
	public double getDouble(String name)
	{
		switch (name)
		{
		case LINE_DASH_IMPROVEMENT_FACTOR:
			return 100.0;
		case DEFAULT_LINE_WIDTH:
			return 0.5;
		case WIRE_WIDTH_SINGLEBIT:
			return 0.5;
		case WIRE_WIDTH_MULTIBIT:
			return 0.85;
		case SUBMODEL_ZOOM_ALPHA_0:
			return 0.1;
		case SUBMODEL_ZOOM_ALPHA_1:
			return 0.2;
		default:
			throw new IllegalArgumentException("Unknown double preference name: " + name);
		}
	}

	@Override
	public String getString(String name)
	{
		switch (name)
		{
		case BIT_ZERO_DASH:
			return "10, 2.5";
		case BIT_Z_DASH:
			return "10, 0.25, 0.25, 1.5, 0.25, 0.25";
		case BIT_X_DASH:
			return "10, 0.25, 0.25, 0.25, 0.25, 0.5, 0.25, 0.25, 0.25, 0.25";
		case BIT_U_DASH:
			return "10, 0.25, 0.25, 0.25, 1, 0.25, 0.25, 0.25";
		case BIT_ONE_DASH:
			return "10, 0.5, 1.5, 0.5";
		default:
			throw new IllegalArgumentException("Unknown string preference name: " + name);
		}
	}

	@Override
	public ColorDefinition getColorDefinition(String name)
	{
		switch (name)
		{
		case BIT_ONE_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_GREEN);
		case BIT_U_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_CYAN);
		case BIT_X_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_RED);
		case BIT_Z_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_YELLOW);
		case BIT_ZERO_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_GRAY);
		case BACKGROUND_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_WHITE);
		case FOREGROUND_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		case TEXT_COLOR:
			return new ColorDefinition(BuiltInColor.COLOR_BLACK);
		default:
			throw new IllegalArgumentException("Unknown color preference name: " + name);
		}
	}
}