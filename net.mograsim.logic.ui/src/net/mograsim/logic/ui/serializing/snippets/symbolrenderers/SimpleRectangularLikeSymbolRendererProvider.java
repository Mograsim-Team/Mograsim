package net.mograsim.logic.ui.serializing.snippets.symbolrenderers;

import java.util.Map.Entry;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.serializing.CodeSnippetSupplier;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.preferences.Preferences;

/**
 * Renders a text (<code>"centerText"</code>) with a given font height (<code>"centerTextHeight"</code>) in the center of the component and
 * draws a label for each pin with a given font height (<code>"pinLabelHeight"</code>). The labels of pins to the left of a given x
 * coordinate (<code>"horizontalComponentCenter"</code>) are drawn to the right of the respective pin; labels of pins to the right are drawn
 * left. A margin (<code>"pinLabelMargin"</code>) is applied for pin label drawing.<br>
 * Parameter format:
 * 
 * <pre>
 * {
 *   "centerText": [String]
 *   "centerTextHeight": [double]
 *   "horizontalComponentCenter": [double]
 *   "pinLabelHeight": [double]
 *   "pinLabelMargin": [double]
 * }
 * </pre>
 * 
 * @author Daniel Kirschten
 */
public class SimpleRectangularLikeSymbolRendererProvider implements RendererProvider
{
	@Override
	public Renderer create(DeserializedSubmodelComponent component, JsonElement params)
	{
		JsonObject asJsonObject = params.getAsJsonObject();
		String centerText = asJsonObject.getAsJsonPrimitive("centerText").getAsString();
		double centerTextHeight = asJsonObject.getAsJsonPrimitive("centerTextHeight").getAsDouble();
		double horizontalComponentCenter = asJsonObject.getAsJsonPrimitive("horizontalComponentCenter").getAsDouble();
		double pinLabelHeight = asJsonObject.getAsJsonPrimitive("pinLabelHeight").getAsDouble();
		double pinLabelMargin = asJsonObject.getAsJsonPrimitive("pinLabelMargin").getAsDouble();
		return (gc, visReg) ->
		{
			double posX = component.getPosX();
			double posY = component.getPosY();
			double width = component.getWidth();
			double height = component.getHeight();

			Font oldFont = gc.getFont();
			gc.setFont(new Font(oldFont.getName(), centerTextHeight, oldFont.getStyle()));
			Point textExtent = gc.textExtent(centerText);
			Color textColor = Preferences.current().getColor("net.mograsim.logic.ui.color.text");
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
		};
	}

	static
	{
		CodeSnippetSupplier.setSymbolRendererProvider(SimpleRectangularLikeSymbolRendererProvider.class.getCanonicalName(),
				new SimpleRectangularLikeSymbolRendererProvider());
	}
}