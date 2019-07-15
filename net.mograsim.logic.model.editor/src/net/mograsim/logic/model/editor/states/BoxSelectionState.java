package net.mograsim.logic.model.editor.states;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas.ZoomedRenderer;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.Selection;
import net.mograsim.logic.model.editor.handles.Handle;

public class BoxSelectionState extends EditorState
{
	private Point origin;
	private double mX, mY;

	private final ZoomedRenderer boxRenderer = gc ->
	{
		if (origin != null)
		{
			gc.setLineWidth(0.5);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
			gc.drawRectangle(origin.x, origin.y, mX - origin.x, mY - origin.y);
		}
	};

	public BoxSelectionState(Editor editor, StateManager manager)
	{
		super(editor, manager);
	}

	@Override
	public void onEntry()
	{
		editor.gui.logicCanvas.addZoomedRenderer(boxRenderer);
		editor.gui.logicCanvas.redrawThreadsafe();
	}

	@Override
	public void onExit()
	{
		editor.gui.logicCanvas.removeZoomedRenderer(boxRenderer);
		editor.gui.logicCanvas.redrawThreadsafe();
	}

	@Override
	public void boxSelect()
	{
		manager.setState(new SelectionState(editor, manager));
	}

	@Override
	public void clicked(Point clicked, int stateMask)
	{
		if (origin == null)
		{
			origin = new Point(clicked.x, clicked.y);
			mX = clicked.x;
			mY = clicked.y;
		} else
		{
			Selection s = editor.getSelection();
			s.clear();
			boolean leftToRight = mX > origin.x, topToBottom = mY > origin.y;
			double x = leftToRight ? origin.x : mX, y = topToBottom ? origin.y : mY, width = leftToRight ? mX - origin.x : origin.x - mX,
					height = topToBottom ? mY - origin.y : origin.y - mY;

			Rectangle selected = new Rectangle(x, y, width, height);
			for (Handle h : editor.handleManager.getHandles())
			{
				Rectangle hBounds = h.getBounds();
				if (selected.contains(hBounds.x, hBounds.y) && selected.contains(hBounds.x + hBounds.width, hBounds.y + hBounds.height))
				{
					s.add(h);
				}
			}
			manager.setState(new SelectionState(editor, manager));
		}
	}

	@Override
	public void mouseMoved(double x, double y)
	{
		mX = x;
		mY = y;
		editor.gui.logicCanvas.redrawThreadsafe();
	}
}
