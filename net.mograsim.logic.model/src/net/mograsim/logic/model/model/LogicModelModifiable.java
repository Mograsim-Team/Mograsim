package net.mograsim.logic.model.model;

import java.util.Set;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.serializing.IdentifyParams;

public class LogicModelModifiable extends LogicModel
{
	public String getDefaultComponentName(ModelComponent component)
	{
		Set<String> componentNames = getComponentsByName().keySet();
		String componentID = component.getIDForSerializing(new IdentifyParams());
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