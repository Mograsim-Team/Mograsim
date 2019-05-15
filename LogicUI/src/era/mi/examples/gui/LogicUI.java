package era.mi.examples.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import era.mi.components.gui.BasicGUIComponent;
import era.mi.components.gui.GUIManualSwitch;
import era.mi.components.gui.GUINotGate;
import era.mi.components.gui.GUIOrGate;
import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import era.mi.wires.gui.GUIWire;
import era.mi.wires.gui.WireConnectionPoint;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

public class LogicUI
{
	private static final int					WIRE_DELAY	= 40;
	private static final int					OR_DELAY	= 100;
	private static final int					NOT_DELAY	= 100;
	private final Display						display;
	private final Shell							shell;
	private final ZoomableCanvas				canvas;
	private final Set<BasicGUIComponent>		components;
	private final Map<BasicGUIComponent, Point>	componentPositions;
	private final Set<GUIWire>					wires;

	public LogicUI()
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		canvas = new ZoomableCanvas(shell, SWT.NONE);

		components = new HashSet<>();
		componentPositions = new HashMap<>();
		wires = new HashSet<>();
		initComponents();

		canvas.addZoomedRenderer(gc -> components.forEach(c -> drawComponent(gc, c)));
		canvas.addZoomedRenderer(gc -> wires.forEach(w -> w.render(gc)));
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
		WireArray r = new WireArray(1, WIRE_DELAY);
		WireArray s = new WireArray(1, WIRE_DELAY);
		WireArray t2 = new WireArray(1, WIRE_DELAY);
		WireArray t1 = new WireArray(1, WIRE_DELAY);
		WireArray q = new WireArray(1, WIRE_DELAY);
		WireArray nq = new WireArray(1, WIRE_DELAY);

		GUIManualSwitch rIn = addComponent(new GUIManualSwitch(r), 100, 100);
		GUIManualSwitch sIn = addComponent(new GUIManualSwitch(s), 100, 200);
		GUIOrGate or1 = addComponent(new GUIOrGate(OR_DELAY, t1, r, nq), 160, 102.5);
		GUIOrGate or2 = addComponent(new GUIOrGate(OR_DELAY, t2, q, s), 160, 192.5);
		GUINotGate not1 = addComponent(new GUINotGate(NOT_DELAY, t1, q), 200, 107.5);
		GUINotGate not2 = addComponent(new GUINotGate(NOT_DELAY, t2, nq), 200, 197.5);

		WireConnectionPoint p1 = addComponent(new WireConnectionPoint(q, 2), 250, 112.5);
		WireConnectionPoint p2 = addComponent(new WireConnectionPoint(nq, 2), 250, 202.5);

		addWire(rIn, 0, or1, 0);
		addWire(sIn, 0, or2, 1);
		addWire(or1, 2, not1, 0);
		addWire(or2, 2, not2, 0);
		addWire(not1, 1, p1, 0);
		addWire(not2, 1, p2, 0);
		addWire(p1, 1, or2, 0, new Point(250, 130), new Point(140, 185), new Point(140, 197.5));
		addWire(p2, 1, or1, 1, new Point(250, 185), new Point(140, 130), new Point(140, 117.5));
	}
	/**
	 * Returns the given component for convenience.
	 */
	private <C extends BasicGUIComponent> C addComponent(C component, double x, double y)
	{
		components.add(component);
		componentPositions.put(component, new Point(x, y));
		return component;
	}
	private void addWire(BasicGUIComponent component1, int component1ConnectionIndex, BasicGUIComponent component2, int component2ConnectionIndex, Point... path)
	{
		wires.add(new GUIWire(canvas::redrawThreadsafe, component1, component1ConnectionIndex, componentPositions.get(component1), component2, component2ConnectionIndex, componentPositions.get(component2), path));
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
		AtomicBoolean running = new AtomicBoolean(true);
		Thread simulationThread = new Thread(() ->
		{
			while(running.get())
			{
				//always execute to keep timeline from "hanging behind" for too long
				Simulation.TIMELINE.executeUpTo(System.currentTimeMillis(), System.currentTimeMillis() + 10);
				long sleepTime;
				if(Simulation.TIMELINE.hasNext())
				{
					sleepTime = Simulation.TIMELINE.nextEventTime() - System.currentTimeMillis();
				} else
					sleepTime = 100;
				try
				{
					if(sleepTime > 0)
						Thread.sleep(sleepTime);
				} catch(InterruptedException e)
				{} //it is normal execution flow to be interrupted
			}
		});
		simulationThread.start();
		Simulation.TIMELINE.addEventAddedListener(event ->
		{
			if(event.getTiming() >= System.currentTimeMillis() / (double) 1)
				simulationThread.interrupt();
		});

		shell.open();
		while(!shell.isDisposed())
			if(!display.readAndDispatch())
				display.sleep();
		running.set(false);
		simulationThread.interrupt();
	}

	public static void main(String[] args)
	{
		new LogicUI().run();
	}
}