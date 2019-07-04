package net.mograsim.logic.ui.serializing.snippets.symbolrenderers;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class DefaultSymbolRendererProvider implements RendererProvider
{
	private static final String id = "<Symbol\nunknown>";

	@Override
	public Renderer create(DeserializedSubmodelComponent component, JsonElement params)
	{
		return (gc, visReg) ->
		{
			ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.text");
			if (fg != null)
				gc.setForeground(ColorManager.current().toColor(fg));
			Point idSize = gc.textExtent(id);
			Rectangle bounds = component.getBounds();
			gc.drawText(id, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
		};
	}
}