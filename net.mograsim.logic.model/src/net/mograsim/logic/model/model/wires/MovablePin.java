package net.mograsim.logic.model.model.wires;

import net.mograsim.logic.model.model.components.GUIComponent;

/**
 * Exactly like {@link Pin}, but {@link #setRelPos(double, double) setRelPos(...)} is public.
 * 
 * @author Daniel Kirschten
 */
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