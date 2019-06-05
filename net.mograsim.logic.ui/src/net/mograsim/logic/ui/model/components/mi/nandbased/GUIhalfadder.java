package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIhalfadder extends SubmodelComponent
{
	private final Pin pinA;
	private final Pin pinB;
	private final Pin pinY;
	private final Pin pin_Z;

	public GUIhalfadder(ViewModelModifiable model)
	{
		super(model, "GUIhalfadder");
		setSize(35, 25);
		setSubmodelScale(.4);

		Pin A = addSubmodelInterface(1, 0, 5);
		Pin B = addSubmodelInterface(1, 0, 20);
		Pin Y = addSubmodelInterface(1, 35, 5);
		Pin _Z = addSubmodelInterface(1, 35, 20);

		this.pinA = getSupermodelPin(A);
		this.pinB = getSupermodelPin(B);
		this.pinY = getSupermodelPin(Y);
		this.pin_Z = getSupermodelPin(_Z);

		initSubmodelComponents(A, B, Y, _Z);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin A, Pin B, Pin Y, Pin _Z)
	{
		GUINandGate nand_Z = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYA = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp_Z = new WireCrossPoint(submodelModifiable, 1);

		nand_Z.moveTo(10, 21.25);
		nandYA.moveTo(40, 2.5);
		nandYB.moveTo(40, 40);
		nandY.moveTo(65, 2.5);
		cpA.moveTo(5, 12.5);
		cpB.moveTo(5, 50);
		cp_Z.moveTo(35, 31.25);

		new GUIWire(submodelModifiable, A, cpA);
		new GUIWire(submodelModifiable, cpA, nandYA.getInputPins().get(0), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpA, nand_Z.getInputPins().get(0), new Point(5, 26.25));
		new GUIWire(submodelModifiable, B, cpB);
		new GUIWire(submodelModifiable, cpB, nandYB.getInputPins().get(1), new Point(5, 55));
		new GUIWire(submodelModifiable, cpB, nand_Z.getInputPins().get(1), new Point(5, 36.25));
		new GUIWire(submodelModifiable, nand_Z.getOutputPin(), cp_Z);
		new GUIWire(submodelModifiable, cp_Z, _Z, new Point(80, 31.25), new Point(80, 50));
		new GUIWire(submodelModifiable, cp_Z, nandYA.getInputPins().get(1), new Point(35, 17.5));
		new GUIWire(submodelModifiable, cp_Z, nandYB.getInputPins().get(0), new Point(35, 45));
		new GUIWire(submodelModifiable, nandYA.getOutputPin(), nandY.getInputPins().get(0), new Point(62.5, 12.5), new Point(62.5, 7.5));
		new GUIWire(submodelModifiable, nandYB.getOutputPin(), nandY.getInputPins().get(1), new Point(62.5, 50), new Point(62.5, 17.5));
		new GUIWire(submodelModifiable, nandY.getOutputPin(), Y);
	}

	public Pin getPinA()
	{
		return pinA;
	}

	public Pin getPinB()
	{
		return pinB;
	}

	public Pin getPinY()
	{
		return pinY;
	}

	public Pin getPin_Z()
	{
		return pin_Z;
	}
}