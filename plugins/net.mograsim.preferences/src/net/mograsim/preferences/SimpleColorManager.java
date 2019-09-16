package net.mograsim.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

import net.mograsim.preferences.ColorDefinition.BuiltInColor;

public class SimpleColorManager extends ColorManager
{
	private static final Map<ColorDefinition, Color> systemColors = new HashMap<>();

	private final Device device;
	private final Map<ColorDefinition, Color> cachedColors;

	public SimpleColorManager()
	{
		this.device = Display.getCurrent();
		this.cachedColors = new HashMap<>(systemColors);
	}

	@Override
	public Color toColor(ColorDefinition col)
	{
		if (col == null)
			return null;
		Color cachedColor = cachedColors.get(col);
		if (cachedColor != null)
			return cachedColor;
		if (col.builtInColor != null)
		{
			Color systemColor = device.getSystemColor(toSWTColorConstant(col.builtInColor));
			systemColors.put(col, systemColor);
			cachedColors.put(col, systemColor);
			return systemColor;
		}
		Color nonSystemColor = new Color(device, col.r, col.g, col.b);
		cachedColors.put(col, nonSystemColor);
		return nonSystemColor;
	}

	@Override
	public void clearCache()
	{
		cachedColors.entrySet().stream().filter(e -> !systemColors.containsKey(e.getKey())).map(Entry::getValue).forEach(Color::dispose);
		cachedColors.clear();
		cachedColors.putAll(systemColors);
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
