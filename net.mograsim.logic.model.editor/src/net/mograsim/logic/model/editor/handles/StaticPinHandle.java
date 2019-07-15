package net.mograsim.logic.model.editor.handles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.wires.Pin;

public class StaticPinHandle extends PinHandle
{
	private final static int CIRCLE_DIAM = 2;
	private final static int CIRCLE_RADIUS = 1;
	private final Pin parent;

	public StaticPinHandle(Pin parent)
	{
		super();
		this.parent = parent;
		setSize(CIRCLE_DIAM, CIRCLE_DIAM);
		parent.addPinMovedListener((p) -> updatePos());
		updatePos();
	}

	@Override
	protected void render(GeneralGC gc)
	{
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		gc.fillOval(getPosX(), getPosY(), CIRCLE_DIAM, CIRCLE_DIAM);
	}

	private void updatePos()
	{
		Point pos = parent.getPos();
		moveTo(pos.x - CIRCLE_RADIUS, pos.y - CIRCLE_RADIUS);
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
		return HandleType.STATIC_PIN;
	}

	@Override
	public Pin getPin()
	{
		return parent;
	}
}