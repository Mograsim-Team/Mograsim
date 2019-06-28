package net.mograsim.logic.ui.util;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.ConnectionPoint;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class ModellingTool
{
	private ViewModelModifiable model;

	ModellingTool(ViewModelModifiable model)
	{
		this.model = model;
	}

	public GUIWire connect(GUIComponent a, GUIComponent b, String pinA, String pinB)
	{
		return connect(a.getPin(pinA), b.getPin(pinB));
	}

	public GUIWire connect(ConnectionPoint a, GUIComponent b, String pinB)
	{
		return connect(a, b.getPin(pinB));
	}

	public GUIWire connect(ConnectionPoint a, ConnectionPoint b)
	{
		return new GUIWire(model, a, b);
	}

	public static ModellingTool createFor(ViewModelModifiable model)
	{
		return new ModellingTool(model);
	}
}
