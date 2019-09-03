package net.mograsim.preferences;

import java.util.Objects;

import org.eclipse.swt.graphics.Color;

public abstract class Preferences
{
	private static Preferences currentPreferences;

	public static void setPreferences(Preferences preferences)
	{
		currentPreferences = Objects.requireNonNull(preferences);
	}

	public static Preferences current()
	{
		if (currentPreferences == null)
			currentPreferences = new DefaultPreferences();
		return currentPreferences;
	}

	public abstract boolean getBoolean(String name);

	public abstract int getInt(String name);

	public abstract double getDouble(String name);

	public abstract ColorDefinition getColorDefinition(String name);

	public Color getColor(String name)
	{
		return ColorManager.current().toColor(getColorDefinition(name));
	}
}
