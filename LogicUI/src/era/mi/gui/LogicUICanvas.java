package era.mi.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import era.mi.gui.components.BasicGUIComponent;
import era.mi.gui.wires.GUIWire;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;

/**
 * Simulation visualizer canvas.
 * 
 * @author Daniel Kirschten
 */
public class LogicUICanvas extends ZoomableCanvas
{
	private final Set<BasicGUIComponent> components;
	private final Map<BasicGUIComponent, Point> componentPositions;
	private final Set<GUIWire> wires;

	public LogicUICanvas(Composite parent, int style)
	{
		super(parent, style);

		components = new HashSet<>();
		componentPositions = new HashMap<>();
		wires = new HashSet<>();

		addZoomedRenderer(gc -> components.forEach(c -> drawComponent(gc, c)));
		addZoomedRenderer(gc -> wires.forEach(w -> w.render(gc)));
		addListener(SWT.MouseDown, this::mouseDown);
	}

	/**
	 * Add a component to be drawn. Returns the given component for convenience.
	 * 
	 * @author Daniel Kirschten
	 */
	public <C extends BasicGUIComponent> C addComponent(C component, double x, double y)
	{
		components.add(component);
		componentPositions.put(component, new Point(x, y));
		return component;
	}

	/**
	 * Add a graphical wire between the given connection points of the given components. The given components have to be added and the given
	 * connection points have to be connected logically first.
	 * 
	 * @author Daniel Kirschten
	 */
	public void addWire(BasicGUIComponent component1, int component1ConnectionIndex, BasicGUIComponent component2,
			int component2ConnectionIndex, Point... path)
	{
		wires.add(new GUIWire(this::redrawThreadsafe, component1, component1ConnectionIndex, componentPositions.get(component1), component2,
				component2ConnectionIndex, componentPositions.get(component2), path));
	}

	private void drawComponent(GeneralGC gc, BasicGUIComponent component)
	{
		TranslatedGC tgc = new TranslatedGC(gc, componentPositions.get(component));
		component.render(tgc);
		tgc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
	}

	private void mouseDown(Event e)
	{
		if (e.button == 1)
		{
			Point click = displayToWorldCoords(e.x, e.y);
			for (BasicGUIComponent component : components)
				if (component.getBounds().translate(componentPositions.get(component)).contains(click))
				{
					if (component.clicked(click.x, click.y))
						redraw();
					break;
				}
		}
	}
}