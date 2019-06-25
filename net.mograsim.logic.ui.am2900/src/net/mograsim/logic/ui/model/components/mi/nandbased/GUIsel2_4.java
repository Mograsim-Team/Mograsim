package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIsel2_4 extends SimpleRectangularSubmodelComponent
{
	public GUIsel2_4(ViewModelModifiable model)
	{
		super(model, 1, "GUIsel2_4");
		setSubmodelScale(.4);
		setInputPins("SA", "SB", "A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin SA = getSubmodelPin("SA");
		Pin SB = getSubmodelPin("SB");
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin B1 = getSubmodelPin("B1");
		Pin B2 = getSubmodelPin("B2");
		Pin B3 = getSubmodelPin("B3");
		Pin B4 = getSubmodelPin("B4");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

		GUINandGate nandA1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandB1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandB2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandB3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandB4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY4 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB3 = new WireCrossPoint(submodelModifiable, 1);

		nandA1.moveTo(20, 2.5);
		nandB1.moveTo(20, 27.5);
		nandA2.moveTo(20, 52.5);
		nandB2.moveTo(20, 77.5);
		nandA3.moveTo(20, 102.5);
		nandB3.moveTo(20, 127.5);
		nandA4.moveTo(20, 152.5);
		nandB4.moveTo(20, 177.5);
		nandY1.moveTo(65, 2.5);
		nandY2.moveTo(65, 27.5);
		nandY3.moveTo(65, 52.5);
		nandY4.moveTo(65, 77.5);
		cpA1.moveCenterTo(7.5, 17.5);
		cpB1.moveCenterTo(5, 42.5);
		cpA2.moveCenterTo(7.5, 67.5);
		cpB2.moveCenterTo(5, 92.5);
		cpA3.moveCenterTo(7.5, 117.5);
		cpB3.moveCenterTo(5, 142.5);

		new GUIWire(submodelModifiable, A1, nandA1.getPin("A"), new Point(15, 62.5), new Point(15, 7.5));
		new GUIWire(submodelModifiable, A2, nandA2.getPin("A"), new Point(17.5, 87.5), new Point(17.5, 57.5));
		new GUIWire(submodelModifiable, A3, nandA3.getPin("A"), new Point(17.5, 112.5), new Point(17.5, 107.5));
		new GUIWire(submodelModifiable, A4, nandA4.getPin("A"), new Point(17.5, 137.5), new Point(17.5, 157.5));
		new GUIWire(submodelModifiable, B1, nandB1.getPin("A"), new Point(10, 162.5), new Point(10, 32.5));
		new GUIWire(submodelModifiable, B2, nandB2.getPin("A"), new Point(12.5, 187.5), new Point(12.5, 82.5));
		new GUIWire(submodelModifiable, B3, nandB3.getPin("A"), new Point(15, 212.5), new Point(15, 132.5));
		new GUIWire(submodelModifiable, B4, nandB4.getPin("A"), new Point(10, 237.5), new Point(10, 182.5));
		new GUIWire(submodelModifiable, SA, cpA1);
		new GUIWire(submodelModifiable, SB, cpB1);
		new GUIWire(submodelModifiable, cpA1, cpA2);
		new GUIWire(submodelModifiable, cpA1, nandA1.getPin("B"));
		new GUIWire(submodelModifiable, cpA2, cpA3);
		new GUIWire(submodelModifiable, cpA2, nandA2.getPin("B"));
		new GUIWire(submodelModifiable, cpA3, nandA3.getPin("B"));
		new GUIWire(submodelModifiable, cpA3, nandA4.getPin("B"), new Point(7.5, 167.5));
		new GUIWire(submodelModifiable, cpB1, cpB2);
		new GUIWire(submodelModifiable, cpB1, nandB1.getPin("B"));
		new GUIWire(submodelModifiable, cpB2, cpB3);
		new GUIWire(submodelModifiable, cpB2, nandB2.getPin("B"));
		new GUIWire(submodelModifiable, cpB3, nandB3.getPin("B"));
		new GUIWire(submodelModifiable, cpB3, nandB4.getPin("B"), new Point(5, 192.5));
		new GUIWire(submodelModifiable, nandA1.getPin("Y"), nandY1.getPin("A"), new Point(42.5, 12.5), new Point(42.5, 7.5));
		new GUIWire(submodelModifiable, nandB1.getPin("Y"), nandY1.getPin("B"), new Point(45, 37.5), new Point(45, 17.5));
		new GUIWire(submodelModifiable, nandA2.getPin("Y"), nandY2.getPin("A"), new Point(47.5, 62.5), new Point(47.5, 32.5));
		new GUIWire(submodelModifiable, nandB2.getPin("Y"), nandY2.getPin("B"), new Point(50, 87.5), new Point(50, 42.5));
		new GUIWire(submodelModifiable, nandA3.getPin("Y"), nandY3.getPin("A"), new Point(52.5, 112.5), new Point(52.5, 57.5));
		new GUIWire(submodelModifiable, nandB3.getPin("Y"), nandY3.getPin("B"), new Point(55, 137.5), new Point(55, 67.5));
		new GUIWire(submodelModifiable, nandA4.getPin("Y"), nandY4.getPin("A"), new Point(57.5, 162.5), new Point(57.5, 82.5));
		new GUIWire(submodelModifiable, nandB4.getPin("Y"), nandY4.getPin("B"), new Point(60, 187.5), new Point(60, 92.5));
		new GUIWire(submodelModifiable, nandY1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nandY2.getPin("Y"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nandY3.getPin("Y"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nandY4.getPin("Y"), Y4, new Point[0]);
	}
}