package era.mi.gui;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import era.mi.logic.Simulation;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

/**
 * Standalone simulation visualizer.
 * 
 * @author Daniel Kirschten
 */
public class LogicUIStandalone
{
	private final Display display;
	private final Shell shell;
	private final LogicUICanvas ui;

	public LogicUIStandalone()
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		ui = new LogicUICanvas(shell, SWT.NONE);

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(ui, null).enableScale();
	}

	public LogicUICanvas getLogicUICanvas()
	{
		return ui;
	}

	/**
	 * Start the simulation timeline, and open the UI shell. Returns when the shell is closed.
	 */
	public void run()
	{
		AtomicBoolean running = new AtomicBoolean(true);
		Thread simulationThread = new Thread(() ->
		{
			while (running.get())
			{
				// always execute to keep timeline from "hanging behind" for too long
				Simulation.TIMELINE.executeUpTo(System.currentTimeMillis(), System.currentTimeMillis() + 10);
				long sleepTime;
				if (Simulation.TIMELINE.hasNext())
					sleepTime = Simulation.TIMELINE.nextEventTime() - System.currentTimeMillis();
				else
					sleepTime = 10;
				try
				{
					if (sleepTime > 0)
						Thread.sleep(sleepTime);
				}
				catch (InterruptedException e)
				{
				} // it is normal execution flow to be interrupted
			}
		});
		simulationThread.start();
		Simulation.TIMELINE.addEventAddedListener(event ->
		{
			if (event.getTiming() <= System.currentTimeMillis())
				simulationThread.interrupt();
		});

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		running.set(false);
		simulationThread.interrupt();
	}
}