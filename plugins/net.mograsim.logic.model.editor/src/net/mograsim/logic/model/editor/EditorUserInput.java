package net.mograsim.logic.model.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.ui.EditorGUI;

public class EditorUserInput
{
	private final EditorGUI gui;

	public EditorUserInput(Editor editor)
	{
		this.gui = editor.gui;
		gui.logicCanvas.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				Point clicked = editor.gui.logicCanvas.canvasToWorldCoords(e.x, e.y);
				switch (e.button)
				{
				case 1:
					editor.handleManager.click(clicked, e.stateMask);
					break;
				}

			}

			@Override
			public void mouseUp(MouseEvent e)
			{
			}
		});

		gui.logicCanvas.addMouseMoveListener((e) ->
		{
			Point dest = editor.gui.logicCanvas.canvasToWorldCoords(e.x, e.y);
			editor.stateManager.mouseMoved(dest.x, dest.y);
		});

		gui.logicCanvas.addKeyListener(new KeyListener()
		{

			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				switch (e.keyCode)
				{
				case 'c':
					if ((e.stateMask & SWT.CTRL) == SWT.CTRL)
						editor.stateManager.copy();
					break;
				case 'v':
					if ((e.stateMask & SWT.CTRL) == SWT.CTRL)
						editor.stateManager.paste();
					break;
				case 'd':
					if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT)
						editor.stateManager.duplicate();
					break;
				case 'g':
					editor.stateManager.grab();
					break;
				case 'r':
					editor.stateManager.delete();
					break;
				case 's':
					if ((e.stateMask & SWT.CTRL) == SWT.CTRL)
						editor.save();
					break;
				case 'a':
					if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT)
						editor.stateManager.add();
					break;
				case 'h':
					editor.stateManager.boxSelect();
					break;
				}

			}
		});
	}

	public Point getCanvasMousePosition()
	{
		return new Point(gui.logicCanvas.toControl(gui.display.getCursorLocation()));
	}

	public Point getWorldMousePosition()
	{
		return gui.logicCanvas.canvasToWorldCoords(getCanvasMousePosition());
	}
}