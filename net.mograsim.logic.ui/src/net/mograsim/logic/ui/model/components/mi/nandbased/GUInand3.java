package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUInand3 extends SimpleRectangularSubmodelComponent
{
	public GUInand3(ViewModelModifiable model)
	{
		super(model, 1, "GUInand3");
		setSubmodelScale(.4);
		setInputCount(3);
		setOutputCount(1);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getInputSubmodelPins().get(0);
		Pin B = getInputSubmodelPins().get(1);
		Pin C = getInputSubmodelPins().get(2);
		Pin Y = getOutputSubmodelPins().get(0);

		GUINandGate nandAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate andAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandABC = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpNandAB = new WireCrossPoint(submodelModifiable, 1);

		nandAB.moveTo(10, 15);
		andAB.moveTo(35, 15);
		nandABC.moveTo(62.5, 2.5);
		cpNandAB.moveTo(32.5, 25);

		new GUIWire(submodelModifiable, A, nandAB.getInputPins().get(0));
		new GUIWire(submodelModifiable, B, nandAB.getInputPins().get(1));
		new GUIWire(submodelModifiable, nandAB.getOutputPin(), cpNandAB, new Point[0]);
		new GUIWire(submodelModifiable, cpNandAB, andAB.getInputPins().get(0), new Point(32.5, 20));
		new GUIWire(submodelModifiable, cpNandAB, andAB.getInputPins().get(1), new Point(32.5, 30));
		new GUIWire(submodelModifiable, andAB.getOutputPin(), nandABC.getInputPins().get(0), new Point(57.5, 25), new Point(57.5, 7.5));
		new GUIWire(submodelModifiable, C, nandABC.getInputPins().get(1), new Point(60, 62.5), new Point(60, 17.5));
		new GUIWire(submodelModifiable, nandABC.getOutputPin(), Y, new Point[0]);
	}
}