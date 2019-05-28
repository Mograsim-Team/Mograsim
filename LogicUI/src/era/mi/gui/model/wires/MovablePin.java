package era.mi.gui.model.wires;

import era.mi.gui.model.components.GUIComponent;

public class MovablePin extends Pin
{
	public MovablePin(GUIComponent component, double relX, double relY)
	{
		super(component, relX, relY);
	}

	@Override
	public void setRelPos(double relX, double relY)
	{
		super.setRelPos(relX, relY);
	}
}