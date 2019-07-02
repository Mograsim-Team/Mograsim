package net.mograsim.logic.ui.serializing;

import java.util.function.Supplier;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;

/**
 * A {@link SubmodelComponent} which was created by deserializing a JSON file.
 * 
 * @author Daniel Kirschten
 */
public interface DeserializedSubmodelComponentI
{
	public ViewModelModifiable getSubmodelModifiable();

	/**
	 * Sets the identifier delegate used by this deserialized component.
	 * 
	 * @author Daniel Kirschten
	 */
	public void setIdentifierDelegate(Supplier<String> identifierDelegate);
}