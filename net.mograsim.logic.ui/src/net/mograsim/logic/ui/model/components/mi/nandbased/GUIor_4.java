package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIor_4 extends SubmodelComponent
{
	private final Pin pinA1;
	private final Pin pinA2;
	private final Pin pinA3;
	private final Pin pinA4;
	private final Pin pinB1;
	private final Pin pinB2;
	private final Pin pinB3;
	private final Pin pinB4;
	private final Pin pinY1;
	private final Pin pinY2;
	private final Pin pinY3;
	private final Pin pinY4;

	public GUIor_4(ViewModelModifiable model)
	{
		super(model, "GUIor_4");
		setSize(40, 80);
		setSubmodelScale(.4);

		Pin A1 = addSubmodelInterface(1, 0, 5);
		Pin A2 = addSubmodelInterface(1, 0, 15);
		Pin A3 = addSubmodelInterface(1, 0, 25);
		Pin A4 = addSubmodelInterface(1, 0, 35);
		Pin B1 = addSubmodelInterface(1, 0, 45);
		Pin B2 = addSubmodelInterface(1, 0, 55);
		Pin B3 = addSubmodelInterface(1, 0, 65);
		Pin B4 = addSubmodelInterface(1, 0, 75);
		Pin Y1 = addSubmodelInterface(1, 40, 5);
		Pin Y2 = addSubmodelInterface(1, 40, 15);
		Pin Y3 = addSubmodelInterface(1, 40, 25);
		Pin Y4 = addSubmodelInterface(1, 40, 35);

		this.pinA1 = getSupermodelPin(A1);
		this.pinA2 = getSupermodelPin(A2);
		this.pinA3 = getSupermodelPin(A3);
		this.pinA4 = getSupermodelPin(A4);
		this.pinB1 = getSupermodelPin(B1);
		this.pinB2 = getSupermodelPin(B2);
		this.pinB3 = getSupermodelPin(B3);
		this.pinB4 = getSupermodelPin(B4);
		this.pinY1 = getSupermodelPin(Y1);
		this.pinY2 = getSupermodelPin(Y2);
		this.pinY3 = getSupermodelPin(Y3);
		this.pinY4 = getSupermodelPin(Y4);

		initSubmodelComponents(A1, A2, A3, A4, B1, B2, B3, B4, Y1, Y2, Y3, Y4);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin A1, Pin A2, Pin A3, Pin A4, Pin B1, Pin B2, Pin B3, Pin B4, Pin Y1, Pin Y2, Pin Y3, Pin Y4)
	{
		GUINandGate notA1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY4 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB4 = new WireCrossPoint(submodelModifiable, 1);

		notA1.moveTo(15, 2.5);
		notA2.moveTo(15, 27.5);
		notA3.moveTo(15, 52.5);
		notA4.moveTo(15, 77.5);
		notB1.moveTo(15, 102.5);
		notB2.moveTo(15, 127.5);
		notB3.moveTo(15, 152.5);
		notB4.moveTo(15, 177.5);
		nandY1.moveTo(70, 2.5);
		nandY2.moveTo(70, 27.5);
		nandY3.moveTo(70, 52.5);
		nandY4.moveTo(70, 77.5);
		cpA1.moveTo(7.5, 12.5);
		cpA2.moveTo(7.5, 37.5);
		cpA3.moveTo(7.5, 62.5);
		cpA4.moveTo(7.5, 87.5);
		cpB1.moveTo(7.5, 112.5);
		cpB2.moveTo(7.5, 137.5);
		cpB3.moveTo(7.5, 162.5);
		cpB4.moveTo(7.5, 187.5);

		new GUIWire(submodelModifiable, A1, cpA1.getPin());
		new GUIWire(submodelModifiable, A2, cpA2.getPin());
		new GUIWire(submodelModifiable, A3, cpA3.getPin());
		new GUIWire(submodelModifiable, A4, cpA4.getPin());
		new GUIWire(submodelModifiable, B1, cpB1.getPin());
		new GUIWire(submodelModifiable, B2, cpB2.getPin());
		new GUIWire(submodelModifiable, B3, cpB3.getPin());
		new GUIWire(submodelModifiable, B4, cpB4.getPin());
		new GUIWire(submodelModifiable, cpA1.getPin(), notA1.getInputPins().get(0), new Point(7.5, 7.5));
		new GUIWire(submodelModifiable, cpA1.getPin(), notA1.getInputPins().get(1), new Point(7.5, 17.5));
		new GUIWire(submodelModifiable, cpA2.getPin(), notA2.getInputPins().get(0), new Point(7.5, 32.5));
		new GUIWire(submodelModifiable, cpA2.getPin(), notA2.getInputPins().get(1), new Point(7.5, 42.5));
		new GUIWire(submodelModifiable, cpA3.getPin(), notA3.getInputPins().get(0), new Point(7.5, 57.5));
		new GUIWire(submodelModifiable, cpA3.getPin(), notA3.getInputPins().get(1), new Point(7.5, 67.5));
		new GUIWire(submodelModifiable, cpA4.getPin(), notA4.getInputPins().get(0), new Point(7.5, 82.5));
		new GUIWire(submodelModifiable, cpA4.getPin(), notA4.getInputPins().get(1), new Point(7.5, 92.5));
		new GUIWire(submodelModifiable, cpB1.getPin(), notB1.getInputPins().get(0), new Point(7.5, 107.5));
		new GUIWire(submodelModifiable, cpB1.getPin(), notB1.getInputPins().get(1), new Point(7.5, 117.5));
		new GUIWire(submodelModifiable, cpB2.getPin(), notB2.getInputPins().get(0), new Point(7.5, 132.5));
		new GUIWire(submodelModifiable, cpB2.getPin(), notB2.getInputPins().get(1), new Point(7.5, 142.5));
		new GUIWire(submodelModifiable, cpB3.getPin(), notB3.getInputPins().get(0), new Point(7.5, 157.5));
		new GUIWire(submodelModifiable, cpB3.getPin(), notB3.getInputPins().get(1), new Point(7.5, 167.5));
		new GUIWire(submodelModifiable, cpB4.getPin(), notB4.getInputPins().get(0), new Point(7.5, 182.5));
		new GUIWire(submodelModifiable, cpB4.getPin(), notB4.getInputPins().get(1), new Point(7.5, 192.5));
		new GUIWire(submodelModifiable, notA1.getOutputPin(), nandY1.getInputPins().get(0), new Point(40, 12.5), new Point(40, 7.5));
		new GUIWire(submodelModifiable, notB1.getOutputPin(), nandY1.getInputPins().get(1), new Point(50, 112.5), new Point(50, 17.5));
		new GUIWire(submodelModifiable, notA2.getOutputPin(), nandY2.getInputPins().get(0), new Point(40, 37.5), new Point(40, 32.5));
		new GUIWire(submodelModifiable, notB2.getOutputPin(), nandY2.getInputPins().get(1), new Point(55, 137.5), new Point(55, 42.5));
		new GUIWire(submodelModifiable, notA3.getOutputPin(), nandY3.getInputPins().get(0), new Point(40, 62.5), new Point(40, 57.5));
		new GUIWire(submodelModifiable, notB3.getOutputPin(), nandY3.getInputPins().get(1), new Point(60, 162.5), new Point(60, 67.5));
		new GUIWire(submodelModifiable, notA4.getOutputPin(), nandY4.getInputPins().get(0), new Point(40, 87.5), new Point(40, 82.5));
		new GUIWire(submodelModifiable, notB4.getOutputPin(), nandY4.getInputPins().get(1), new Point(65, 187.5), new Point(65, 92.5));
		new GUIWire(submodelModifiable, nandY1.getOutputPin(), Y1);
		new GUIWire(submodelModifiable, nandY2.getOutputPin(), Y2);
		new GUIWire(submodelModifiable, nandY3.getOutputPin(), Y3);
		new GUIWire(submodelModifiable, nandY4.getOutputPin(), Y4);
	}

	public Pin getPinA1()
	{
		return pinA1;
	}

	public Pin getPinA2()
	{
		return pinA2;
	}

	public Pin getPinA3()
	{
		return pinA3;
	}

	public Pin getPinA4()
	{
		return pinA4;
	}

	public Pin getPinB1()
	{
		return pinB1;
	}

	public Pin getPinB2()
	{
		return pinB2;
	}

	public Pin getPinB3()
	{
		return pinB3;
	}

	public Pin getPinB4()
	{
		return pinB4;
	}

	public Pin getPinY1()
	{
		return pinY1;
	}

	public Pin getPinY2()
	{
		return pinY2;
	}

	public Pin getPinY3()
	{
		return pinY3;
	}

	public Pin getPinY4()
	{
		return pinY4;
	}
}