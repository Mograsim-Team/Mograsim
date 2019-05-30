package net.mograsim.logic.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

/**
 * Standalone simulation visualizer graphical user interface.
 * 
 * @author Daniel Kirschten
 */
public class LogicUIStandaloneGUI
{
	private final Display display;
	private final Shell shell;
	private final LogicUICanvas ui;

	public LogicUIStandaloneGUI(ViewModel model)
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		ui = new LogicUICanvas(shell, SWT.NONE, model);

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(ui, null).enableScale();

		// TODO don't do this here
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
//		timeline = ViewLogicModelAdapter.convert(model, params);
	}

	public LogicUICanvas getLogicUICanvas()
	{
		return ui;
	}

	/**
	 * Opens the UI shell. Returns when the shell is closed.
	 */
	public void run()
	{
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}