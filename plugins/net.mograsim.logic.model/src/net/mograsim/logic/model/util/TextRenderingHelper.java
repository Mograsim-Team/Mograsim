package net.mograsim.logic.model.util;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class TextRenderingHelper
{
	public static void drawTextFitting(GeneralGC gc, String string, Rectangle box, double margin, boolean isTransparent)
	{
		drawTextFitting(gc, string, box.x, box.y, box.width, box.height, margin, isTransparent);
	}

	public static void drawTextFitting(GeneralGC gc, String string, double x, double y, double width, double height, double margin,
			boolean isTransparent)
	{
		Point textExtentOldFont = gc.textExtent(string);
		Font oldFont = gc.getFont();
		double factorX = (width - margin * 2) / textExtentOldFont.x;
		double factorY = (height - margin * 2) / textExtentOldFont.y;
		double factor = Math.min(factorX, factorY);
		gc.setFont(oldFont.scale(factor));
		Point textExtentCurrentFont = gc.textExtent(string);
		double xOff = (width - textExtentCurrentFont.x) / 2;
		double yOff = (height - textExtentCurrentFont.y) / 2;
		gc.drawText(string, x + xOff, y + yOff, isTransparent);
		gc.setFont(oldFont);
	}
}