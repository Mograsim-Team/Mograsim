package net.mograsim.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import net.mograsim.preferences.ColorDefinition.BuiltInColor;

public class SimpleColorManager extends ColorManager
{
	private static final Map<BuiltInColor, Color> systemColors = new HashMap<>();
	private static final Map<Color, BuiltInColor> systemColorConstants = new HashMap<>();

	@Override
	public Color toColor(Device device, ColorDefinition col)
	{
		Color systemColor = systemColors.get(col.builtInColor);
		if (systemColor != null)
			return systemColor;
		if (col.builtInColor != null)
		{
			systemColor = device.getSystemColor(toSWTColorConstant(col.builtInColor));
			systemColors.put(col.builtInColor, systemColor);
			systemColorConstants.put(systemColor, col.builtInColor);
			return systemColor;
		}
		return new Color(device, col.r, col.g, col.b);
	}

	@Override
	public void dispose(Color col)
	{
		if (!systemColorConstants.containsKey(col))
			col.dispose();
	}

	public static int toSWTColorConstant(BuiltInColor col)
	{
		switch (col)
		{
		case COLOR_BLACK:
			return SWT.COLOR_BLACK;
		case COLOR_BLUE:
			return SWT.COLOR_BLUE;
		case COLOR_CYAN:
			return SWT.COLOR_CYAN;
		case COLOR_DARK_BLUE:
			return SWT.COLOR_DARK_BLUE;
		case COLOR_DARK_CYAN:
			return SWT.COLOR_DARK_CYAN;
		case COLOR_DARK_GRAY:
			return SWT.COLOR_DARK_GRAY;
		case COLOR_DARK_GREEN:
			return SWT.COLOR_DARK_GREEN;
		case COLOR_DARK_MAGENTA:
			return SWT.COLOR_DARK_MAGENTA;
		case COLOR_DARK_RED:
			return SWT.COLOR_DARK_RED;
		case COLOR_DARK_YELLOW:
			return SWT.COLOR_DARK_YELLOW;
		case COLOR_GRAY:
			return SWT.COLOR_GRAY;
		case COLOR_GREEN:
			return SWT.COLOR_GREEN;
		case COLOR_MAGENTA:
			return SWT.COLOR_MAGENTA;
		case COLOR_RED:
			return SWT.COLOR_RED;
		case COLOR_WHITE:
			return SWT.COLOR_WHITE;
		case COLOR_YELLOW:
			return SWT.COLOR_YELLOW;
		default:
			throw new IllegalArgumentException("Unknown enum constant: " + col);
		}
	}
}
