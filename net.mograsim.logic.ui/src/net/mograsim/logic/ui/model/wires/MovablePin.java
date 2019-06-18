package net.mograsim.logic.ui.model.wires;

import net.mograsim.logic.ui.model.components.GUIComponent;

public class MovablePin extends Pin
{
	public MovablePin(GUIComponent component, String name, int logicWidth, double relX, double relY)
	{
		super(component, name, logicWidth, relX, relY);
	}

	@Override
	public void setRelPos(double relX, double relY)
	{
		super.setRelPos(relX, relY);
	}
}