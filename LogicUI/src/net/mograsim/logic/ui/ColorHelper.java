package net.mograsim.logic.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.mograsim.logic.core.types.ColorDefinition;
import net.mograsim.logic.core.types.ColorDefinition.BuiltInColor;

//TODO replace with a proper ColorManager
public class ColorHelper
{
	public static void executeWithDifferentForeground(GeneralGC gc, ColorDefinition col, Runnable exec)
	{
		executeWithDifferentColor(gc.getDevice(), col, gc::getForeground, gc::setForeground, exec);
	}

	public static void executeWithDifferentBackground(GeneralGC gc, ColorDefinition col, Runnable exec)
	{
		executeWithDifferentColor(gc.getDevice(), col, gc::getBackground, gc::setBackground, exec);
	}

	private static void executeWithDifferentColor(Device device, ColorDefinition col, Supplier<Color> getColor, Consumer<Color> setColor,
			Runnable exec)
	{
		Color oldColor = getColor.get();
		boolean isNoSystemColor = col.builtInColor == null;
		Color newColor;
		if (isNoSystemColor)
			newColor = new Color(device, col.r, col.g, col.b);
		else
			newColor = device.getSystemColor(ColorHelper.toSWTColorConstant(col.builtInColor));
		setColor.accept(newColor);

		exec.run();

		setColor.accept(oldColor);
		if (isNoSystemColor)
			newColor.dispose();
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

	private ColorHelper()
	{
		throw new UnsupportedOperationException("No instances of ColorHelper");
	}
}