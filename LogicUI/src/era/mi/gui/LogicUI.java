package era.mi.gui;

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

import era.mi.gui.components.BasicGUIComponent;
import era.mi.gui.wires.GUIWire;
import era.mi.logic.Simulation;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

/**
 * Standalone simulation visualizer.
 * 
 * @author Daniel Kirschten
 */
public class LogicUI
{
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

		canvas.addZoomedRenderer(gc -> components.forEach(c -> drawComponent(gc, c)));
		canvas.addZoomedRenderer(gc -> wires.forEach(w -> w.render(gc)));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(canvas);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(canvas, null).enableScale();
		canvas.addListener(SWT.MouseDown, this::mouseDown);
	}
	/**
	 * Add a component to be drawn.
	 * Returns the given component for convenience.
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
	 * Add a graphical wire between the given connection points of the given components.
	 * The given components have to be added and the given connection points have to be connected logically first.
	 * 
	 * @author Daniel Kirschten
	 */
	public void addWire(BasicGUIComponent component1, int component1ConnectionIndex, BasicGUIComponent component2, int component2ConnectionIndex, Point... path)
	{
		wires.add(new GUIWire(canvas::redrawThreadsafe, component1, component1ConnectionIndex, componentPositions.get(component1), component2, component2ConnectionIndex, componentPositions.get(component2), path));
	}
	private void drawComponent(GeneralGC gc, BasicGUIComponent component)
	{
		TranslatedGC tgc = new TranslatedGC(gc, componentPositions.get(component));
		component.render(tgc);
		tgc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
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

	/**
	 * Start the simulation timeline, and open the UI shell.
	 * Returns when the shell is closed.
	 */
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
}