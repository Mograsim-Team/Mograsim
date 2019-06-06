package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIand41 extends SimpleRectangularSubmodelComponent
{
	public GUIand41(ViewModelModifiable model)
	{
		super(model, 1, "GUIand41");
		setSubmodelScale(.4);
		setInputCount(5);
		setOutputCount(4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A1 = getInputSubmodelPins().get(0);
		Pin A2 = getInputSubmodelPins().get(1);
		Pin A3 = getInputSubmodelPins().get(2);
		Pin A4 = getInputSubmodelPins().get(3);
		Pin B = getInputSubmodelPins().get(4);
		Pin Y1 = getOutputSubmodelPins().get(0);
		Pin Y2 = getOutputSubmodelPins().get(1);
		Pin Y3 = getOutputSubmodelPins().get(2);
		Pin Y4 = getOutputSubmodelPins().get(3);

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
		cpB2.moveCenterTo(25, 47.5);
		cpB3.moveCenterTo(25, 72.5);
		cpB4.moveCenterTo(25, 97.5);

		new GUIWire(submodelModifiable, A1, and1.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, A2, and2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, A3, and3.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, A4, and4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, B, cpB4, new Point(25, 112.5));
		new GUIWire(submodelModifiable, cpB4, and4.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpB4, cpB3, new Point[0]);
		new GUIWire(submodelModifiable, cpB3, and3.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpB3, cpB2, new Point[0]);
		new GUIWire(submodelModifiable, cpB2, and2.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpB2, and1.getInputPins().get(1), new Point(25, 22.5));
		new GUIWire(submodelModifiable, and1.getOutputPins().get(0), Y1, new Point[0]);
		new GUIWire(submodelModifiable, and2.getOutputPins().get(0), Y2, new Point[0]);
		new GUIWire(submodelModifiable, and3.getOutputPins().get(0), Y3, new Point[0]);
		new GUIWire(submodelModifiable, and4.getOutputPins().get(0), Y4, new Point[0]);
	}
}