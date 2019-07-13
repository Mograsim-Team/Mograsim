package net.mograsim.logic.model.editor.handles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;

public class InterfacePinHandle extends PinHandle
{
	private final static int CIRCLE_DIAM = 4, CIRCLE_RADIUS = CIRCLE_DIAM / 2, SELECTED_CIRCLE_OFFSET = 1,
			SELECTED_CIRCLE_DIAM = SELECTED_CIRCLE_OFFSET * 2 + CIRCLE_DIAM;
	private final MovablePin parent;
	private final DeserializedSubmodelComponent owner;
	private boolean selected = false;

	public InterfacePinHandle(MovablePin parent, DeserializedSubmodelComponent pinOwner)
	{
		super();
		this.parent = parent;
		this.owner = pinOwner;
		setSize(CIRCLE_DIAM, CIRCLE_DIAM);
		initPos();
	}

	private void initPos()
	{
		Point pos = parent.getPos();
		moveTo(pos.x - CIRCLE_RADIUS, pos.y - CIRCLE_RADIUS);
	}

	@Override
	protected void render(GeneralGC gc)
	{
		double x = getPosX(), y = getPosY();

		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		gc.drawText(parent.name, x + CIRCLE_DIAM, y + CIRCLE_DIAM, true);
		if (selected)
		{
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			gc.drawOval(x - SELECTED_CIRCLE_OFFSET, y - SELECTED_CIRCLE_OFFSET, SELECTED_CIRCLE_DIAM,
					SELECTED_CIRCLE_DIAM);
		}
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		gc.fillOval(getPosX(), getPosY(), CIRCLE_DIAM, CIRCLE_DIAM);
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
	public void reqDelete()
	{
		owner.removeSubmodelInterface(parent.name);
	}
	
	@Override
	protected void moveTo(double x, double y)
	{
		super.moveTo(x, y);
		parent.setRelPos(getCenterX(), getCenterY());
	}

	@Override
	public double getCenterX()
	{
		return getPosX() + CIRCLE_RADIUS;
	}

	@Override
	public double getCenterY()
	{
		return getPosY() + CIRCLE_RADIUS;
	}

	@Override
	public HandleType getType()
	{
		return HandleType.INTERFACE_PIN;
	}

	@Override
	public Pin getPin()
	{
		return parent;
	}
}