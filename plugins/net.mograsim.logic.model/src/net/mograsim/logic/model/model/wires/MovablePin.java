package net.mograsim.logic.model.model.wires;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;

/**
 * Exactly like {@link Pin}, but {@link #setRelPos(double, double) setRelPos(...)} is public.
 * 
 * @author Daniel Kirschten
 */
public class MovablePin extends Pin
{
	public MovablePin(LogicModelModifiable model, ModelComponent component, String name, int logicWidth, PinUsage usage, double relX,
			double relY)
	{
		super(model, component, name, logicWidth, usage, relX, relY);
	}

	@Override
	public void setRelPos(double relX, double relY)
	{
		super.setRelPos(relX, relY);
	}
}