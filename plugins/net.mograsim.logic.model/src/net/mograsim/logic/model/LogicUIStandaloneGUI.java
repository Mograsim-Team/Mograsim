package net.mograsim.logic.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.preferences.Preferences;

/**
 * Standalone simulation visualizer graphical user interface.
 * 
 * @author Daniel Kirschten
 */
public class LogicUIStandaloneGUI implements Runnable
{
	private final Display display;
	private final Shell shell;
	private final LogicUICanvas ui;

	public LogicUIStandaloneGUI(LogicModel model)
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		ui = new LogicUICanvas(shell, SWT.NONE, model);

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = Preferences.current().getInt("net.mograsim.logic.model.button.drag");
		userInput.buttonZoom = Preferences.current().getInt("net.mograsim.logic.model.button.zoom");
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(ui, null).enableScale();
	}

	public LogicUICanvas getLogicUICanvas()
	{
		return ui;
	}

	/**
	 * Opens the UI shell. Returns when the shell is closed.
	 */
	@Override
	public void run()
	{
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}