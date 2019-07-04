package net.mograsim.logic.ui.serializing.snippets.symbolrenderers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.serializing.CodeSnippetSupplier;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

/**
 * Renders a text (<code>"text"</code>) with a given font height (<code>"height"</code>) in the center of the component.<br>
 * Parameter format:
 * 
 * <pre>
 * {
 *   "text": [String]
 *   "height": [int]
 * }
 * </pre>
 * 
 * @author Daniel Kirschten
 */
public class CenteredTextSymbolRendererProvider implements RendererProvider
{
	@Override
	public Renderer create(DeserializedSubmodelComponent component, JsonElement params)
	{
		JsonObject asJsonObject = params.getAsJsonObject();
		String text = asJsonObject.getAsJsonPrimitive("text").getAsString();
		int fontHeight = asJsonObject.getAsJsonPrimitive("height").getAsInt();
		return (gc, visReg) ->
		{
			Font oldFont = gc.getFont();
			gc.setFont(new Font(oldFont.getName(), fontHeight, oldFont.getStyle()));
			ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.text");
			if (fg != null)
				gc.setForeground(ColorManager.current().toColor(fg));
			Point idSize = gc.textExtent(text);
			Rectangle bounds = component.getBounds();
			gc.drawText(text, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
			gc.setFont(oldFont);
		};
	}

	static
	{
		CodeSnippetSupplier.setSymbolRendererProvider(CenteredTextSymbolRendererProvider.class.getCanonicalName(),
				new CenteredTextSymbolRendererProvider());
	}
}