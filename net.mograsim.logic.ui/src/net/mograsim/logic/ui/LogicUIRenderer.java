package net.mograsim.logic.ui;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.Pin;

public class LogicUIRenderer
{
	private static final boolean DRAW_PINS = false;

	private final ViewModel model;

	public LogicUIRenderer(ViewModel model)
	{
		this.model = model;
	}

	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		gc.setLineWidth(.5);
		model.getWires().forEach(w -> w.render(gc));
		model.getComponents().forEach(c -> renderComponent(gc, c, visibleRegion));
	}

	private static void renderComponent(GeneralGC gc, GUIComponent component, Rectangle visibleRegion)
	{
		component.render(gc, visibleRegion);
		if (DRAW_PINS)
		{
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_CYAN));
			for (Pin p : component.getPins())
			{
				Point pos = p.getPos();
				gc.fillOval(pos.x - 1, pos.y - 1, 2, 2);
			}
		}
	}

}