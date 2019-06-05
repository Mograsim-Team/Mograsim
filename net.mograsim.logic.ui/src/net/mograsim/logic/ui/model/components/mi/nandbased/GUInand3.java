package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUInand3 extends SubmodelComponent
{
	private final Pin pinA;
	private final Pin pinB;
	private final Pin pinC;
	private final Pin pinY;

	public GUInand3(ViewModelModifiable model)
	{
		super(model, "GUInand3");
		setSize(40, 30);
		setSubmodelScale(.4);

		Pin A = addSubmodelInterface(1, 0, 5);
		Pin B = addSubmodelInterface(1, 0, 15);
		Pin C = addSubmodelInterface(1, 0, 25);
		Pin Y = addSubmodelInterface(1, 40, 15);

		this.pinA = getSupermodelPin(A);
		this.pinB = getSupermodelPin(B);
		this.pinC = getSupermodelPin(C);
		this.pinY = getSupermodelPin(Y);

		initSubmodelComponents(A, B, C, Y);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin A, Pin B, Pin C, Pin Y)
	{
		GUINandGate nandAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate andAB = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandABC = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpNandAB = new WireCrossPoint(submodelModifiable, 1);

		nandAB.moveTo(10, 15);
		andAB.moveTo(40, 15);
		nandABC.moveTo(70, 27.5);
		cpNandAB.moveTo(35, 25);

		new GUIWire(submodelModifiable, A, nandAB.getInputPins().get(0), new Point(5, 12.5), new Point(5, 20));
		new GUIWire(submodelModifiable, B, nandAB.getInputPins().get(1), new Point(5, 37.5), new Point(5, 30));
		new GUIWire(submodelModifiable, nandAB.getOutputPin(), cpNandAB);
		new GUIWire(submodelModifiable, cpNandAB, andAB.getInputPins().get(0), new Point(35, 20));
		new GUIWire(submodelModifiable, cpNandAB, andAB.getInputPins().get(1), new Point(35, 30));
		new GUIWire(submodelModifiable, andAB.getOutputPin(), nandABC.getInputPins().get(0), new Point(65, 25), new Point(65, 32.5));
		new GUIWire(submodelModifiable, C, nandABC.getInputPins().get(1), new Point(65, 62.5), new Point(65, 42.5));
		new GUIWire(submodelModifiable, nandABC.getOutputPin(), Y);
	}

	public Pin getPinA()
	{
		return pinA;
	}

	public Pin getPinB()
	{
		return pinB;
	}

	public Pin getPinC()
	{
		return pinC;
	}

	public Pin getPinY()
	{
		return pinY;
	}
}