package net.mograsim.logic.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.components.GUIComponent;

/**
 * Simulation visualizer canvas.
 * 
 * @author Daniel Kirschten
 */
public class LogicUICanvas extends ZoomableCanvas
{
	private final ViewModel model;

	public LogicUICanvas(Composite parent, int style, ViewModel model)
	{
		super(parent, style);

		this.model = model;

		LogicUIRenderer renderer = new LogicUIRenderer(model);
		addZoomedRenderer(gc -> renderer.render(gc, new Rectangle(-offX / zoom, -offY / zoom, gW / zoom, gH / zoom)));
		model.addRedrawListener(this::redrawThreadsafe);

		addListener(SWT.MouseDown, this::mouseDown);
	}

	private void mouseDown(Event e)
	{
		if (e.button == 1)
		{
			Point click = displayToWorldCoords(e.x, e.y);
			for (GUIComponent component : model.getComponents())
				if (component.getBounds().contains(click) && component.clicked(click.x, click.y))
				{
					redraw();
					break;
				}
		}
	}
}