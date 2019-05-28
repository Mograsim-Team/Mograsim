package era.mi.gui;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIComponent;
import era.mi.gui.model.wires.Pin;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;

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

		Consumer<Object> redrawConsumer = o -> redrawThreadsafe();
		Consumer<Pin> pinAddedListener = p ->
		{
			p.addPinMovedListener(redrawConsumer);
			redrawThreadsafe();
		};
		Consumer<Pin> pinRemovedListener = p ->
		{
			p.removePinMovedListener(redrawConsumer);
			redrawThreadsafe();
		};
		model.addComponentAddedListener(c ->
		{
			c.addComponentChangedListener(redrawConsumer);
			c.addComponentMovedListener(redrawConsumer);
			c.addPinAddedListener(pinAddedListener);
			c.addPinRemovedListener(pinRemovedListener);
			redrawThreadsafe();
		});
		model.addComponentRemovedListener(c ->
		{
			c.removeComponentChangedListener(redrawConsumer);
			c.removeComponentMovedListener(redrawConsumer);
			c.removePinAddedListener(pinAddedListener);
			c.removePinRemovedListener(pinRemovedListener);
			redrawThreadsafe();
		});
		model.addWireAddedListener(c -> redrawThreadsafe());
		model.addWireRemovedListener(c -> redrawThreadsafe());

		addZoomedRenderer(gc ->
		{
			Rectangle visibleRegion = new Rectangle(offX, offY, gW / zoom, gH / zoom);
			model.getComponents().forEach(c -> drawComponent(gc, c, visibleRegion));
		});
		addZoomedRenderer(gc -> model.getWires().forEach(w -> w.render(gc)));
		addListener(SWT.MouseDown, this::mouseDown);
	}

	private void drawComponent(GeneralGC gc, GUIComponent component, Rectangle visibleRegion)
	{
		component.render(gc, visibleRegion);
		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_CYAN));
		for (Pin p : component.getPins())
		{
			Point pos = p.getPos();
			gc.fillOval(pos.x - 1, pos.y - 1, 2, 2);
		}
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