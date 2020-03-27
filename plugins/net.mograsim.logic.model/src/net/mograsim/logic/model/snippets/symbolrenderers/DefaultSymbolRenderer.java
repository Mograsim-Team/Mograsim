package net.mograsim.logic.model.snippets.symbolrenderers;

import static net.mograsim.logic.model.preferences.RenderPreferences.TEXT_COLOR;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

public class DefaultSymbolRenderer implements Renderer
{
	private static final String id = "<Symbol\nunknown>";

	private final ModelComponent component;

	public DefaultSymbolRenderer(ModelComponent component)
	{
		this(component, null);
	}

	public DefaultSymbolRenderer(ModelComponent component, @SuppressWarnings("unused") Void params)
	{
		this.component = component;
	}

	@Override
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		ColorDefinition fg = renderPrefs.getColorDefinition(TEXT_COLOR);
		if (fg != null)
			gc.setForeground(ColorManager.current().toColor(fg));
		Point idSize = gc.textExtent(id);
		Rectangle bounds = component.getBounds();
		gc.drawText(id, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
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
		SubmodelComponentSnippetSuppliers.symbolRendererSupplier.setSnippetSupplier(DefaultSymbolRenderer.class.getCanonicalName(),
				SnippetDefinintion.create(Void.class, DefaultSymbolRenderer::new));
	}
}