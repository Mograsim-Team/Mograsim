package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIxor extends SimpleRectangularSubmodelComponent
{
	public GUIxor(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIxor(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIxor", name);
		setSubmodelScale(.4);
		setInputPins("A", "B");
		setOutputPins("Y");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getSubmodelPin("A");
		Pin B = getSubmodelPin("B");
		Pin Y = getSubmodelPin("Y");

		GUINandGate nandAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYA = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB = new WireCrossPoint(submodelModifiable, 1);

		nandAB.moveTo(7.5, 15);
		nandYA.moveTo(35, 2.5);
		nandYB.moveTo(35, 27.5);
		nandY.moveTo(62.5, 15);
		cpA.moveCenterTo(5, 12.5);
		cpB.moveCenterTo(5, 37.5);
		cpAB.moveCenterTo(30, 25);

		new GUIWire(submodelModifiable, A, cpA, new Point[0]);
		new GUIWire(submodelModifiable, B, cpB, new Point[0]);
		new GUIWire(submodelModifiable, cpA, nandAB.getPin("A"), new Point(5, 20));
		new GUIWire(submodelModifiable, cpB, nandAB.getPin("B"), new Point(5, 30));
		new GUIWire(submodelModifiable, nandAB.getPin("Y"), cpAB);
		new GUIWire(submodelModifiable, cpAB, nandYA.getPin("B"), new Point(30, 17.5));
		new GUIWire(submodelModifiable, cpAB, nandYB.getPin("A"), new Point(30, 32.5));
		new GUIWire(submodelModifiable, cpA, nandYA.getPin("A"), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpB, nandYB.getPin("B"), new Point(5, 42.5));
		new GUIWire(submodelModifiable, nandYA.getPin("Y"), nandY.getPin("A"));
		new GUIWire(submodelModifiable, nandYB.getPin("Y"), nandY.getPin("B"));
		new GUIWire(submodelModifiable, nandY.getPin("Y"), Y);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIxor.class.getCanonicalName(), (m, p, n) -> new GUIxor(m, n));
	}
}