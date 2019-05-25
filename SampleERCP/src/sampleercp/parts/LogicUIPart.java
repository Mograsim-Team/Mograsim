package sampleercp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import era.mi.gui.LogicUICanvas;
import era.mi.gui.examples.RSLatchGUIExample;
import era.mi.logic.Simulation;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

public class LogicUIPart
{
	@Inject
	private MPart part;

	@PostConstruct
	public void create(Composite parent)
	{
		LogicUICanvas ui = new LogicUICanvas(parent, SWT.NONE);
		RSLatchGUIExample.addComponentsAndWires(ui);
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		Thread simulationThread = new Thread(() ->
		{
			// TODO find a better condition
			while (!ui.isDisposed())
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
	}
}