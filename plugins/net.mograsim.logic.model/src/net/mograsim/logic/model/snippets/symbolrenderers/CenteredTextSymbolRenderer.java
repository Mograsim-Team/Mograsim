package net.mograsim.logic.model.snippets.symbolrenderers;

import static net.mograsim.logic.model.preferences.RenderPreferences.TEXT_COLOR;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
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

/**
 * Renders a text (<code>"text"</code>) with a given font height (<code>"height"</code>) in the center of the component.
 * 
 * @author Daniel Kirschten
 */
public class CenteredTextSymbolRenderer implements Renderer
{
	private final ModelComponent component;
	private final String text;
	private final double fontHeight;

	public CenteredTextSymbolRenderer(ModelComponent component, CenteredTextParams params)
	{
		this.component = component;
		this.text = params.text;
		this.fontHeight = params.fontHeight;

	}

	@Override
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), fontHeight, oldFont.getStyle()));
		ColorDefinition fg = renderPrefs.getColorDefinition(TEXT_COLOR);
		if (fg != null)
			gc.setForeground(ColorManager.current().toColor(fg));
		Point idSize = gc.textExtent(text);
		Rectangle bounds = component.getBounds();
		gc.drawText(text, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
		gc.setFont(oldFont);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "centeredText";
	}

	@Override
	public CenteredTextParams getParamsForSerializing(IdentifyParams idParams)
	{
		CenteredTextParams params = new CenteredTextParams();
		params.text = text;
		params.fontHeight = fontHeight;
		return params;
	}

	public static class CenteredTextParams
	{
		public String text;
		public double fontHeight;
	}

	static
	{
		SubmodelComponentSnippetSuppliers.symbolRendererSupplier.setSnippetSupplier(CenteredTextSymbolRenderer.class.getCanonicalName(),
				SnippetDefinintion.create(CenteredTextParams.class, CenteredTextSymbolRenderer::new));
	}
}