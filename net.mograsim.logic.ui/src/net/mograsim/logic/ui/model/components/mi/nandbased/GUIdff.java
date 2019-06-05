package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdff extends SubmodelComponent
{
	private final Pin pinC;
	private final Pin pinD;
	private final Pin pinQ;
	private final Pin pin_Q;

	public GUIdff(ViewModelModifiable model)
	{
		super(model, "GUIdff");
		setSize(35, 25);
		setSubmodelScale(.2);

		Pin C = addSubmodelInterface(1, 0, 5);
		Pin D = addSubmodelInterface(1, 0, 20);
		Pin Q = addSubmodelInterface(1, 35, 5);
		Pin _Q = addSubmodelInterface(1, 35, 20);

		this.pinC = getSupermodelPin(C);
		this.pinD = getSupermodelPin(D);
		this.pinQ = getSupermodelPin(Q);
		this.pin_Q = getSupermodelPin(_Q);

		initSubmodelComponents(C, D, Q, _Q);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin C, Pin D, Pin Q, Pin _Q)
	{
		GUI_rsLatch _rsLatch1 = new GUI_rsLatch(submodelModifiable);
		GUInand3 nand3 = new GUInand3(submodelModifiable);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		GUI_rsLatch _rsLatch2 = new GUI_rsLatch(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp4 = new WireCrossPoint(submodelModifiable, 1);

		_rsLatch1.moveTo(40, 5);
		nand3.moveTo(40, 40);
		nand2.moveTo(40, 85);
		_rsLatch2.moveTo(120, 35);
		cp1.moveTo(10, 25);
		cp2.moveTo(20, 65);
		cp3.moveTo(100, 35);
		cp4.moveTo(100, 55);

		new GUIWire(submodelModifiable, C, cp1.getPin());
		new GUIWire(submodelModifiable, cp1.getPin(), _rsLatch1.getPin_R());
		new GUIWire(submodelModifiable, cp1.getPin(), nand3.getPinB(), new Point(10, 55));
		new GUIWire(submodelModifiable, D, nand2.getInputPins().get(1));
		new GUIWire(submodelModifiable, nand2.getOutputPin(), cp2.getPin(), new Point(65, 95), new Point(65, 75), new Point(20, 75));
		new GUIWire(submodelModifiable, cp2.getPin(), _rsLatch1.getPin_S(), new Point(20, 10));
		new GUIWire(submodelModifiable, cp2.getPin(), nand3.getPinC());
		new GUIWire(submodelModifiable, _rsLatch1.getPin_Q(), cp3.getPin(), new Point(100, 25));
		new GUIWire(submodelModifiable, cp3.getPin(), nand3.getPinA(), new Point(30, 35), new Point(30, 45));
		new GUIWire(submodelModifiable, cp3.getPin(), _rsLatch2.getPin_S(), new Point(100, 40));
		new GUIWire(submodelModifiable, nand3.getPinY(), cp4.getPin());
		new GUIWire(submodelModifiable, cp4.getPin(), _rsLatch2.getPin_R());
		new GUIWire(submodelModifiable, cp4.getPin(), nand2.getInputPins().get(0), new Point(100, 80), new Point(30, 80),
				new Point(30, 90));
		new GUIWire(submodelModifiable, _rsLatch2.getPinQ(), Q, new Point(165, 40), new Point(165, 25));
		new GUIWire(submodelModifiable, _rsLatch2.getPin_Q(), _Q, new Point(165, 55), new Point(165, 100));
	}

	public Pin getPinC()
	{
		return pinC;
	}

	public Pin getPinD()
	{
		return pinD;
	}

	public Pin getPinQ()
	{
		return pinQ;
	}

	public Pin getPin_Q()
	{
		return pin_Q;
	}
}