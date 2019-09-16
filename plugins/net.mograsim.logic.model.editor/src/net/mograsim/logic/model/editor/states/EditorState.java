package net.mograsim.logic.model.editor.states;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.handles.InterfacePinHandle;
import net.mograsim.logic.model.editor.handles.Handle.HandleClickInfo;

public abstract class EditorState
{
	protected final Editor editor;
	protected final StateManager manager;

	public EditorState(Editor session, StateManager manager)
	{
		this.editor = session;
		this.manager = manager;
	}

	//@formatter:off
    public void add() {}
    public void delete() {}
    public void copy() {}
    public void paste() {}
    public void duplicate() {}
    public void grab() {}
    public void mouseMoved(double x, double y) {}    
    public void select(Point pos, boolean additive) {}
    public void boxSelect() {}
    public void onEntry() {}
    public void onExit() {}
	public void clicked(InterfacePinHandle interfacePinHandle, int stateMask) {}
	public void clickedEmpty(Point clicked, int stateMask) {}
	public void clicked(Point clicked, int stateMask) {}
	public boolean clickedHandle(HandleClickInfo handleClickInfo) { return false; }
	//@formatter:on
}
