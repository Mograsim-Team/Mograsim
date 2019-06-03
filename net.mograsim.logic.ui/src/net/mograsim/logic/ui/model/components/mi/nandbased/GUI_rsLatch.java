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
	public GUI_rsLatch(ViewModelModifiable model)
	{
		super(model, "_rsLatch");
		setSize(35, 25);
		setSubmodelScale(.2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents()
	{
		Pin _S = addSubmodelInterface(1, 0, 5);
		Pin _R = addSubmodelInterface(1, 0, 20);
		Pin Q = addSubmodelInterface(1, 35, 5);
		Pin _Q = addSubmodelInterface(1, 35, 20);

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
}