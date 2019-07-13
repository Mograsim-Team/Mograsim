package net.mograsim.logic.model.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIAm2901DestDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901DestDecode(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIAm2901DestDecode(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIAm2901DestDecode", name);
		setSubmodelScale(.25);
		setInputPins("I8", "I7", "I6");
		setOutputPins("NSH", "RSH", "RAMWE", "YF", "LSH", "QWE");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I8 = getSubmodelPin("I8");
		Pin I7 = getSubmodelPin("I7");
		Pin I6 = getSubmodelPin("I6");
		Pin NSH = getSubmodelPin("NSH");
		Pin RSH = getSubmodelPin("RSH");
		Pin RAMWE = getSubmodelPin("RAMWE");
		Pin YF = getSubmodelPin("YF");
		Pin LSH = getSubmodelPin("LSH");
		Pin QWE = getSubmodelPin("QWE");

		GUINandGate notI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI7 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI8I7 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI6 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandRSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandRAMWE = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI7NotI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandLSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate notRSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate andI7NotI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandQWE = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYF = new GUINandGate(submodelModifiable, 1);
		GUINandGate notQWE = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI83 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI71 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI72 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI73 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI6 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI7 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI8I7 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotRSH = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI7NotI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI7NotI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI6 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandQWE = new WireCrossPoint(submodelModifiable, 1);

		notI8.moveTo(15, 10);
		notI7.moveTo(15, 50);
		nandI8I7.moveTo(15, 90);
		notI6.moveTo(15, 150);
		nandRSH.moveTo(50, 10);
		nandRAMWE.moveTo(50, 50);
		nandI7NotI8.moveTo(50, 90);
		nandLSH.moveTo(50, 130);
		notRSH.moveTo(80, 10);
		andI7NotI8.moveTo(80, 90);
		nandQWE.moveTo(80, 145);
		nandYF.moveTo(110, 105);
		notQWE.moveTo(115, 210);
		cpI81.moveCenterTo(5, 20);
		cpI82.moveCenterTo(5, 15);
		cpI83.moveCenterTo(5, 25);
		cpI71.moveCenterTo(10, 60);
		cpI72.moveCenterTo(10, 65);
		cpI73.moveCenterTo(10, 105);
		cpI6.moveCenterTo(5, 155);
		cpNotI7.moveCenterTo(40, 60);
		cpNotI81.moveCenterTo(45, 20);
		cpNotI82.moveCenterTo(45, 55);
		cpNandI8I7.moveCenterTo(40, 135);
		cpNotRSH.moveCenterTo(75, 20);
		cpNandI7NotI81.moveCenterTo(75, 100);
		cpNandI7NotI82.moveCenterTo(75, 105);
		cpNotI6.moveCenterTo(75, 160);
		cpNandQWE.moveCenterTo(110, 215);

		new GUIWire(submodelModifiable, I8, cpI81, new Point[0]);
		new GUIWire(submodelModifiable, cpI81, cpI82, new Point[0]);
		new GUIWire(submodelModifiable, cpI81, cpI83, new Point[0]);
		new GUIWire(submodelModifiable, cpI82, notI8.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpI83, notI8.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, I7, cpI71, new Point[0]);
		new GUIWire(submodelModifiable, cpI71, notI7.getPin("A"), new Point(10, 55));
		new GUIWire(submodelModifiable, cpI71, cpI72, new Point[0]);
		new GUIWire(submodelModifiable, cpI72, notI7.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI72, cpI73, new Point[0]);
		new GUIWire(submodelModifiable, cpI73, nandI8I7.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI83, nandI8I7.getPin("A"), new Point(5, 95));
		new GUIWire(submodelModifiable, I6, cpI6, new Point(5, 100));
		new GUIWire(submodelModifiable, cpI6, notI6.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpI6, notI6.getPin("B"), new Point(5, 165));
		new GUIWire(submodelModifiable, cpI82, nandRSH.getPin("A"), new Point(5, 5), new Point(40, 5), new Point(40, 15));
		new GUIWire(submodelModifiable, notI7.getPin("Y"), cpNotI7, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI7, nandRSH.getPin("B"), new Point(40, 25));
		new GUIWire(submodelModifiable, cpNotI7, nandRAMWE.getPin("B"), new Point(40, 65));
		new GUIWire(submodelModifiable, notI8.getPin("Y"), cpNotI81, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI81, NSH, new Point(45, 5), new Point(135, 5), new Point(135, 20));
		new GUIWire(submodelModifiable, cpNotI81, cpNotI82, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI82, nandRAMWE.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI82, nandI7NotI8.getPin("A"), new Point(45, 95));
		new GUIWire(submodelModifiable, cpI73, nandI7NotI8.getPin("B"), new Point(10, 115), new Point(45, 115), new Point(45, 105));
		new GUIWire(submodelModifiable, nandI8I7.getPin("Y"), cpNandI8I7, new Point(40, 100));
		new GUIWire(submodelModifiable, cpNandI8I7, nandLSH.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpNandI8I7, nandLSH.getPin("B"), new Point(40, 145));
		new GUIWire(submodelModifiable, nandRSH.getPin("Y"), cpNotRSH, new Point[0]);
		new GUIWire(submodelModifiable, cpNotRSH, notRSH.getPin("A"), new Point(75, 15));
		new GUIWire(submodelModifiable, cpNotRSH, notRSH.getPin("B"), new Point(75, 25));
		new GUIWire(submodelModifiable, nandRAMWE.getPin("Y"), RAMWE, new Point(125, 60), new Point(125, 100));
		new GUIWire(submodelModifiable, nandI7NotI8.getPin("Y"), cpNandI7NotI81, new Point[0]);
		new GUIWire(submodelModifiable, cpNandI7NotI81, andI7NotI8.getPin("A"), new Point(75, 95));
		new GUIWire(submodelModifiable, cpNandI7NotI81, cpNandI7NotI82, new Point[0]);
		new GUIWire(submodelModifiable, cpNandI7NotI82, andI7NotI8.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nandLSH.getPin("Y"), LSH, new Point(125, 140), new Point(125, 180));
		new GUIWire(submodelModifiable, cpNandI7NotI82, nandQWE.getPin("A"), new Point(75, 150));
		new GUIWire(submodelModifiable, notI6.getPin("Y"), cpNotI6, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI6, nandQWE.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, notRSH.getPin("Y"), RSH, new Point(130, 20), new Point(130, 60));
		new GUIWire(submodelModifiable, andI7NotI8.getPin("Y"), nandYF.getPin("A"));
		new GUIWire(submodelModifiable, cpNotI6, nandYF.getPin("B"), new Point(75, 170), new Point(105, 170), new Point(105, 120));
		new GUIWire(submodelModifiable, nandQWE.getPin("Y"), cpNandQWE, new Point(110, 155));
		new GUIWire(submodelModifiable, cpNandQWE, notQWE.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpNandQWE, notQWE.getPin("B"), new Point(110, 225));
		new GUIWire(submodelModifiable, nandYF.getPin("Y"), YF);
		new GUIWire(submodelModifiable, notQWE.getPin("Y"), QWE, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901DestDecode.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2901DestDecode(m, n));
	}
}