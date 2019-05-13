import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

public class LogicUIPlayground
{
	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		ZoomableCanvas canvas = new ZoomableCanvas(shell, SWT.NONE);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		canvas.addZoomedRenderer(gc -> gc.drawText("Test", 0, 0));
		new ZoomableCanvasUserInput(canvas).enableUserInput();
		new ZoomableCanvasOverlay(canvas, null).enableScale();
		shell.open();
		while(!shell.isDisposed())
			if(!display.readAndDispatch())
				display.sleep();
	}
}