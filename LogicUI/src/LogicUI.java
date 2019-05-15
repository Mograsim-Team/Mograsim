import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import era.mi.components.gui.BasicGUIComponent;
import era.mi.components.gui.GUIAndGate;
import era.mi.components.gui.GUIManualSwitch;
import era.mi.components.gui.GUIMerger;
import era.mi.components.gui.GUIMux;
import era.mi.components.gui.GUINotGate;
import era.mi.components.gui.GUISplitter;
import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

public class LogicUI
{
	private final Display						display;
	private final Shell							shell;
	private final ZoomableCanvas				canvas;
	private final Set<BasicGUIComponent>		components;
	private final Map<BasicGUIComponent, Point>	componentPositions;

	public LogicUI()
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		canvas = new ZoomableCanvas(shell, SWT.NONE);

		components = new HashSet<>();
		componentPositions = new HashMap<>();
		initComponents();

		canvas.addZoomedRenderer(gc -> components.forEach(component -> drawComponent(gc, component)));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(canvas);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(canvas, null).enableScale();
		canvas.addListener(SWT.MouseDown, this::mouseDown);
	}
	private void initComponents()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(1, 1), b = new WireArray(1, 1), c = new WireArray(1, 10), d = new WireArray(2, 1), e = new WireArray(1, 1),
				f = new WireArray(1, 1), g = new WireArray(1, 1), h = new WireArray(2, 1), i = new WireArray(2, 1), j = new WireArray(1, 1), k = new WireArray(1, 1);
		addComponent(new GUIManualSwitch(a), 160, 10);
		addComponent(new GUIAndGate(1, f, a, b), 130, 10);
		addComponent(new GUINotGate(1, f, g), 100, 10);
		addComponent(new GUIMerger(h, c, g), 70, 10);
		addComponent(new GUIMux(1, i, e, h, d), 10, 10);
		addComponent(new GUISplitter(i, k, j), 40, 10);
	}
	private void addComponent(BasicGUIComponent component, double x, double y)
	{
		components.add(component);
		componentPositions.put(component, new Point(x, y));
	}
	private void drawComponent(GeneralGC gc, BasicGUIComponent component)
	{
		TranslatedGC tgc = new TranslatedGC(gc, componentPositions.get(component));
		component.render(tgc);
		tgc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		for(int i = 0; i < component.getConnectedWireArraysCount(); i ++)
		{
			Point connectionPoint = component.getWireArrayConnectionPoint(i);
			if(connectionPoint != null)
				tgc.fillOval(connectionPoint.x - 1, connectionPoint.y - 1, 2, 2);
		}
	}
	private void mouseDown(Event e)
	{
		if(e.button == 1)
		{
			Point click = canvas.displayToWorldCoords(e.x, e.y);
			for(BasicGUIComponent component : components)
				if(component.getBounds().translate(componentPositions.get(component)).contains(click))
				{
					if(component.clicked(click.x, click.y))
						canvas.redraw();
					break;
				}
		}
	}

	public void run()
	{
		shell.open();
		while(!shell.isDisposed())
			if(!display.readAndDispatch())
				display.sleep();
	}

	public static void main(String[] args)
	{
		new LogicUI().run();
	}
}