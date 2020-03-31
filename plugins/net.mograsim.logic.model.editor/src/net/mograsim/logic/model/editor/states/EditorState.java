package net.mograsim.logic.model.editor.states;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.handles.Handle.HandleClickInfo;
import net.mograsim.logic.model.editor.handles.InterfacePinHandle;

public abstract class EditorState
{
	protected final Editor editor;
	protected final StateManager manager;

	public EditorState(Editor session, StateManager manager)
	{
		this.editor = session;
		this.manager = manager;
	}

	// These methods are intended to be overridden
	//@formatter:off
    public void add() {/**/}
    public void delete() {/**/}
    public void copy() {/**/}
    public void paste() {/**/}
    public void duplicate() {/**/}
    public void grab() {/**/}
    @SuppressWarnings("unused") public void mouseMoved(double x, double y) {/**/}    
    @SuppressWarnings("unused") public void select(Point pos, boolean additive) {/**/}
    public void boxSelect() {/**/}
    public void onEntry() {/**/}
    public void onExit() {/**/}
    @SuppressWarnings("unused") public void clicked(InterfacePinHandle interfacePinHandle, int stateMask) {/**/}
    @SuppressWarnings("unused") public void clickedEmpty(Point clicked, int stateMask) {/**/}
    @SuppressWarnings("unused") public void clicked(Point clicked, int stateMask) {/**/}
    @SuppressWarnings({ "unused", "static-method" }) public boolean clickedHandle(HandleClickInfo handleClickInfo) {return false;}
	//@formatter:on
}
