package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIhalfadder extends SimpleRectangularSubmodelComponent
{
	public GUIhalfadder(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIhalfadder(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIhalfadder", name);
		setSubmodelScale(.4);
		setInputPins("A", "B");
		setOutputPins("Y", "_Z");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getSubmodelPin("A");
		Pin B = getSubmodelPin("B");
		Pin Y = getSubmodelPin("Y");
		Pin _Z = getSubmodelPin("_Z");

		GUINandGate nand_Z = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYA = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp_Z = new WireCrossPoint(submodelModifiable, 1);

		nand_Z.moveTo(10, 15);
		nandYA.moveTo(40, 2.5);
		nandYB.moveTo(40, 27.5);
		nandY.moveTo(65, 2.5);
		cpA.moveCenterTo(5, 12.5);
		cpB.moveCenterTo(5, 37.5);
		cp_Z.moveCenterTo(35, 25);

		new GUIWire(submodelModifiable, A, cpA, new Point[0]);
		new GUIWire(submodelModifiable, cpA, nandYA.getPin("A"), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpA, nand_Z.getPin("A"), new Point(5, 20));
		new GUIWire(submodelModifiable, B, cpB, new Point[0]);
		new GUIWire(submodelModifiable, cpB, nandYB.getPin("B"), new Point(5, 42.5));
		new GUIWire(submodelModifiable, cpB, nand_Z.getPin("B"), new Point(5, 30));
		new GUIWire(submodelModifiable, nand_Z.getPin("Y"), cp_Z, new Point[0]);
		new GUIWire(submodelModifiable, cp_Z, _Z, new Point(80, 25), new Point(80, 37.5));
		new GUIWire(submodelModifiable, cp_Z, nandYA.getPin("B"), new Point(35, 17.5));
		new GUIWire(submodelModifiable, cp_Z, nandYB.getPin("A"), new Point(35, 32.5));
		new GUIWire(submodelModifiable, nandYA.getPin("Y"), nandY.getPin("A"), new Point(62.5, 12.5), new Point(62.5, 7.5));
		new GUIWire(submodelModifiable, nandYB.getPin("Y"), nandY.getPin("B"), new Point(62.5, 37.5), new Point(62.5, 17.5));
		new GUIWire(submodelModifiable, nandY.getPin("Y"), Y, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIhalfadder.class.getCanonicalName(), (m, p, n) -> new GUIhalfadder(m, n));
	}
}