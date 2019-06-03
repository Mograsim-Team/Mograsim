package net.mograsim.logic.ui.model;

import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class ViewModelModifiable extends ViewModel
{
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