package net.mograsim.logic.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

public class ColorHelper
{
	public static void executeWithDifferentForeground(GeneralGC gc, ColorDefinition col, Runnable exec)
	{
		executeWithDifferentColor(col, gc::getForeground, gc::setForeground, exec);
	}

	public static void executeWithDifferentBackground(GeneralGC gc, ColorDefinition col, Runnable exec)
	{
		executeWithDifferentColor(col, gc::getBackground, gc::setBackground, exec);
	}

	private static void executeWithDifferentColor(ColorDefinition col, Supplier<Color> getColor, Consumer<Color> setColor, Runnable exec)
	{
		if (col == null)
			exec.run();
		else
		{
			ColorManager cm = ColorManager.current();
			Color oldColor = getColor.get();
			Color newColor = cm.toColor(col);
			setColor.accept(newColor);
			exec.run();
			setColor.accept(oldColor);
		}
	}

	private ColorHelper()
	{
		throw new UnsupportedOperationException("No instances of ColorHelper");
	}
}