package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIand41 extends SubmodelComponent
{
	private final Pin pinA1;
	private final Pin pinA2;
	private final Pin pinA3;
	private final Pin pinA4;
	private final Pin pinB;
	private final Pin pinY1;
	private final Pin pinY2;
	private final Pin pinY3;
	private final Pin pinY4;

	public GUIand41(ViewModelModifiable model)
	{
		super(model, "GUIand41");
		setSize(35, 55);
		setSubmodelScale(.4);

		Pin A1 = addSubmodelInterface(1, 0, 5);
		Pin A2 = addSubmodelInterface(1, 0, 15);
		Pin A3 = addSubmodelInterface(1, 0, 25);
		Pin A4 = addSubmodelInterface(1, 0, 35);
		Pin B = addSubmodelInterface(1, 0, 50);
		Pin Y1 = addSubmodelInterface(1, 35, 5);
		Pin Y2 = addSubmodelInterface(1, 35, 15);
		Pin Y3 = addSubmodelInterface(1, 35, 25);
		Pin Y4 = addSubmodelInterface(1, 35, 35);

		this.pinA1 = getSupermodelPin(A1);
		this.pinA2 = getSupermodelPin(A2);
		this.pinA3 = getSupermodelPin(A3);
		this.pinA4 = getSupermodelPin(A4);
		this.pinB = getSupermodelPin(B);
		this.pinY1 = getSupermodelPin(Y1);
		this.pinY2 = getSupermodelPin(Y2);
		this.pinY3 = getSupermodelPin(Y3);
		this.pinY4 = getSupermodelPin(Y4);

		initSubmodelComponents(A1, A2, A3, A4, B, Y1, Y2, Y3, Y4);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin A1, Pin A2, Pin A3, Pin A4, Pin B, Pin Y1, Pin Y2, Pin Y3, Pin Y4)
	{
		GUIand and1 = new GUIand(submodelModifiable);
		GUIand and2 = new GUIand(submodelModifiable);
		GUIand and3 = new GUIand(submodelModifiable);
		GUIand and4 = new GUIand(submodelModifiable);

		WireCrossPoint cpB2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB4 = new WireCrossPoint(submodelModifiable, 1);

		and1.moveTo(30, 7.5);
		and2.moveTo(30, 32.5);
		and3.moveTo(30, 57.5);
		and4.moveTo(30, 82.5);
		cpB2.moveTo(25, 52.5);
		cpB3.moveTo(25, 77.5);
		cpB4.moveTo(25, 102.5);

		new GUIWire(submodelModifiable, A1, and1.getPinA());
		new GUIWire(submodelModifiable, A2, and2.getPinA());
		new GUIWire(submodelModifiable, A3, and3.getPinA());
		new GUIWire(submodelModifiable, A4, and4.getPinA());
		new GUIWire(submodelModifiable, B, cpB4, new Point(25, 125));
		new GUIWire(submodelModifiable, cpB4, and4.getPinB());
		new GUIWire(submodelModifiable, cpB4, cpB3);
		new GUIWire(submodelModifiable, cpB3, and3.getPinB());
		new GUIWire(submodelModifiable, cpB3, cpB2);
		new GUIWire(submodelModifiable, cpB2, and2.getPinB());
		new GUIWire(submodelModifiable, cpB2, and1.getPinB(), new Point(25, 27.5));
		new GUIWire(submodelModifiable, and1.getPinY(), Y1, new Point(75, 20), new Point(75, 12.5));
		new GUIWire(submodelModifiable, and2.getPinY(), Y2, new Point(75, 45), new Point(75, 37.5));
		new GUIWire(submodelModifiable, and3.getPinY(), Y3, new Point(75, 70), new Point(75, 62.5));
		new GUIWire(submodelModifiable, and4.getPinY(), Y4, new Point(75, 95), new Point(75, 87.5));
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

	public Pin getPinB()
	{
		return pinB;
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