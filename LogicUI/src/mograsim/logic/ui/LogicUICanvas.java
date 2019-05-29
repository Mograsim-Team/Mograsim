package mograsim.logic.ui;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import mograsim.logic.ui.model.ViewModel;
import mograsim.logic.ui.model.components.GUIComponent;
import mograsim.logic.ui.model.wires.GUIWire;
import mograsim.logic.ui.model.wires.Pin;
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
	private static final boolean DRAW_PINS = false;

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
		Consumer<? super GUIComponent> componentAddedListener = c ->
		{
			c.addComponentLookChangedListener(redrawConsumer);
			c.addComponentMovedListener(redrawConsumer);
			c.addPinAddedListener(pinAddedListener);
			c.addPinRemovedListener(pinRemovedListener);
			redrawThreadsafe();
		};
		model.addComponentAddedListener(componentAddedListener);
		model.getComponents().forEach(componentAddedListener);
		model.addComponentRemovedListener(c ->
		{
			c.removeComponentLookChangedListener(redrawConsumer);
			c.removeComponentMovedListener(redrawConsumer);
			c.removePinAddedListener(pinAddedListener);
			c.removePinRemovedListener(pinRemovedListener);
			redrawThreadsafe();
		});
		Consumer<? super GUIWire> wireAddedListener = w ->
		{
			w.addWireLookChangedListener(redrawConsumer);
			redrawThreadsafe();
		};
		model.addWireAddedListener(wireAddedListener);
		model.getWires().forEach(wireAddedListener);
		model.addWireRemovedListener(w ->
		{
			w.removeWireLookChangedListener(redrawConsumer);
			redrawThreadsafe();
		});

		addZoomedRenderer(gc ->
		{
			gc.setLineWidth(.5);
			model.getWires().forEach(w -> w.render(gc));
			Rectangle visibleRegion = new Rectangle(offX, offY, gW / zoom, gH / zoom);
			model.getComponents().forEach(c -> drawComponent(gc, c, visibleRegion));
		});
		addListener(SWT.MouseDown, this::mouseDown);
	}

	private void drawComponent(GeneralGC gc, GUIComponent component, Rectangle visibleRegion)
	{
		component.render(gc, visibleRegion);
		if (DRAW_PINS)
		{
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
			for (Pin p : component.getPins())
			{
				Point pos = p.getPos();
				gc.fillOval(pos.x - 1, pos.y - 1, 2, 2);
			}
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