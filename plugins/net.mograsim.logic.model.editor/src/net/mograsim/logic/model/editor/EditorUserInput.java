package net.mograsim.logic.model.editor;

import static net.mograsim.logic.model.preferences.RenderPreferences.ACTION_BUTTON;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.ui.EditorGUI;

public class EditorUserInput
{
	private final EditorGUI gui;

	public EditorUserInput(Editor editor)
	{
		this.gui = editor.gui;
		gui.logicCanvas.addListener(SWT.MouseDown, e ->
		{
			Point clicked = editor.gui.logicCanvas.canvasToWorldCoords(e.x, e.y);
			if (e.button == editor.renderPrefs.getInt(ACTION_BUTTON))
				editor.handleManager.click(clicked, e.stateMask);
		});

		gui.logicCanvas.addMouseMoveListener(e ->
		{
			Point dest = editor.gui.logicCanvas.canvasToWorldCoords(e.x, e.y);
			editor.stateManager.mouseMoved(dest.x, dest.y);
		});

		gui.logicCanvas.addListener(SWT.KeyDown, e ->
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
			default:
				// don't react
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