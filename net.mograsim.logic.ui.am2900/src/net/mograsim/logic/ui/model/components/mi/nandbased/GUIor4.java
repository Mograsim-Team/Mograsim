package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIor4 extends SimpleRectangularSubmodelComponent
{
	public GUIor4(ViewModelModifiable model)
	{
		super(model, 1, "GUIor4");
		setSubmodelScale(.2);
		setInputPins("A1", "A2", "A3", "A4");
		setOutputPins("Y");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin Y = getSubmodelPin("Y");

		GUINandGate nandA1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandA4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate or12 = new GUINandGate(submodelModifiable, 1);
		GUINandGate or34 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nor12 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nor34 = new GUINandGate(submodelModifiable, 1);
		GUINandGate or1234 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpOr12 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpOr34 = new WireCrossPoint(submodelModifiable, 1);

		nandA1.moveTo(20, 15);
		nandA2.moveTo(20, 65);
		nandA3.moveTo(20, 115);
		nandA4.moveTo(20, 165);
		or12.moveTo(50, 40);
		or34.moveTo(50, 140);
		nor12.moveTo(110, 40);
		nor34.moveTo(110, 140);
		or1234.moveTo(140, 90);
		cpA1.moveCenterTo(15, 25);
		cpA2.moveCenterTo(15, 75);
		cpA3.moveCenterTo(15, 125);
		cpA4.moveCenterTo(15, 175);
		cpOr12.moveCenterTo(105, 50);
		cpOr34.moveCenterTo(105, 150);

		new GUIWire(submodelModifiable, A1, cpA1, new Point[0]);
		new GUIWire(submodelModifiable, A2, cpA2, new Point[0]);
		new GUIWire(submodelModifiable, A3, cpA3, new Point[0]);
		new GUIWire(submodelModifiable, A4, cpA4, new Point[0]);
		new GUIWire(submodelModifiable, cpA1, nandA1.getPin("A"), new Point(15, 20));
		new GUIWire(submodelModifiable, cpA2, nandA2.getPin("A"), new Point(15, 70));
		new GUIWire(submodelModifiable, cpA3, nandA3.getPin("A"), new Point(15, 120));
		new GUIWire(submodelModifiable, cpA4, nandA4.getPin("A"), new Point(15, 170));
		new GUIWire(submodelModifiable, cpA1, nandA1.getPin("B"), new Point(15, 30));
		new GUIWire(submodelModifiable, cpA2, nandA2.getPin("B"), new Point(15, 80));
		new GUIWire(submodelModifiable, cpA3, nandA3.getPin("B"), new Point(15, 130));
		new GUIWire(submodelModifiable, cpA4, nandA4.getPin("B"), new Point(15, 180));
		new GUIWire(submodelModifiable, nandA1.getPin("Y"), or12.getPin("A"));
		new GUIWire(submodelModifiable, nandA2.getPin("Y"), or12.getPin("B"));
		new GUIWire(submodelModifiable, nandA3.getPin("Y"), or34.getPin("A"));
		new GUIWire(submodelModifiable, nandA4.getPin("Y"), or34.getPin("B"));
		new GUIWire(submodelModifiable, or12.getPin("Y"), cpOr12, new Point[0]);
		new GUIWire(submodelModifiable, or34.getPin("Y"), cpOr34, new Point[0]);
		new GUIWire(submodelModifiable, cpOr12, nor12.getPin("A"), new Point(105, 45));
		new GUIWire(submodelModifiable, cpOr12, nor12.getPin("B"), new Point(105, 55));
		new GUIWire(submodelModifiable, cpOr34, nor34.getPin("A"), new Point(105, 145));
		new GUIWire(submodelModifiable, cpOr34, nor34.getPin("B"), new Point(105, 155));
		new GUIWire(submodelModifiable, nor12.getPin("Y"), or1234.getPin("A"));
		new GUIWire(submodelModifiable, nor34.getPin("Y"), or1234.getPin("B"));
		new GUIWire(submodelModifiable, or1234.getPin("Y"), Y);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentProvider(GUIor4.class.getCanonicalName(), (m, p) -> new GUIor4(m));
	}
}