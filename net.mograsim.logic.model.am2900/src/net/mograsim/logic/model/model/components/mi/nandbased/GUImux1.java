package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUImux1 extends SimpleRectangularSubmodelComponent
{
	public GUImux1(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUImux1(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUImux1", name);
		setSubmodelScale(.4);
		setInputPins("S0", "I0", "I1");
		setOutputPins("Y");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents()
	{
		Pin S0 = getSubmodelPin("S0");
		Pin I0 = getSubmodelPin("I0");
		Pin I1 = getSubmodelPin("I1");
		Pin Y = getSubmodelPin("Y");

		GUINandGate nandS0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp0 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);

		nandS0.moveTo(10, 7.5);
		nandI0.moveTo(35, 22.5);
		nandI1.moveTo(35, 47.5);
		nandY.moveTo(60, 30);
		cp0.moveCenterTo(5, 12.5);
		cp1.moveCenterTo(5, 22.5);

		new GUIWire(submodelModifiable, S0, cp0, new Point[0]);
		new GUIWire(submodelModifiable, cp0, nandS0.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cp0, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nandS0.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nandS0.getPin("Y"), nandI0.getPin("A"));
		new GUIWire(submodelModifiable, I0, nandI0.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cp1, nandI1.getPin("A"), new Point(5, 52.5));
		new GUIWire(submodelModifiable, I1, nandI1.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nandI0.getPin("Y"), nandY.getPin("A"));
		new GUIWire(submodelModifiable, nandI1.getPin("Y"), nandY.getPin("B"));
		new GUIWire(submodelModifiable, nandY.getPin("Y"), Y);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUImux1.class.getCanonicalName(), (m, p, n) -> new GUImux1(m, n));
	}
}