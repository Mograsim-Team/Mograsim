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
		setInputPins("_S", "_R");
		setOutputPins("Q", "_Q");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin _S = getSubmodelPin("_S");
		Pin _R = getSubmodelPin("_R");
		Pin Q = getSubmodelPin("Q");
		Pin _Q = getSubmodelPin("_Q");

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 7.5);
		nand2.moveTo(40, 12.5);
		cp1.moveCenterTo(35, 17.5);
		cp2.moveCenterTo(65, 37.5);

		new GUIWire(submodelModifiable, _S, nand1.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, _R, nand2.getPin("B"), new Point(35, 37.5), new Point(35, 27.5));
		new GUIWire(submodelModifiable, nand1.getPin("Y"), cp1, new Point[0]);
		new GUIWire(submodelModifiable, nand2.getPin("Y"), cp2, new Point(65, 22.5));
		new GUIWire(submodelModifiable, cp1, nand2.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cp2, nand1.getPin("B"), new Point(65, 42.5), new Point(5, 42.5), new Point(5, 22.5));
		new GUIWire(submodelModifiable, cp1, Q, new Point(35, 17.5), new Point(35, 7.5), new Point(65, 7.5), new Point(65, 12.5));
		new GUIWire(submodelModifiable, cp2, _Q, new Point[0]);
	}
}