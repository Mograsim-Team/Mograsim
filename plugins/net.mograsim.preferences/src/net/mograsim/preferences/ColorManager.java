package net.mograsim.preferences;

import org.eclipse.swt.graphics.Color;

public abstract class ColorManager
{
	private static ColorManager currentManager;

	public static void setColorManager(ColorManager manager)
	{
		if (manager == null)
			throw new NullPointerException();
		currentManager = manager;
	}

	public static ColorManager current()
	{
		if (currentManager == null)
			currentManager = new SimpleColorManager();
		return currentManager;
	}

	public abstract Color toColor(ColorDefinition col);

	public void clearCache()
	{
		// this method is intended to be overridden
	}
}