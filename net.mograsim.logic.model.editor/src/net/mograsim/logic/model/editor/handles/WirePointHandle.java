package net.mograsim.logic.model.editor.handles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.model.model.wires.GUIWire;

public class WirePointHandle extends Handle
{
	private final static int END_OFFSET = 4;
	private final HandleManager manager;
	private boolean selected = false;
	public final GUIWire parent;

	private int pointIndex;

	public WirePointHandle(HandleManager manager, GUIWire parent, int pointIndex)
	{
		super();
		this.manager = manager;
		this.parent = parent;
		this.pointIndex = pointIndex;
		setSize(END_OFFSET, END_OFFSET);
		Point pathPoint = parent.getPathPoint(pointIndex);
		moveTo(pathPoint.x, pathPoint.y);
	}

	void updatePos()
	{
		Point p = parent.getPathPoint(pointIndex);
		moveTo(p.x, p.y);
		callRedrawListeners();
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.setLineWidth(1.0);
		gc.setForeground(Display.getDefault().getSystemColor(selected ? SWT.COLOR_YELLOW : SWT.COLOR_BLUE));
		gc.drawLine(getPosX(), getPosY(), getPosX() + END_OFFSET, getPosY() + END_OFFSET);
	}

	@Override
	public void reqMove(double x, double y)
	{
		parent.setPathPoint(new Point(x, y), pointIndex);
	}

	@Override
	public void onSelect()
	{
		selected = true;
		callRedrawListeners();
	}

	@Override
	public void onDeselect()
	{
		selected = false;
		callRedrawListeners();
	}

	@Override
	public void reqDelete()
	{
		manager.destroyWirePointHandle(parent, this);
	}

	@Override
	public HandleType getType()
	{
		return HandleType.WIRE_POINT;
	}

	/**
	 * Sets the index of the {@link Point} within the parent {@link Wire}s path that is controlled by this handle
	 * 
	 * @param index Index of the Point in the Wires path.
	 * @throws IndexOutOfBoundsException
	 */
	public void setIndex(int index)
	{
		this.pointIndex = index;
		updatePos();
	}
}
