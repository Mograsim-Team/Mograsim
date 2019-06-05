package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public class GUIfulladder extends SubmodelComponent
{
	private final Pin pinA;
	private final Pin pinB;
	private final Pin pinC;
	private final Pin pinY;
	private final Pin pinZ;

	public GUIfulladder(ViewModelModifiable model)
	{
		super(model, "GUIfulladder");
		setSize(50, 40);
		setSubmodelScale(.4);

		Pin A = addSubmodelInterface(1, 0, 5);
		Pin B = addSubmodelInterface(1, 0, 20);
		Pin C = addSubmodelInterface(1, 0, 35);
		Pin Y = addSubmodelInterface(1, 50, 5);
		Pin Z = addSubmodelInterface(1, 50, 20);

		this.pinA = getSupermodelPin(A);
		this.pinB = getSupermodelPin(B);
		this.pinC = getSupermodelPin(C);
		this.pinY = getSupermodelPin(Y);
		this.pinZ = getSupermodelPin(Z);

		initSubmodelComponents(A, B, C, Y, Z);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin A, Pin B, Pin C, Pin Y, Pin Z)
	{
		GUIhalfadder halfBC = new GUIhalfadder(submodelModifiable);
		GUIhalfadder halfAY = new GUIhalfadder(submodelModifiable);
		GUINandGate nandZ = new GUINandGate(submodelModifiable, 1);

		halfAY.moveTo(55, 7.5);
		halfBC.moveTo(10, 40);
		nandZ.moveTo(100, 25);

		new GUIWire(submodelModifiable, A, halfAY.getPinA());
		new GUIWire(submodelModifiable, B, halfBC.getPinA());// , new Point(5, 50), new Point(5, 45));
		new GUIWire(submodelModifiable, C, halfBC.getPinB());// , new Point(5, 87.5), new Point(5, 60));
		new GUIWire(submodelModifiable, halfBC.getPinY(), halfAY.getPinB());// , new Point(50, 45), new Point(50, 27.5));
		new GUIWire(submodelModifiable, halfBC.getPin_Z(), nandZ.getInputPins().get(1));
		new GUIWire(submodelModifiable, halfAY.getPinY(), Y);
		new GUIWire(submodelModifiable, halfAY.getPin_Z(), nandZ.getInputPins().get(0));
		new GUIWire(submodelModifiable, nandZ.getOutputPin(), Z);
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

	public Pin getPinZ()
	{
		return pinZ;
	}
}