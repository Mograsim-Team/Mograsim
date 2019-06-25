package net.mograsim.preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

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

	public abstract Color toColor(Device device, ColorDefinition col);

	public abstract void dispose(Color col);
}