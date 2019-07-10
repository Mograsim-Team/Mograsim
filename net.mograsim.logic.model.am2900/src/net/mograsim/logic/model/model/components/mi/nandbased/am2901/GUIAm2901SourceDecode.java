package net.mograsim.logic.model.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIAm2901SourceDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901SourceDecode(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIAm2901SourceDecode(ViewModelModifiable model, String name)
	{
		super(model, 1, "Am2901SourceDecode", name);
		setSubmodelScale(.25);
		setInputPins("I2", "I1", "I0");
		setOutputPins("SQ", "RA", "SB", "SA", "RD");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I2 = getSubmodelPin("I2");
		Pin I1 = getSubmodelPin("I1");
		Pin I0 = getSubmodelPin("I0");
		Pin SQ = getSubmodelPin("SQ");
		Pin RA = getSubmodelPin("RA");
		Pin SB = getSubmodelPin("SB");
		Pin SA = getSubmodelPin("SA");
		Pin RD = getSubmodelPin("RD");

		GUINandGate notI2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand21 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand22 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand23 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand24 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand25 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand31 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand32 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand33 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand34 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand35 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand41 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand42 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpI21 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI22 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI23 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI01 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI02 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI11 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI12 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI13 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI0 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNand22 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNand23 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNand24 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNand31 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNand35 = new WireCrossPoint(submodelModifiable, 1);

		notI2.moveTo(10, 10);
		notI1.moveTo(10, 50);
		notI0.moveTo(10, 90);
		nand21.moveTo(40, 10);
		nand22.moveTo(40, 50);
		nand23.moveTo(40, 90);
		nand24.moveTo(40, 130);
		nand25.moveTo(40, 170);
		nand31.moveTo(70, 10);
		nand32.moveTo(70, 50);
		nand33.moveTo(70, 90);
		nand34.moveTo(70, 130);
		nand35.moveTo(70, 170);
		nand41.moveTo(100, 10);
		nand42.moveTo(100, 170);
		cpI21.moveCenterTo(5, 20);
		cpI22.moveCenterTo(7.5, 20);
		cpI23.moveCenterTo(5, 145);
		cpI1.moveCenterTo(7.5, 60);
		cpI01.moveCenterTo(7.5, 100);
		cpI02.moveCenterTo(7.5, 105);
		cpNotI2.moveCenterTo(32.5, 55);
		cpNotI11.moveCenterTo(35, 60);
		cpNotI12.moveCenterTo(35, 65);
		cpNotI13.moveCenterTo(35, 135);
		cpNotI0.moveCenterTo(37.5, 100);
		cpNand22.moveCenterTo(65, 60);
		cpNand23.moveCenterTo(65, 100);
		cpNand24.moveCenterTo(65, 140);
		cpNand31.moveCenterTo(95, 20);
		cpNand35.moveCenterTo(95, 180);

		new GUIWire(submodelModifiable, I2, cpI21, new Point[0]);
		new GUIWire(submodelModifiable, cpI21, nand21.getPin("A"), new Point(5, 5), new Point(35, 5), new Point(35, 15));
		new GUIWire(submodelModifiable, cpI21, cpI22, new Point[0]);
		new GUIWire(submodelModifiable, cpI22, notI2.getPin("A"), new Point(7.5, 15));
		new GUIWire(submodelModifiable, cpI22, notI2.getPin("B"), new Point(7.5, 25));
		new GUIWire(submodelModifiable, cpI21, cpI23, new Point[0]);
		new GUIWire(submodelModifiable, cpI23, nand24.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI23, nand35.getPin("B"), new Point(5, 195), new Point(65, 195), new Point(65, 185));
		new GUIWire(submodelModifiable, I1, cpI1, new Point[0]);
		new GUIWire(submodelModifiable, cpI1, notI1.getPin("A"), new Point(7.5, 55));
		new GUIWire(submodelModifiable, cpI1, notI1.getPin("B"), new Point(7.5, 65));
		new GUIWire(submodelModifiable, I0, cpI01, new Point[0]);
		new GUIWire(submodelModifiable, cpI01, notI0.getPin("A"), new Point(7.5, 95));
		new GUIWire(submodelModifiable, cpI01, cpI02, new Point[0]);
		new GUIWire(submodelModifiable, cpI02, notI0.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI02, nand23.getPin("B"), new Point(7.5, 112.5), new Point(32.5, 112.5), new Point(32.5, 105));
		new GUIWire(submodelModifiable, notI2.getPin("Y"), cpNotI2, new Point(32.5, 20));
		new GUIWire(submodelModifiable, cpNotI2, nand22.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI2, nand23.getPin("A"), new Point(32.5, 95));
		new GUIWire(submodelModifiable, notI1.getPin("Y"), cpNotI11, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI11, nand21.getPin("B"), new Point(35, 25));
		new GUIWire(submodelModifiable, cpNotI11, cpNotI12, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI12, nand22.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI12, cpNotI13, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI13, nand24.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI13, nand25.getPin("A"), new Point(35, 175));
		new GUIWire(submodelModifiable, notI0.getPin("Y"), cpNotI0, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI0, nand31.getPin("B"), new Point(37.5, 35), new Point(65, 35), new Point(65, 25));
		new GUIWire(submodelModifiable, cpNotI0, nand25.getPin("B"), new Point(37.5, 185));
		new GUIWire(submodelModifiable, nand21.getPin("Y"), nand31.getPin("A"));
		new GUIWire(submodelModifiable, nand22.getPin("Y"), cpNand22, new Point[0]);
		new GUIWire(submodelModifiable, cpNand22, nand32.getPin("A"), new Point(65, 55));
		new GUIWire(submodelModifiable, cpNand22, nand32.getPin("B"), new Point(65, 65));
		new GUIWire(submodelModifiable, nand23.getPin("Y"), cpNand23, new Point[0]);
		new GUIWire(submodelModifiable, cpNand23, nand33.getPin("A"), new Point(65, 95));
		new GUIWire(submodelModifiable, cpNand23, nand33.getPin("B"), new Point(65, 105));
		new GUIWire(submodelModifiable, nand24.getPin("Y"), cpNand24, new Point[0]);
		new GUIWire(submodelModifiable, cpNand24, nand34.getPin("A"), new Point(65, 135));
		new GUIWire(submodelModifiable, cpNand24, nand34.getPin("B"), new Point(65, 145));
		new GUIWire(submodelModifiable, nand25.getPin("Y"), nand35.getPin("A"));
		new GUIWire(submodelModifiable, nand31.getPin("Y"), cpNand31, new Point[0]);
		new GUIWire(submodelModifiable, cpNand31, nand41.getPin("A"), new Point(95, 15));
		new GUIWire(submodelModifiable, cpNand31, nand41.getPin("B"), new Point(95, 25));
		new GUIWire(submodelModifiable, nand32.getPin("Y"), RA, new Point[0]);
		new GUIWire(submodelModifiable, nand33.getPin("Y"), SB, new Point[0]);
		new GUIWire(submodelModifiable, nand34.getPin("Y"), SA, new Point[0]);
		new GUIWire(submodelModifiable, nand35.getPin("Y"), cpNand35, new Point[0]);
		new GUIWire(submodelModifiable, cpNand35, nand42.getPin("A"), new Point(95, 175));
		new GUIWire(submodelModifiable, cpNand35, nand42.getPin("B"), new Point(95, 185));
		new GUIWire(submodelModifiable, nand41.getPin("Y"), SQ, new Point[0]);
		new GUIWire(submodelModifiable, nand42.getPin("Y"), RD, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901SourceDecode.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2901SourceDecode(m, n));
	}
}