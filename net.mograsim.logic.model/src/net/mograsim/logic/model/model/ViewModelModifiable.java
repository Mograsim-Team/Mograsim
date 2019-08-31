package net.mograsim.logic.model.model;

import java.util.Set;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.GUIWire;

//TODO a ViewModel is modifiable without casting to ViewModelModifiable via GUIWire::destroy and GUIComponent::destroy
public class ViewModelModifiable extends ViewModel
{
	public String getDefaultComponentName(GUIComponent component)
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
	public void componentCreated(GUIComponent component)
	{
		super.componentCreated(component);
	}

	@Override
	public void componentDestroyed(GUIComponent component)
	{
		super.componentDestroyed(component);
	}

	@Override
	public void wireCreated(GUIWire wire)
	{
		super.wireCreated(wire);
	}

	@Override
	public void wireDestroyed(GUIWire wire)
	{
		super.wireDestroyed(wire);
	}
}