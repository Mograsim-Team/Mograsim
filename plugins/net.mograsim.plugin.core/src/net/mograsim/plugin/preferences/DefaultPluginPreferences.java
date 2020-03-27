package net.mograsim.plugin.preferences;

import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.DefaultPreferences;

public class DefaultPluginPreferences extends DefaultPreferences implements PluginPreferences
{
	@Override
	public boolean getBoolean(String name)
	{
		switch (name)
		{
		case MPM_EDITOR_BITS_AS_COLUMN_NAME:
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
		case SIMULATION_SPEED_PRECISION:
			return 6;
		case MAX_MEMORY_CHANGE_INTERVAL:
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
		default:
			throw new IllegalArgumentException("Unknown double preference name: " + name);
		}
	}

	@Override
	public ColorDefinition getColorDefinition(String name)
	{
		switch (name)
		{
		default:
			throw new IllegalArgumentException("Unknown color preference name: " + name);
		}
	}
}