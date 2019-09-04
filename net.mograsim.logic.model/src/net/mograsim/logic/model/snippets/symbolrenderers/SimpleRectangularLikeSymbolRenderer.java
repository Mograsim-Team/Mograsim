package net.mograsim.logic.model.snippets.symbolrenderers;

import java.util.Map.Entry;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
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
	private final String centerText;
	private final double centerTextHeight;
	private final double horizontalComponentCenter;
	private final double pinLabelHeight;
	private final double pinLabelMargin;

	public SimpleRectangularLikeSymbolRenderer(GUIComponent component, SimpleRectangularLikeParams params)
	{
		this.component = component;
		this.centerText = params.centerText;
		this.centerTextHeight = params.centerTextHeight;
		this.horizontalComponentCenter = params.horizontalComponentCenter;
		this.pinLabelHeight = params.pinLabelHeight;
		this.pinLabelMargin = params.pinLabelMargin;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = component.getPosX();
		double posY = component.getPosY();
		double width = component.getWidth();
		double height = component.getHeight();

		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), centerTextHeight, oldFont.getStyle()));
		Point textExtent = gc.textExtent(centerText);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(centerText, posX + (width - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(new Font(oldFont.getName(), pinLabelHeight, oldFont.getStyle()));
		for (Entry<String, Pin> pinEntry : component.getPins().entrySet())
		{
			String pinName = pinEntry.getKey();
			Pin pin = pinEntry.getValue();
			double pinX = pin.getRelX();
			double pinY = posY + pin.getRelY();
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName, posX + pinX + (pinX > horizontalComponentCenter ? -textExtent.x - pinLabelMargin : pinLabelMargin),
					pinY - textExtent.y / 2, true);
		}
		gc.setFont(oldFont);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "simpleRectangularLike";
	}

	@Override
	public SimpleRectangularLikeParams getParamsForSerializing(IdentifyParams idParams)
	{
		SimpleRectangularLikeParams params = new SimpleRectangularLikeParams();
		params.centerText = centerText;
		params.centerTextHeight = centerTextHeight;
		params.horizontalComponentCenter = horizontalComponentCenter;
		params.pinLabelHeight = pinLabelHeight;
		params.pinLabelMargin = pinLabelMargin;
		return params;
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
		SubmodelComponentSnippetSuppliers.symbolRendererSupplier.setSnippetSupplier(
				SimpleRectangularLikeSymbolRenderer.class.getCanonicalName(),
				SnippetDefinintion.create(SimpleRectangularLikeParams.class, SimpleRectangularLikeSymbolRenderer::new));
	}
}