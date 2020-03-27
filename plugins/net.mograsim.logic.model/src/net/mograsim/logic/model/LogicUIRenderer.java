package net.mograsim.logic.model;

import static net.mograsim.logic.model.preferences.RenderPreferences.DEFAULT_LINE_WIDTH;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.preferences.RenderPreferences;

public class LogicUIRenderer
{
	private static final boolean DRAW_PINS = false;

	private final LogicModel model;

	public LogicUIRenderer(LogicModel model)
	{
		this.model = model;
	}

	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		gc.setAntialias(SWT.ON);
		gc.setClipping(visibleRegion);
		gc.setLineWidth(renderPrefs.getDouble(DEFAULT_LINE_WIDTH));
		model.getWiresByName().values().forEach(w ->
		{
			Rectangle bounds = w.getBounds();
			double lw = gc.getLineWidth();
			if (visibleRegion.intersects(bounds.x - lw, bounds.y - lw, bounds.width + lw + lw, bounds.height + lw + lw))
				w.render(gc, renderPrefs);
		});
		model.getComponentsByName().values().forEach(c -> renderComponent(gc, renderPrefs, c, visibleRegion));
	}

	private static void renderComponent(GeneralGC gc, RenderPreferences renderPrefs, ModelComponent component, Rectangle visibleRegion)
	{
		Rectangle bounds = component.getBounds();
		double lw = gc.getLineWidth();
		if (visibleRegion.intersects(bounds.x - lw, bounds.y - lw, bounds.width + lw + lw, bounds.height + lw + lw))
		{
			component.render(gc, renderPrefs, visibleRegion);
			if (DRAW_PINS)
			{
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_CYAN));
				for (Pin p : component.getPins().values())
				{
					Point pos = p.getPos();
					gc.fillOval(pos.x - 1, pos.y - 1, 2, 2);
				}
			}
		}
	}

}