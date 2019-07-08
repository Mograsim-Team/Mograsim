package net.mograsim.logic.ui.serializing.snippets.symbolrenderers;

import java.util.Map.Entry;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.serializing.CodeSnippetSupplier;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.SnippetSupplier;
import net.mograsim.preferences.Preferences;

/**
 * Renders a text (<code>"centerText"</code>) with a given font height (<code>"centerTextHeight"</code>) in the center of the component and
 * draws a label for each pin with a given font height (<code>"pinLabelHeight"</code>). The labels of pins to the left of a given x
 * coordinate (<code>"horizontalComponentCenter"</code>) are drawn to the right of the respective pin; labels of pins to the right are drawn
 * left. A margin (<code>"pinLabelMargin"</code>) is applied for pin label drawing.
 * 
 * @author Daniel Kirschten
 */
public class SimpleRectangularLikeSymbolRenderer implements Renderer
{
	private final GUIComponent component;
	private final SimpleRectangularLikeParams params;

	public SimpleRectangularLikeSymbolRenderer(SubmodelComponent component, SimpleRectangularLikeParams params)
	{
		this.component = component;
		this.params = params;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = component.getPosX();
		double posY = component.getPosY();
		double width = component.getWidth();
		double height = component.getHeight();

		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), params.centerTextHeight, oldFont.getStyle()));
		Point textExtent = gc.textExtent(params.centerText);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.ui.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(params.centerText, posX + (width - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(new Font(oldFont.getName(), params.pinLabelHeight, oldFont.getStyle()));
		for (Entry<String, Pin> pinEntry : component.getPins().entrySet())
		{
			String pinName = pinEntry.getKey();
			Pin pin = pinEntry.getValue();
			double pinX = pin.getRelX();
			double pinY = posY + pin.getRelY();
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName,
					posX + pinX + (pinX > params.horizontalComponentCenter ? -textExtent.x - params.pinLabelMargin : params.pinLabelMargin),
					pinY - textExtent.y / 2, true);
		}
		gc.setFont(oldFont);
	}

	public static class SimpleRectangularLikeParams
	{
		public String centerText;
		public double centerTextHeight;
		public double horizontalComponentCenter;
		public double pinLabelHeight;
		public double pinLabelMargin;
	}

	static
	{
		CodeSnippetSupplier.symbolRendererSupplier.setSnippetSupplier(SimpleRectangularLikeSymbolRenderer.class.getCanonicalName(),
				SnippetSupplier.create(SimpleRectangularLikeParams.class, SimpleRectangularLikeSymbolRenderer::new));
	}
}