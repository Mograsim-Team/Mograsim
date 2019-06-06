package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUI_rsLatch extends SimpleRectangularSubmodelComponent
{
	public GUI_rsLatch(ViewModelModifiable model)
	{
		super(model, 1, "_rsLatch");
		setSubmodelScale(.4);
		setInputCount(2);
		setOutputCount(2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin _S = getInputSubmodelPins().get(0);
		Pin _R = getInputSubmodelPins().get(1);
		Pin Q = getOutputSubmodelPins().get(0);
		Pin _Q = getOutputSubmodelPins().get(1);

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 7.5);
		nand2.moveTo(40, 12.5);
		cp1.moveCenterTo(35, 17.5);
		cp2.moveCenterTo(65, 37.5);

		new GUIWire(submodelModifiable, _S, nand1.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, _R, nand2.getInputPins().get(1), new Point(35, 37.5), new Point(35, 27.5));
		new GUIWire(submodelModifiable, nand1.getOutputPin(), cp1, new Point[0]);
		new GUIWire(submodelModifiable, nand2.getOutputPin(), cp2, new Point(65, 22.5));
		new GUIWire(submodelModifiable, cp1, nand2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cp2, nand1.getInputPins().get(1), new Point(65, 42.5), new Point(5, 42.5), new Point(5, 22.5));
		new GUIWire(submodelModifiable, cp1, Q, new Point(35, 17.5), new Point(35, 7.5), new Point(65, 7.5), new Point(65, 12.5));
		new GUIWire(submodelModifiable, cp2, _Q, new Point[0]);
	}
}