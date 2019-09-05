package net.mograsim.logic.model.model.components.submodels;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IdentifyParams;

public class SubmodelInterface extends ModelComponent
{
	public SubmodelInterface(LogicModelModifiable model, String name)
	{
		super(model, name);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{// nothing to do here
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		throw new UnsupportedOperationException("A SubmodelInterface can't be serialized. Use SubmodelComponent.addSubmodelInterface");
	}

	/**
	 * {@inheritDoc}<br>
	 * This method is only marked public in {@link SubmodelInterface} for {@link SubmodelComponent} to be able to add / remove pins to /
	 * from a SubmodelInterface.<br>
	 * 
	 * @see ModelComponent#removePin(Pin)
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
	 * @see ModelComponent#removePin(Pin)
	 * 
	 * @author Daniel Kirschten
	 */
	@Override
	protected void removePin(String name)
	{
		super.removePin(name);
	}
}