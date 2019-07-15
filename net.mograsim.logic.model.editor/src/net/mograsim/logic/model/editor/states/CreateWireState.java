package net.mograsim.logic.model.editor.states;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas.ZoomedRenderer;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.handles.PinHandle;
import net.mograsim.logic.model.editor.handles.Handle.HandleClickInfo;

public class CreateWireState extends EditorState
{
	private final PinHandle origin;
	private double mX, mY;
	private ZoomedRenderer drawLine;

	public CreateWireState(Editor session, StateManager manager, PinHandle origin)
	{
		super(session, manager);
		this.origin = origin;
		mX = origin.getCenterX();
		mY = origin.getCenterY();
	}

	@Override
	public void mouseMoved(double x, double y)
	{
		this.mX = x;
		this.mY = y;
		editor.gui.logicCanvas.redraw();
	}

	@Override
	public void onEntry()
	{
		editor.getSelection().clear();
		drawLine = gc ->
		{
			gc.setLineWidth(1);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
			gc.drawLine((int) origin.getCenterX(), (int) origin.getCenterY(), (int) mX, (int) mY);
		};
		editor.gui.logicCanvas.addZoomedRenderer(drawLine);
		editor.gui.logicCanvas.redraw();
	}

	@Override
	public void onExit()
	{
		editor.gui.logicCanvas.removeZoomedRenderer(drawLine);
		editor.gui.logicCanvas.redraw();
	}

	@Override
	public boolean clickedHandle(HandleClickInfo handleClickInfo)
	{
		switch (handleClickInfo.clicked.getType())
		{
		case INTERFACE_PIN:
		case STATIC_PIN:
			try
			{
				editor.addWire(origin, (PinHandle) handleClickInfo.clicked);
			} 
			catch (IllegalArgumentException e)
			{
				Shell tmp = new Shell(Display.getCurrent());
				editor.dialogManager.openWarningDialog("Warning!", e.getMessage());
				tmp.dispose();
			}
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void clicked(Point clicked, int stateMask)
	{
		manager.setState(new SelectionState(editor, manager));
	}
}
