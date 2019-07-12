package net.mograsim.logic.ui.editor.states;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.editor.Editor;
import net.mograsim.logic.ui.editor.handles.Handle.HandleClickInfo;

public class StateManager
{
	private EditorState state;

	public StateManager(Editor session)
	{
		state = new SelectionState(session, this);
		state.onEntry();
	}

	public EditorState getState()
	{
		return state;
	}

	public void setState(EditorState state)
	{
		this.state.onExit();
		this.state = state;
		state.onEntry();
	}

	public void add()
	{
		state.add();
	}

	public void delete()
	{
		state.delete();
	}

	public void copy()
	{
		state.copy();
	}

	public void paste()
	{
		state.paste();
	}

	public void duplicate()
	{
		state.duplicate();
	}

	public void grab()
	{
		state.grab();
	}

	public void mouseMoved(double x, double y)
	{
		state.mouseMoved(x, y);
	}

	public void select(Point pos, boolean additive)
	{
		state.select(pos, additive);
	}

	public boolean clickedHandle(HandleClickInfo handleClickInfo)
	{
		return state.clickedHandle(handleClickInfo);
	}

	public void boxSelect()
	{
		state.boxSelect();
	}
}
