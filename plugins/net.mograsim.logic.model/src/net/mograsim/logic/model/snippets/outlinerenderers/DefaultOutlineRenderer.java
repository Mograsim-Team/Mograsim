package net.mograsim.logic.model.snippets.outlinerenderers;

import static net.mograsim.logic.model.preferences.RenderPreferences.FOREGROUND_COLOR;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

public class DefaultOutlineRenderer implements Renderer
{
	private final ModelComponent component;

	public DefaultOutlineRenderer(ModelComponent component)
	{
		this(component, null);
	}

	public DefaultOutlineRenderer(ModelComponent component, @SuppressWarnings("unused") Void params)
	{
		this.component = component;
	}

	@Override
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		ColorDefinition fg = renderPrefs.getColorDefinition(FOREGROUND_COLOR);
		if (fg != null)
			gc.setForeground(ColorManager.current().toColor(fg));
		gc.drawRectangle(component.getBounds());
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "default";
	}

	@Override
	public Void getParamsForSerializing(IdentifyParams idParams)
	{
		return null;
	}

	static
	{
		SubmodelComponentSnippetSuppliers.outlineRendererSupplier.setSnippetSupplier(DefaultOutlineRenderer.class.getCanonicalName(),
				SnippetDefinintion.create(Void.class, DefaultOutlineRenderer::new));
	}
}