package net.mograsim.logic.ui.serializing;

import java.util.function.Supplier;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;

/**
 * A {@link SimpleRectangularSubmodelComponent} which was created by deserializing a JSON file.
 * 
 * @author Daniel Kirschten
 */
public class DeserializedSimpleRectangularSubmodelComponent extends SimpleRectangularSubmodelComponent
		implements DeserializedSubmodelComponentI
{
	public DeserializedSimpleRectangularSubmodelComponent(ViewModelModifiable model, int logicWidth, String label)
	{
		super(model, logicWidth, label);
	}

	@Override
	public ViewModelModifiable getSubmodelModifiable()
	{
		return submodelModifiable;
	}

	@Override
	public void setIdentifierDelegate(Supplier<String> identifierDelegate)
	{
		this.identifierDelegate = identifierDelegate;
	}

	@Override
	public void setSubmodelScale(double submodelScale)
	{
		super.setSubmodelScale(submodelScale);
	}

	@Override
	public void setInputPins(String... pinNames)
	{
		super.setInputPins(pinNames);
	}

	@Override
	public void setOutputPins(String... pinNames)
	{
		super.setOutputPins(pinNames);
	}
}