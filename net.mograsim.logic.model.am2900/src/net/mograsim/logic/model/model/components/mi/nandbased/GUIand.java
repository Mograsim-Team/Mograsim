package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIand extends SimpleRectangularSubmodelComponent
{
	public GUIand(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIand(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIand", name);
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

		GUINandGate nand = new GUINandGate(submodelModifiable, 1);
		GUINandGate not = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);

		nand.moveTo(20, 15);
		not.moveTo(50, 15);
		cp1.moveCenterTo(45, 25);

		new GUIWire(submodelModifiable, A, nand.getPin("A"));
		new GUIWire(submodelModifiable, B, nand.getPin("B"));
		new GUIWire(submodelModifiable, nand.getPin("Y"), cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, not.getPin("A"), new Point(45, 20));
		new GUIWire(submodelModifiable, cp1, not.getPin("B"), new Point(45, 30));
		new GUIWire(submodelModifiable, not.getPin("Y"), Y);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIand.class.getCanonicalName(), (m, p, n) -> new GUIand(m, n));
	}
}