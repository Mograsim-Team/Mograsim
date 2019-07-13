package net.mograsim.logic.model.editor.states;

import java.util.Map;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.handles.Handle;

public class GrabState extends EditorState
{
	private Map<Handle, Point> offsets;
	
	public GrabState(Editor editor, StateManager manager)
	{
		super(editor, manager);
	}

	@Override
	public void onEntry()
	{
		 offsets = editor.getSelection().calculateOffsets();
	}
	
	@Override
	public void grab()
	{
		manager.setState(new SelectionState(editor, manager));
	}

	@Override
	public void mouseMoved(double x, double y)
	{
		editor.moveHandles(x, y, offsets);
	}

	@Override
	public void clicked(Point clicked, int stateMask)
	{
		manager.setState(new SelectionState(editor, manager));
	}
}
