package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIhalfadder extends SimpleRectangularSubmodelComponent
{
	public GUIhalfadder(ViewModelModifiable model)
	{
		super(model, 1, "GUIhalfadder");
		setSubmodelScale(.4);
		setInputCount(2);
		setOutputCount(2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getInputSubmodelPins().get(0);
		Pin B = getInputSubmodelPins().get(1);
		Pin Y = getOutputSubmodelPins().get(0);
		Pin _Z = getOutputSubmodelPins().get(1);

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
		cpA.moveTo(5, 12.5);
		cpB.moveTo(5, 37.5);
		cp_Z.moveTo(35, 25);

		new GUIWire(submodelModifiable, A, cpA, new Point[0]);
		new GUIWire(submodelModifiable, cpA, nandYA.getInputPins().get(0), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpA, nand_Z.getInputPins().get(0), new Point(5, 20));
		new GUIWire(submodelModifiable, B, cpB, new Point[0]);
		new GUIWire(submodelModifiable, cpB, nandYB.getInputPins().get(1), new Point(5, 42.5));
		new GUIWire(submodelModifiable, cpB, nand_Z.getInputPins().get(1), new Point(5, 30));
		new GUIWire(submodelModifiable, nand_Z.getOutputPin(), cp_Z, new Point[0]);
		new GUIWire(submodelModifiable, cp_Z, _Z, new Point(80, 25), new Point(80, 37.5));
		new GUIWire(submodelModifiable, cp_Z, nandYA.getInputPins().get(1), new Point(35, 17.5));
		new GUIWire(submodelModifiable, cp_Z, nandYB.getInputPins().get(0), new Point(35, 32.5));
		new GUIWire(submodelModifiable, nandYA.getOutputPin(), nandY.getInputPins().get(0), new Point(62.5, 12.5), new Point(62.5, 7.5));
		new GUIWire(submodelModifiable, nandYB.getOutputPin(), nandY.getInputPins().get(1), new Point(62.5, 37.5), new Point(62.5, 17.5));
		new GUIWire(submodelModifiable, nandY.getOutputPin(), Y, new Point[0]);
	}
}