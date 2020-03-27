package net.mograsim.logic.model;

import static net.mograsim.logic.model.preferences.RenderPreferences.DRAG_BUTTON;
import static net.mograsim.logic.model.preferences.RenderPreferences.ZOOM_BUTTON;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.preferences.RenderPreferences;

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

	public LogicUIStandaloneGUI(LogicModel model, RenderPreferences renderPrefs)
	{
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		ui = new LogicUICanvas(shell, SWT.NONE, model, renderPrefs);

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		// TODO add a listener
		userInput.buttonDrag = renderPrefs.getInt(DRAG_BUTTON);
		// TODO add a listener
		userInput.buttonZoom = renderPrefs.getInt(ZOOM_BUTTON);
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