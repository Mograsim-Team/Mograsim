package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIxor extends SimpleRectangularSubmodelComponent
{
	public GUIxor(ViewModelModifiable model)
	{
		super(model, 1, "GUIxor");
		setSubmodelScale(.4);
		setInputPins("A", "B");
		setOutputPins("Y");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getInputSubmodelPins().get(0);
		Pin B = getInputSubmodelPins().get(1);
		Pin Y = getOutputSubmodelPins().get(0);

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
		new GUIWire(submodelModifiable, cpA, nandAB.getInputPins().get(0), new Point(5, 20));
		new GUIWire(submodelModifiable, cpB, nandAB.getInputPins().get(1), new Point(5, 30));
		new GUIWire(submodelModifiable, nandAB.getOutputPin(), cpAB);
		new GUIWire(submodelModifiable, cpAB, nandYA.getInputPins().get(1), new Point(30, 17.5));
		new GUIWire(submodelModifiable, cpAB, nandYB.getInputPins().get(0), new Point(30, 32.5));
		new GUIWire(submodelModifiable, cpA, nandYA.getInputPins().get(0), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpB, nandYB.getInputPins().get(1), new Point(5, 42.5));
		new GUIWire(submodelModifiable, nandYA.getOutputPin(), nandY.getInputPins().get(0));
		new GUIWire(submodelModifiable, nandYB.getOutputPin(), nandY.getInputPins().get(1));
		new GUIWire(submodelModifiable, nandY.getOutputPin(), Y);
	}
}