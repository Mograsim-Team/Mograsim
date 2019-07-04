package net.mograsim.logic.ui.serializing.snippets.outlinerenderers;

import com.google.gson.JsonElement;

import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class DefaultOutlineRendererProvider implements RendererProvider
{
	@Override
	public Renderer create(DeserializedSubmodelComponent component, JsonElement params)
	{
		return (gc, visReg) ->
		{
			ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.foreground");
			if (fg != null)
				gc.setForeground(ColorManager.current().toColor(fg));
			gc.drawRectangle(component.getBounds());
		};

	}
}