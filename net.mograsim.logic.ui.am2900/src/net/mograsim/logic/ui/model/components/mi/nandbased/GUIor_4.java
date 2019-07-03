package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIor_4 extends SimpleRectangularSubmodelComponent
{
	public GUIor_4(ViewModelModifiable model)
	{
		super(model, 1, "GUIor_4");
		setSubmodelScale(.4);
		setInputPins("A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4");
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
		Pin B1 = getSubmodelPin("B1");
		Pin B2 = getSubmodelPin("B2");
		Pin B3 = getSubmodelPin("B3");
		Pin B4 = getSubmodelPin("B4");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

		GUINandGate notA1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notA4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notB4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY4 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB4 = new WireCrossPoint(submodelModifiable, 1);

		notA1.moveTo(15, 2.5);
		notA2.moveTo(15, 27.5);
		notA3.moveTo(15, 52.5);
		notA4.moveTo(15, 77.5);
		notB1.moveTo(15, 102.5);
		notB2.moveTo(15, 127.5);
		notB3.moveTo(15, 152.5);
		notB4.moveTo(15, 177.5);
		nandY1.moveTo(65, 2.5);
		nandY2.moveTo(65, 27.5);
		nandY3.moveTo(65, 52.5);
		nandY4.moveTo(65, 77.5);
		cpA1.moveCenterTo(7.5, 12.5);
		cpA2.moveCenterTo(7.5, 37.5);
		cpA3.moveCenterTo(7.5, 62.5);
		cpA4.moveCenterTo(7.5, 87.5);
		cpB1.moveCenterTo(7.5, 112.5);
		cpB2.moveCenterTo(7.5, 137.5);
		cpB3.moveCenterTo(7.5, 162.5);
		cpB4.moveCenterTo(7.5, 187.5);

		new GUIWire(submodelModifiable, A1, cpA1, new Point[0]);
		new GUIWire(submodelModifiable, A2, cpA2, new Point[0]);
		new GUIWire(submodelModifiable, A3, cpA3, new Point[0]);
		new GUIWire(submodelModifiable, A4, cpA4, new Point[0]);
		new GUIWire(submodelModifiable, B1, cpB1, new Point[0]);
		new GUIWire(submodelModifiable, B2, cpB2, new Point[0]);
		new GUIWire(submodelModifiable, B3, cpB3, new Point[0]);
		new GUIWire(submodelModifiable, B4, cpB4, new Point[0]);
		new GUIWire(submodelModifiable, cpA1, notA1.getPin("A"), new Point(7.5, 7.5));
		new GUIWire(submodelModifiable, cpA1, notA1.getPin("B"), new Point(7.5, 17.5));
		new GUIWire(submodelModifiable, cpA2, notA2.getPin("A"), new Point(7.5, 32.5));
		new GUIWire(submodelModifiable, cpA2, notA2.getPin("B"), new Point(7.5, 42.5));
		new GUIWire(submodelModifiable, cpA3, notA3.getPin("A"), new Point(7.5, 57.5));
		new GUIWire(submodelModifiable, cpA3, notA3.getPin("B"), new Point(7.5, 67.5));
		new GUIWire(submodelModifiable, cpA4, notA4.getPin("A"), new Point(7.5, 82.5));
		new GUIWire(submodelModifiable, cpA4, notA4.getPin("B"), new Point(7.5, 92.5));
		new GUIWire(submodelModifiable, cpB1, notB1.getPin("A"), new Point(7.5, 107.5));
		new GUIWire(submodelModifiable, cpB1, notB1.getPin("B"), new Point(7.5, 117.5));
		new GUIWire(submodelModifiable, cpB2, notB2.getPin("A"), new Point(7.5, 132.5));
		new GUIWire(submodelModifiable, cpB2, notB2.getPin("B"), new Point(7.5, 142.5));
		new GUIWire(submodelModifiable, cpB3, notB3.getPin("A"), new Point(7.5, 157.5));
		new GUIWire(submodelModifiable, cpB3, notB3.getPin("B"), new Point(7.5, 167.5));
		new GUIWire(submodelModifiable, cpB4, notB4.getPin("A"), new Point(7.5, 182.5));
		new GUIWire(submodelModifiable, cpB4, notB4.getPin("B"), new Point(7.5, 192.5));
		new GUIWire(submodelModifiable, notA1.getPin("Y"), nandY1.getPin("A"), new Point(40, 12.5), new Point(40, 7.5));
		new GUIWire(submodelModifiable, notB1.getPin("Y"), nandY1.getPin("B"), new Point(45, 112.5), new Point(45, 17.5));
		new GUIWire(submodelModifiable, notA2.getPin("Y"), nandY2.getPin("A"), new Point(40, 37.5), new Point(40, 32.5));
		new GUIWire(submodelModifiable, notB2.getPin("Y"), nandY2.getPin("B"), new Point(50, 137.5), new Point(50, 42.5));
		new GUIWire(submodelModifiable, notA3.getPin("Y"), nandY3.getPin("A"), new Point(40, 62.5), new Point(40, 57.5));
		new GUIWire(submodelModifiable, notB3.getPin("Y"), nandY3.getPin("B"), new Point(55, 162.5), new Point(55, 67.5));
		new GUIWire(submodelModifiable, notA4.getPin("Y"), nandY4.getPin("A"), new Point(40, 87.5), new Point(40, 82.5));
		new GUIWire(submodelModifiable, notB4.getPin("Y"), nandY4.getPin("B"), new Point(60, 187.5), new Point(60, 92.5));
		new GUIWire(submodelModifiable, nandY1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nandY2.getPin("Y"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nandY3.getPin("Y"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nandY4.getPin("Y"), Y4, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentProvider(GUIor_4.class.getCanonicalName(), (m, p) -> new GUIor_4(m));
	}
}