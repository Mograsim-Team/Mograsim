package net.mograsim.logic.ui.editor.handles;

import net.mograsim.logic.ui.model.wires.Pin;

public abstract class PinHandle extends Handle
{
	public PinHandle()
	{
		super();
	}

	public abstract Pin getPin();
	public abstract double getCenterX();
	public abstract double getCenterY();
}
