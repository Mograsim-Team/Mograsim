package net.mograsim.logic.ui.util;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

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

	public GUIWire connect(WireCrossPoint a, GUIComponent b, String pinB)
	{
		return connect(a.getPin(), b.getPin(pinB));
	}

	public GUIWire connect(Pin a, GUIComponent b, String pinB)
	{
		return connect(a, b.getPin(pinB));
	}

	public GUIWire connect(GUIComponent a, WireCrossPoint b, String pinA)
	{
		return connect(a.getPin(pinA), b.getPin());
	}

	public GUIWire connect(WireCrossPoint a, WireCrossPoint b)
	{
		return connect(a.getPin(), b.getPin());
	}

	public GUIWire connect(Pin a, WireCrossPoint b)
	{
		return connect(a, b.getPin());
	}

	public GUIWire connect(GUIComponent a, Pin b, String pinA)
	{
		return connect(a.getPin(pinA), b);
	}

	public GUIWire connect(WireCrossPoint a, Pin b)
	{
		return connect(a.getPin(), b);
	}

	public GUIWire connect(Pin a, Pin b)
	{
		return new GUIWire(model, a, b);
	}

	public static ModellingTool createFor(ViewModelModifiable model)
	{
		return new ModellingTool(model);
	}
}
