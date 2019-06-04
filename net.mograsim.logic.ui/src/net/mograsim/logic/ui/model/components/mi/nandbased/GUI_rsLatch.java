package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUI_rsLatch extends SubmodelComponent
{
	private final Pin pin_S;
	private final Pin pin_R;
	private final Pin pinQ;
	private final Pin pin_Q;

	public GUI_rsLatch(ViewModelModifiable model)
	{
		super(model, "_rsLatch");
		setSize(35, 25);
		setSubmodelScale(.2);

		Pin _S = addSubmodelInterface(1, 0, 5);
		Pin _R = addSubmodelInterface(1, 0, 20);
		Pin Q = addSubmodelInterface(1, 35, 5);
		Pin _Q = addSubmodelInterface(1, 35, 20);

		this.pin_S = getSupermodelPin(_S);
		this.pin_R = getSupermodelPin(_R);
		this.pinQ = getSupermodelPin(Q);
		this.pin_Q = getSupermodelPin(_Q);

		initSubmodelComponents(_S, _R, Q, _Q);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin _S, Pin _R, Pin Q, Pin _Q)
	{

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		nand1.moveTo(80, 20);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		nand2.moveTo(80, 85);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		cp1.moveTo(120, 30);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		cp2.moveTo(120, 95);

		new GUIWire(submodelModifiable, _S, nand1.getInputPins().get(0));
		new GUIWire(submodelModifiable, _R, nand2.getInputPins().get(1));
		new GUIWire(submodelModifiable, nand1.getOutputPin(), cp1.getPin());
		new GUIWire(submodelModifiable, nand2.getOutputPin(), cp2.getPin());
		new GUIWire(submodelModifiable, cp1.getPin(), nand2.getInputPins().get(0), new Point(120, 50), new Point(60, 75),
				new Point(60, 90));
		new GUIWire(submodelModifiable, cp2.getPin(), nand1.getInputPins().get(1), new Point(120, 75), new Point(60, 50),
				new Point(60, 35));
		new GUIWire(submodelModifiable, cp1.getPin(), Q, new Point(150, 30), new Point(150, 25));
		new GUIWire(submodelModifiable, cp2.getPin(), _Q, new Point(150, 95), new Point(150, 100));
	}

	public Pin getPin_S()
	{
		return pin_S;
	}

	public Pin getPin_R()
	{
		return pin_R;
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