package net.mograsim.logic.ui.editor.handles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;

public class CornerHandle extends Handle
{
	private final static int LENGTH = 5;
	private final DeserializedSubmodelComponent toBeEdited;
	private boolean selected;
	
	public CornerHandle(DeserializedSubmodelComponent toBeEdited)
	{
		super();
		this.toBeEdited = toBeEdited;
		setSize(LENGTH, LENGTH);
		initPos();
	}

	@Override
	protected void render(GeneralGC gc)
	{
		gc.setBackground(Display.getCurrent().getSystemColor(selected ? SWT.COLOR_YELLOW : SWT.COLOR_DARK_GREEN));
		gc.fillRectangle(getPosX(), getPosY(), LENGTH, LENGTH);
	}

	private void initPos()
	{
		Rectangle bounds = toBeEdited.getBounds();
		double subScale = toBeEdited.getSubmodelScale();
		moveTo(bounds.width / subScale, bounds.height / subScale);
	}
	
	@Override
	public void reqMove(double x, double y)
	{
		moveTo(x, y);
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
	protected void moveTo(double x, double y)
	{
		super.moveTo(x, y);
		double subScale = toBeEdited.getSubmodelScale();
		toBeEdited.setSize(x * subScale, y * subScale);
	}
	
	@Override
	public HandleType getType()
	{
		return HandleType.CORNER;
	}
}