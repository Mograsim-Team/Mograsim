package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUInand3 extends SimpleRectangularSubmodelComponent
{
	public GUInand3(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUInand3(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUInand3", name);
		setSubmodelScale(.4);
		setInputPins("A", "B", "C");
		setOutputPins("Y");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getSubmodelPin("A");
		Pin B = getSubmodelPin("B");
		Pin C = getSubmodelPin("C");
		Pin Y = getSubmodelPin("Y");

		GUINandGate nandAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate andAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandABC = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpNandAB = new WireCrossPoint(submodelModifiable, 1);

		nandAB.moveTo(10, 15);
		andAB.moveTo(35, 15);
		nandABC.moveTo(62.5, 2.5);
		cpNandAB.moveCenterTo(32.5, 25);

		new GUIWire(submodelModifiable, A, nandAB.getPin("A"));
		new GUIWire(submodelModifiable, B, nandAB.getPin("B"));
		new GUIWire(submodelModifiable, nandAB.getPin("Y"), cpNandAB, new Point[0]);
		new GUIWire(submodelModifiable, cpNandAB, andAB.getPin("A"), new Point(32.5, 20));
		new GUIWire(submodelModifiable, cpNandAB, andAB.getPin("B"), new Point(32.5, 30));
		new GUIWire(submodelModifiable, andAB.getPin("Y"), nandABC.getPin("A"), new Point(57.5, 25), new Point(57.5, 7.5));
		new GUIWire(submodelModifiable, C, nandABC.getPin("B"), new Point(60, 62.5), new Point(60, 17.5));
		new GUIWire(submodelModifiable, nandABC.getPin("Y"), Y, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUInand3.class.getCanonicalName(), (m, p, n) -> new GUInand3(m, n));
	}
}