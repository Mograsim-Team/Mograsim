package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public class GUIandor414 extends SubmodelComponent
{
	private final Pin pinC1;
	private final Pin pinC2;
	private final Pin pinC3;
	private final Pin pinC4;
	private final Pin pinA1;
	private final Pin pinA2;
	private final Pin pinA3;
	private final Pin pinA4;
	private final Pin pinB;
	private final Pin pinY1;
	private final Pin pinY2;
	private final Pin pinY3;
	private final Pin pinY4;

	public GUIandor414(ViewModelModifiable model)
	{
		super(model, "GUIandor414");
		setSize(50, 90);
		setSubmodelScale(.4);

		Pin C1 = addSubmodelInterface(1, 0, 5);
		Pin C2 = addSubmodelInterface(1, 0, 15);
		Pin C3 = addSubmodelInterface(1, 0, 25);
		Pin C4 = addSubmodelInterface(1, 0, 35);
		Pin A1 = addSubmodelInterface(1, 0, 45);
		Pin A2 = addSubmodelInterface(1, 0, 55);
		Pin A3 = addSubmodelInterface(1, 0, 65);
		Pin A4 = addSubmodelInterface(1, 0, 75);
		Pin B = addSubmodelInterface(1, 0, 85);
		Pin Y1 = addSubmodelInterface(1, 50, 5);
		Pin Y2 = addSubmodelInterface(1, 50, 15);
		Pin Y3 = addSubmodelInterface(1, 50, 25);
		Pin Y4 = addSubmodelInterface(1, 50, 35);

		this.pinC1 = getSupermodelPin(C1);
		this.pinC2 = getSupermodelPin(C2);
		this.pinC3 = getSupermodelPin(C3);
		this.pinC4 = getSupermodelPin(C4);
		this.pinA1 = getSupermodelPin(A1);
		this.pinA2 = getSupermodelPin(A2);
		this.pinA3 = getSupermodelPin(A3);
		this.pinA4 = getSupermodelPin(A4);
		this.pinB = getSupermodelPin(B);
		this.pinY1 = getSupermodelPin(Y1);
		this.pinY2 = getSupermodelPin(Y2);
		this.pinY3 = getSupermodelPin(Y3);
		this.pinY4 = getSupermodelPin(Y4);

		initSubmodelComponents(C1, C2, C3, C4, A1, A2, A3, A4, B, Y1, Y2, Y3, Y4);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin C1, Pin C2, Pin C3, Pin C4, Pin A1, Pin A2, Pin A3, Pin A4, Pin B, Pin Y1, Pin Y2, Pin Y3,
			Pin Y4)
	{
		GUIand41 and = new GUIand41(submodelModifiable);
		GUIor_4 or = new GUIor_4(submodelModifiable);

		and.moveTo(15, 137.5);
		or.moveTo(55, 97.5);

		new GUIWire(submodelModifiable, A1, and.getPinA1(), new Point(10, 112.5), new Point(10, 142.5));
		new GUIWire(submodelModifiable, A2, and.getPinA2(), new Point(5, 137.5), new Point(5, 152.5));
		new GUIWire(submodelModifiable, A3, and.getPinA3());
		new GUIWire(submodelModifiable, A4, and.getPinA4(), new Point(5, 187.5), new Point(5, 172.5));
		new GUIWire(submodelModifiable, B, and.getPinB(), new Point(10, 212.5), new Point(10, 187.5));
		new GUIWire(submodelModifiable, C1, or.getPinA1(), new Point(50, 12.5), new Point(50, 102.5));
		new GUIWire(submodelModifiable, C2, or.getPinA2(), new Point(45, 37.5), new Point(45, 112.5));
		new GUIWire(submodelModifiable, C3, or.getPinA3(), new Point(40, 62.5), new Point(40, 122.5));
		new GUIWire(submodelModifiable, C4, or.getPinA4(), new Point(35, 87.5), new Point(35, 132.5));
		new GUIWire(submodelModifiable, and.getPinY1(), or.getPinB1());
		new GUIWire(submodelModifiable, and.getPinY2(), or.getPinB2());
		new GUIWire(submodelModifiable, and.getPinY3(), or.getPinB3());
		new GUIWire(submodelModifiable, and.getPinY4(), or.getPinB4());
		new GUIWire(submodelModifiable, or.getPinY1(), Y1, new Point(100, 102.5), new Point(100, 12.5));
		new GUIWire(submodelModifiable, or.getPinY2(), Y2, new Point(105, 112.5), new Point(105, 37.5));
		new GUIWire(submodelModifiable, or.getPinY3(), Y3, new Point(110, 122.5), new Point(110, 62.5));
		new GUIWire(submodelModifiable, or.getPinY4(), Y4, new Point(115, 132.5), new Point(115, 87.5));
	}

	public Pin getPinC1()
	{
		return pinC1;
	}

	public Pin getPinC2()
	{
		return pinC2;
	}

	public Pin getPinC3()
	{
		return pinC3;
	}

	public Pin getPinC4()
	{
		return pinC4;
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