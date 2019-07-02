package net.mograsim.logic.ui.model.components.submodels;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.Pin;

public class SubmodelInterface extends GUIComponent
{
	public SubmodelInterface(ViewModelModifiable model)
	{
		super(model);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{// nothing to do here
	}

	/**
	 * {@inheritDoc}<br>
	 * This method is only marked public in {@link SubmodelInterface} for {@link SubmodelComponent} to be able to add / remove pins to /
	 * from a SubmodelInterface.<br>
	 * 
	 * @see GUIComponent#removePin(Pin)
	 * 
	 * @author Daniel Kirschten
	 */
	@Override
	public void addPin(Pin pin)
	{
		super.addPin(pin);
	}

	/**
	 * {@inheritDoc}<br>
	 * This method is only marked public in {@link SubmodelInterface} for {@link SubmodelComponent} to be able to add / remove pins to /
	 * from a SubmodelInterface.<br>
	 * 
	 * @see GUIComponent#removePin(Pin)
	 * 
	 * @author Daniel Kirschten
	 */
	@Override
	protected void removePin(String name)
	{
		super.removePin(name);
	}
}