package era.mi.gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import era.mi.gui.model.components.GUIComponent;
import era.mi.gui.model.wires.GUIWire;
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
	private final Set<GUIComponent> components;
	private final Set<GUIWire> wires;

	public LogicUICanvas(Composite parent, int style)
	{
		super(parent, style);

		components = new HashSet<>();
		wires = new HashSet<>();

		addZoomedRenderer(gc ->
		{
			Rectangle visibleRegion = new Rectangle(offX, offY, gW / zoom, gH / zoom);
			components.forEach(c -> drawComponent(gc, c, visibleRegion));
		});
		addZoomedRenderer(gc -> wires.forEach(w -> w.render(gc)));
		addListener(SWT.MouseDown, this::mouseDown);
	}

	/**
	 * Add a component to be drawn. Returns the given component for convenience.
	 * 
	 * @author Daniel Kirschten
	 */
	// TODO replace with model change listener
	public <C extends GUIComponent> C addComponent(C component)
	{
		components.add(component);
		return component;
	}

	/**
	 * Add a graphical wire between the given connection points of the given components. The given components have to be added and the given
	 * connection points have to be connected logically first.
	 * 
	 * @author Daniel Kirschten
	 */
	// TODO replace with model change listener
	public void addWire(Pin pin1, Pin pin2, Point... path)
	{
		wires.add(new GUIWire(this::redrawThreadsafe, pin1, pin2, path));
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
			for (GUIComponent component : components)
				if (component.getBounds().contains(click))
				{
					if (component.clicked(click.x, click.y))
						redraw();
					break;
				}
		}
	}
}