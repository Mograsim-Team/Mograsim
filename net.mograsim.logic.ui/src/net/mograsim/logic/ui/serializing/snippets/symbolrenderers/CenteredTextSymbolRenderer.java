package net.mograsim.logic.ui.serializing.snippets.symbolrenderers;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.serializing.CodeSnippetSupplier;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.SnippetSupplier;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

/**
 * Renders a text (<code>"text"</code>) with a given font height (<code>"height"</code>) in the center of the component.
 * 
 * @author Daniel Kirschten
 */
public class CenteredTextSymbolRenderer implements Renderer
{
	private final GUIComponent component;
	private final CenteredTextParams params;

	public CenteredTextSymbolRenderer(SubmodelComponent component, CenteredTextParams params)
	{
		this.component = component;
		this.params = params;

	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), params.fontHeight, oldFont.getStyle()));
		ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.text");
		if (fg != null)
			gc.setForeground(ColorManager.current().toColor(fg));
		Point idSize = gc.textExtent(params.text);
		Rectangle bounds = component.getBounds();
		gc.drawText(params.text, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
		gc.setFont(oldFont);
	}

	public static class CenteredTextParams
	{
		public String text;
		public double fontHeight;
	}

	static
	{
		CodeSnippetSupplier.symbolRendererProviderSupplier.setSnippetProvider(CenteredTextSymbolRenderer.class.getCanonicalName(),
				SnippetSupplier.create(CenteredTextParams.class, CenteredTextSymbolRenderer::new));
	}
}