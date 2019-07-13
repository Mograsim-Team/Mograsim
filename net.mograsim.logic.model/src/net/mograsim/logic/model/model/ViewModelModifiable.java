package net.mograsim.logic.model.model;

import java.util.Set;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.GUIWire;

public class ViewModelModifiable extends ViewModel
{
	public String getDefaultComponentName(GUIComponent component)
	{
		Set<String> componentNames = getComponentsByName().keySet();
		String nameBase = component.getClass().getSimpleName() + '#';
		for (int i = 0;; i++)
		{
			String nameCandidate = nameBase + i;
			if (!componentNames.contains(nameCandidate))
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