package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIdemux2 extends SimpleRectangularSubmodelComponent
{
	public GUIdemux2(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIdemux2(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIdemux2", name);
		setSubmodelScale(.4);
		setInputPins("S0", "S1");
		setOutputPins("Y00", "Y01", "Y10", "Y11");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin S0 = getSubmodelPin("S0");
		Pin S1 = getSubmodelPin("S1");
		Pin Y00 = getSubmodelPin("Y00");
		Pin Y01 = getSubmodelPin("Y01");
		Pin Y10 = getSubmodelPin("Y10");
		Pin Y11 = getSubmodelPin("Y11");

		GUINandGate notS0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notS1 = new GUINandGate(submodelModifiable, 1);
		GUIand andY00 = new GUIand(submodelModifiable);
		GUIand andY01 = new GUIand(submodelModifiable);
		GUIand andY10 = new GUIand(submodelModifiable);
		GUIand andY11 = new GUIand(submodelModifiable);

		WireCrossPoint cpS01 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpS02 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpS03 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpS11 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpS12 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpS13 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotS0 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotS1 = new WireCrossPoint(submodelModifiable, 1);

		notS0.moveTo(10, 2.5);
		notS1.moveTo(10, 27.5);
		andY00.moveTo(40, 2.5);
		andY01.moveTo(40, 27.5);
		andY10.moveTo(40, 52.5);
		andY11.moveTo(40, 77.5);
		cpS01.moveCenterTo(7.5, 12.5);
		cpS11.moveCenterTo(5, 37.5);
		cpS02.moveCenterTo(7.5, 17.5);
		cpS12.moveCenterTo(5, 42.5);
		cpS03.moveCenterTo(37.5, 62.5);
		cpS13.moveCenterTo(32.5, 67.5);
		cpNotS0.moveCenterTo(32.5, 12.5);
		cpNotS1.moveCenterTo(35, 37.5);

		new GUIWire(submodelModifiable, S0, cpS01, new Point[0]);
		new GUIWire(submodelModifiable, S1, cpS11, new Point[0]);
		new GUIWire(submodelModifiable, cpS01, notS0.getPin("A"), new Point(7.5, 7.5));
		new GUIWire(submodelModifiable, cpS11, notS1.getPin("A"), new Point(5, 32.5));
		new GUIWire(submodelModifiable, cpS01, cpS02, new Point[0]);
		new GUIWire(submodelModifiable, cpS11, cpS12, new Point[0]);
		new GUIWire(submodelModifiable, cpS02, notS0.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpS12, notS1.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpS02, cpS03, new Point(7.5, 62.5));
		new GUIWire(submodelModifiable, cpS12, cpS13, new Point(5, 67.5), new Point(32.5, 67.5));
		new GUIWire(submodelModifiable, notS0.getPin("Y"), cpNotS0, new Point[0]);
		new GUIWire(submodelModifiable, notS1.getPin("Y"), cpNotS1, new Point[0]);
		new GUIWire(submodelModifiable, cpNotS0, andY00.getPin("A"), new Point(32.5, 7.5));
		new GUIWire(submodelModifiable, cpNotS1, andY00.getPin("B"), new Point(35, 17.5));
		new GUIWire(submodelModifiable, cpS03, andY01.getPin("A"), new Point(37.5, 32.5));
		new GUIWire(submodelModifiable, cpNotS1, andY01.getPin("B"), new Point(35, 42.5));
		new GUIWire(submodelModifiable, cpNotS0, andY10.getPin("A"), new Point(32.5, 57.5));
		new GUIWire(submodelModifiable, cpS13, andY10.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpS03, andY11.getPin("A"), new Point(37.5, 82.5));
		new GUIWire(submodelModifiable, cpS13, andY11.getPin("B"), new Point(32.5, 92.5));
		new GUIWire(submodelModifiable, andY00.getPin("Y"), Y00);
		new GUIWire(submodelModifiable, andY01.getPin("Y"), Y01);
		new GUIWire(submodelModifiable, andY10.getPin("Y"), Y10);
		new GUIWire(submodelModifiable, andY11.getPin("Y"), Y11);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIdemux2.class.getCanonicalName(), (m, p, n) -> new GUIdemux2(m, n));
	}
}