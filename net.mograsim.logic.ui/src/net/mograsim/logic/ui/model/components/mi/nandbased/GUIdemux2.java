package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdemux2 extends SimpleRectangularSubmodelComponent
{
	public GUIdemux2(ViewModelModifiable model)
	{
		super(model, 1, "GUIdemux2");
		setSubmodelScale(.4);
		setInputCount(2);
		setOutputCount(4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin S0 = getInputSubmodelPins().get(0);
		Pin S1 = getInputSubmodelPins().get(1);
		Pin Y00 = getOutputSubmodelPins().get(0);
		Pin Y01 = getOutputSubmodelPins().get(1);
		Pin Y10 = getOutputSubmodelPins().get(2);
		Pin Y11 = getOutputSubmodelPins().get(3);

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
		cpS01.moveTo(7.5, 12.5);
		cpS11.moveTo(5, 37.5);
		cpS02.moveTo(7.5, 17.5);
		cpS12.moveTo(5, 42.5);
		cpS03.moveTo(37.5, 62.5);
		cpS13.moveTo(32.5, 67.5);
		cpNotS0.moveTo(32.5, 12.5);
		cpNotS1.moveTo(35, 37.5);

		new GUIWire(submodelModifiable, S0, cpS01, new Point[0]);
		new GUIWire(submodelModifiable, S1, cpS11, new Point[0]);
		new GUIWire(submodelModifiable, cpS01, notS0.getInputPins().get(0), new Point(7.5, 7.5));
		new GUIWire(submodelModifiable, cpS11, notS1.getInputPins().get(0), new Point(5, 32.5));
		new GUIWire(submodelModifiable, cpS01, cpS02, new Point[0]);
		new GUIWire(submodelModifiable, cpS11, cpS12, new Point[0]);
		new GUIWire(submodelModifiable, cpS02, notS0.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpS12, notS1.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpS02, cpS03, new Point(7.5, 62.5));
		new GUIWire(submodelModifiable, cpS12, cpS13, new Point(5, 67.5), new Point(32.5, 67.5));
		new GUIWire(submodelModifiable, notS0.getOutputPin(), cpNotS0, new Point[0]);
		new GUIWire(submodelModifiable, notS1.getOutputPin(), cpNotS1, new Point[0]);
		new GUIWire(submodelModifiable, cpNotS0, andY00.getInputPins().get(0), new Point(32.5, 7.5));
		new GUIWire(submodelModifiable, cpNotS1, andY00.getInputPins().get(1), new Point(35, 17.5));
		new GUIWire(submodelModifiable, cpS03, andY01.getInputPins().get(0), new Point(37.5, 32.5));
		new GUIWire(submodelModifiable, cpNotS1, andY01.getInputPins().get(1), new Point(35, 42.5));
		new GUIWire(submodelModifiable, cpNotS0, andY10.getInputPins().get(0), new Point(32.5, 57.5));
		new GUIWire(submodelModifiable, cpS13, andY10.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpS03, andY11.getInputPins().get(0), new Point(37.5, 82.5));
		new GUIWire(submodelModifiable, cpS13, andY11.getInputPins().get(1), new Point(32.5, 92.5));
		new GUIWire(submodelModifiable, andY00.getOutputPins().get(0), Y00);
		new GUIWire(submodelModifiable, andY01.getOutputPins().get(0), Y01);
		new GUIWire(submodelModifiable, andY10.getOutputPins().get(0), Y10);
		new GUIWire(submodelModifiable, andY11.getOutputPins().get(0), Y11);
	}
}