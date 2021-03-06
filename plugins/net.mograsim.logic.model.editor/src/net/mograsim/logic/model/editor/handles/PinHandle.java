package net.mograsim.logic.model.editor.handles;

import net.mograsim.logic.model.model.wires.Pin;

public abstract class PinHandle extends Handle
{
	public PinHandle(int priority)
	{
		super(priority);
	}

	public abstract Pin getPin();

	public abstract double getCenterX();

	public abstract double getCenterY();
}
