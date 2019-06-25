package net.mograsim.preferences;

public abstract class Preferences
{
	private static Preferences currentPreferences;

	public static void setPreferences(Preferences preferences)
	{
		if (preferences == null)
			throw new NullPointerException();
		currentPreferences = preferences;
	}

	public static Preferences current()
	{
		if (currentPreferences == null)
			currentPreferences = new DefaultPreferences();
		return currentPreferences;
	}

	public abstract ColorDefinition getColor(String name);
}
