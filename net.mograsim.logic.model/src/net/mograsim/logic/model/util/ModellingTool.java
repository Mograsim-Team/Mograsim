package net.mograsim.logic.model.util;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;

public class ModellingTool
{
	private ViewModelModifiable model;

	ModellingTool(ViewModelModifiable model)
	{
		this.model = model;
	}

	public GUIWire connect(GUIComponent a, GUIComponent b, String pinA, String pinB)
	{
		return connect(null, a, b, pinA, pinB);
	}

	public GUIWire connect(WireCrossPoint a, GUIComponent b, String pinB)
	{
		return connect(null, a, b, pinB);
	}

	public GUIWire connect(Pin a, GUIComponent b, String pinB)
	{
		return connect(null, a, b, pinB);
	}

	public GUIWire connect(GUIComponent a, WireCrossPoint b, String pinA)
	{
		return connect(null, a, b, pinA);
	}

	public GUIWire connect(WireCrossPoint a, WireCrossPoint b)
	{
		return connect(null, a, b);
	}

	public GUIWire connect(Pin a, WireCrossPoint b)
	{
		return connect(null, a, b);
	}

	public GUIWire connect(GUIComponent a, Pin b, String pinA)
	{
		return connect(null, a, b, pinA);
	}

	public GUIWire connect(WireCrossPoint a, Pin b)
	{
		return connect(null, a, b);
	}

	public GUIWire connect(Pin a, Pin b)
	{
		return connect(null, a, b);
	}

	public GUIWire connect(String name, GUIComponent a, GUIComponent b, String pinA, String pinB)
	{
		return connect(name, a.getPin(pinA), b.getPin(pinB));
	}

	public GUIWire connect(String name, WireCrossPoint a, GUIComponent b, String pinB)
	{
		return connect(name, a.getPin(), b.getPin(pinB));
	}

	public GUIWire connect(String name, Pin a, GUIComponent b, String pinB)
	{
		return connect(name, a, b.getPin(pinB));
	}

	public GUIWire connect(String name, GUIComponent a, WireCrossPoint b, String pinA)
	{
		return connect(name, a.getPin(pinA), b.getPin());
	}

	public GUIWire connect(String name, WireCrossPoint a, WireCrossPoint b)
	{
		return connect(name, a.getPin(), b.getPin());
	}

	public GUIWire connect(String name, Pin a, WireCrossPoint b)
	{
		return connect(name, a, b.getPin());
	}

	public GUIWire connect(String name, GUIComponent a, Pin b, String pinA)
	{
		return connect(name, a.getPin(pinA), b);
	}

	public GUIWire connect(String name, WireCrossPoint a, Pin b)
	{
		return connect(name, a.getPin(), b);
	}

	public GUIWire connect(String name, Pin a, Pin b)
	{
		return new GUIWire(model, name, a, b);
	}

	public static ModellingTool createFor(ViewModelModifiable model)
	{
		return new ModellingTool(model);
	}
}
