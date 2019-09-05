package net.mograsim.logic.model.model;

import java.util.Set;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;

public class LogicModelModifiable extends LogicModel
{
	public String getDefaultComponentName(ModelComponent component)
	{
		Set<String> componentNames = getComponentsByName().keySet();
		// TODO get the ID of component
		// The following does not work because this method is called in the constructor of DeserializedSubmodelComponent at a time where
		// idForSerializingOverride is not yet set
//		String componentID = null;
//		if (component instanceof DeserializedSubmodelComponent)
//			componentID = ((DeserializedSubmodelComponent) component).idForSerializingOverride;
//		if (componentID == null)
//			componentID = component.getClass().getSimpleName();
		String componentID = component.getClass().getSimpleName();
		String nameBase = componentID + '#';
		for (int i = 0;; i++)
		{
			String nameCandidate = nameBase + i;
			if (!componentNames.contains(nameCandidate))
				return nameCandidate;
		}
	}

	public String getDefaultWireName()
	{
		Set<String> wireNames = getWiresByName().keySet();
		for (int i = 0;; i++)
		{
			String nameCandidate = "unnamedWire#" + i;
			if (!wireNames.contains(nameCandidate))
				return nameCandidate;
		}
	}

	@Override
	public void componentCreated(ModelComponent component, Runnable destroyed)
	{
		super.componentCreated(component, destroyed);
	}

	@Override
	public void destroyComponent(ModelComponent component)
	{
		super.destroyComponent(component);
	}

	@Override
	public void wireCreated(ModelWire wire, Runnable destroyed)
	{
		super.wireCreated(wire, destroyed);
	}

	@Override
	public void destroyWire(ModelWire wire)
	{
		super.destroyWire(wire);
	}
}