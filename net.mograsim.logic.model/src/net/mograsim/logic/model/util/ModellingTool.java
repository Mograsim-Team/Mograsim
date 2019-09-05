package net.mograsim.logic.model.util;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;

public class ModellingTool
{
	private ViewModelModifiable model;

	ModellingTool(ViewModelModifiable model)
	{
		this.model = model;
	}

	public ModelWire connect(ModelComponent a, ModelComponent b, String pinA, String pinB)
	{
		return connect(null, a, b, pinA, pinB);
	}

	public ModelWire connect(ModelWireCrossPoint a, ModelComponent b, String pinB)
	{
		return connect(null, a, b, pinB);
	}

	public ModelWire connect(Pin a, ModelComponent b, String pinB)
	{
		return connect(null, a, b, pinB);
	}

	public ModelWire connect(ModelComponent a, ModelWireCrossPoint b, String pinA)
	{
		return connect(null, a, b, pinA);
	}

	public ModelWire connect(ModelWireCrossPoint a, ModelWireCrossPoint b)
	{
		return connect(null, a, b);
	}

	public ModelWire connect(Pin a, ModelWireCrossPoint b)
	{
		return connect(null, a, b);
	}

	public ModelWire connect(ModelComponent a, Pin b, String pinA)
	{
		return connect(null, a, b, pinA);
	}

	public ModelWire connect(ModelWireCrossPoint a, Pin b)
	{
		return connect(null, a, b);
	}

	public ModelWire connect(Pin a, Pin b)
	{
		return connect(null, a, b);
	}

	public ModelWire connect(String name, ModelComponent a, ModelComponent b, String pinA, String pinB)
	{
		return connect(name, a.getPin(pinA), b.getPin(pinB));
	}

	public ModelWire connect(String name, ModelWireCrossPoint a, ModelComponent b, String pinB)
	{
		return connect(name, a.getPin(), b.getPin(pinB));
	}

	public ModelWire connect(String name, Pin a, ModelComponent b, String pinB)
	{
		return connect(name, a, b.getPin(pinB));
	}

	public ModelWire connect(String name, ModelComponent a, ModelWireCrossPoint b, String pinA)
	{
		return connect(name, a.getPin(pinA), b.getPin());
	}

	public ModelWire connect(String name, ModelWireCrossPoint a, ModelWireCrossPoint b)
	{
		return connect(name, a.getPin(), b.getPin());
	}

	public ModelWire connect(String name, Pin a, ModelWireCrossPoint b)
	{
		return connect(name, a, b.getPin());
	}

	public ModelWire connect(String name, ModelComponent a, Pin b, String pinA)
	{
		return connect(name, a.getPin(pinA), b);
	}

	public ModelWire connect(String name, ModelWireCrossPoint a, Pin b)
	{
		return connect(name, a.getPin(), b);
	}

	public ModelWire connect(String name, Pin a, Pin b)
	{
		return new ModelWire(model, name, a, b);
	}

	public static ModellingTool createFor(ViewModelModifiable model)
	{
		return new ModellingTool(model);
	}
}
