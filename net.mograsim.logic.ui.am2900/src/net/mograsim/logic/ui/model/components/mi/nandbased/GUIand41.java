package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIand41 extends SimpleRectangularSubmodelComponent
{
	public GUIand41(ViewModelModifiable model)
	{
		super(model, 1, "GUIand41");
		setSubmodelScale(.4);
		setInputPins("A1", "A2", "A3", "A4", "B");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin B = getSubmodelPin("B");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

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

		new GUIWire(submodelModifiable, A1, and1.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, A2, and2.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, A3, and3.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, A4, and4.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, B, cpB4, new Point(25, 112.5));
		new GUIWire(submodelModifiable, cpB4, and4.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpB4, cpB3, new Point[0]);
		new GUIWire(submodelModifiable, cpB3, and3.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpB3, cpB2, new Point[0]);
		new GUIWire(submodelModifiable, cpB2, and2.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpB2, and1.getPin("B"), new Point(25, 22.5));
		new GUIWire(submodelModifiable, and1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, and2.getPin("Y"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, and3.getPin("Y"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, and4.getPin("Y"), Y4, new Point[0]);
	}
}