package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdlatch extends SubmodelComponent
{
	private final Pin pinD;
	private final Pin pinE;
	private final Pin pinQ;
	private final Pin pin_Q;

	public GUIdlatch(ViewModelModifiable model)
	{
		super(model, "GUIdlatch");
		setSize(35, 25);
		setSubmodelScale(.4);

		Pin D = addSubmodelInterface(1, 0, 5);
		Pin E = addSubmodelInterface(1, 0, 20);
		Pin Q = addSubmodelInterface(1, 35, 5);
		Pin _Q = addSubmodelInterface(1, 35, 20);

		this.pinD = getSupermodelPin(D);
		this.pinE = getSupermodelPin(E);
		this.pinQ = getSupermodelPin(Q);
		this.pin_Q = getSupermodelPin(_Q);

		initSubmodelComponents(D, E, Q, _Q);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin D, Pin E, Pin Q, Pin _Q)
	{
		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		GUI_rsLatch _rsLatch = new GUI_rsLatch(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 7.5);
		nand2.moveTo(15, 35);
		_rsLatch.moveTo(45, 7.5);
		cp1.moveTo(5, 50);
		cp2.moveTo(35, 17.5);

		new GUIWire(submodelModifiable, D, nand1.getInputPins().get(0));
		new GUIWire(submodelModifiable, E, cp1.getPin());
		new GUIWire(submodelModifiable, cp1.getPin(), nand1.getInputPins().get(1), new Point(5, 22.5));
		new GUIWire(submodelModifiable, cp1.getPin(), nand2.getInputPins().get(1));
		new GUIWire(submodelModifiable, nand1.getOutputPin(), cp2.getPin());
		new GUIWire(submodelModifiable, cp2.getPin(), nand2.getInputPins().get(0), new Point(35, 30), new Point(10, 30), new Point(10, 40));
		new GUIWire(submodelModifiable, cp2.getPin(), _rsLatch.getPin_S(), new Point(35, 12.5));
		new GUIWire(submodelModifiable, nand2.getOutputPin(), _rsLatch.getPin_R(), new Point(40, 45), new Point(40, 27.5));
		new GUIWire(submodelModifiable, _rsLatch.getPinQ(), Q);
		new GUIWire(submodelModifiable, _rsLatch.getPin_Q(), _Q, new Point(82.5, 27.5), new Point(82.5, 50));
	}

	public Pin getPinD()
	{
		return pinD;
	}

	public Pin getPinE()
	{
		return pinE;
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