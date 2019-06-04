package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdemux2 extends SubmodelComponent
{
	private final Pin pinS0;
	private final Pin pinS1;
	private final Pin pinY00;
	private final Pin pinY01;
	private final Pin pinY10;
	private final Pin pinY11;

	public GUIdemux2(ViewModelModifiable model)
	{
		super(model, "GUIdemux2");
		setSize(40, 42.5);
		setSubmodelScale(.4);

		Pin S0 = addSubmodelInterface(1, 0, 5);
		Pin S1 = addSubmodelInterface(1, 0, 15);
		Pin Y00 = addSubmodelInterface(1, 40, 5);
		Pin Y01 = addSubmodelInterface(1, 40, 15);
		Pin Y10 = addSubmodelInterface(1, 40, 25);
		Pin Y11 = addSubmodelInterface(1, 40, 35);

		this.pinS0 = getSupermodelPin(S0);
		this.pinS1 = getSupermodelPin(S1);
		this.pinY00 = getSupermodelPin(Y00);
		this.pinY01 = getSupermodelPin(Y01);
		this.pinY10 = getSupermodelPin(Y10);
		this.pinY11 = getSupermodelPin(Y11);

		initSubmodelComponents(S0, S1, Y00, Y01, Y10, Y11);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin S0, Pin S1, Pin Y00, Pin Y01, Pin Y10, Pin Y11)
	{
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

		notS0.moveTo(15, 2.5);
		notS1.moveTo(15, 27.5);
		andY00.moveTo(55, 2.5);
		andY01.moveTo(55, 27.5);
		andY10.moveTo(55, 52.5);
		andY11.moveTo(55, 77.5);
		cpS01.moveTo(5, 12.5);
		cpS11.moveTo(10, 37.5);
		cpS02.moveTo(5, 17.5);
		cpS12.moveTo(10, 42.5);
		cpS03.moveTo(50, 67.5);
		cpS13.moveTo(40, 72.5);
		cpNotS0.moveTo(40, 12.5);
		cpNotS1.moveTo(45, 37.5);

		new GUIWire(submodelModifiable, S0, cpS01.getPin());
		new GUIWire(submodelModifiable, S1, cpS11.getPin());
		new GUIWire(submodelModifiable, cpS01.getPin(), notS0.getInputPins().get(0), new Point(5, 7.5));
		new GUIWire(submodelModifiable, cpS11.getPin(), notS1.getInputPins().get(0), new Point(10, 32.5));
		new GUIWire(submodelModifiable, cpS01.getPin(), cpS02.getPin());
		new GUIWire(submodelModifiable, cpS11.getPin(), cpS12.getPin());
		new GUIWire(submodelModifiable, cpS02.getPin(), notS0.getInputPins().get(1));
		new GUIWire(submodelModifiable, cpS12.getPin(), notS1.getInputPins().get(1));
		new GUIWire(submodelModifiable, cpS02.getPin(), cpS03.getPin(), new Point(5, 67.5));
		new GUIWire(submodelModifiable, cpS12.getPin(), cpS13.getPin(), new Point(10, 62.5), new Point(40, 62.5));
		new GUIWire(submodelModifiable, notS0.getOutputPin(), cpNotS0.getPin());
		new GUIWire(submodelModifiable, notS1.getOutputPin(), cpNotS1.getPin());
		new GUIWire(submodelModifiable, cpNotS0.getPin(), andY00.getPinA(), new Point(40, 7.5));
		new GUIWire(submodelModifiable, cpNotS1.getPin(), andY00.getPinB(), new Point(45, 22.5));
		new GUIWire(submodelModifiable, cpS03.getPin(), andY01.getPinA(), new Point(50, 32.5));
		new GUIWire(submodelModifiable, cpNotS1.getPin(), andY01.getPinB(), new Point(45, 47.5));
		new GUIWire(submodelModifiable, cpNotS0.getPin(), andY10.getPinA(), new Point(40, 57.5));
		new GUIWire(submodelModifiable, cpS13.getPin(), andY10.getPinB());
		new GUIWire(submodelModifiable, cpS03.getPin(), andY11.getPinA(), new Point(50, 82.5));
		new GUIWire(submodelModifiable, cpS13.getPin(), andY11.getPinB(), new Point(40, 97.5));
		new GUIWire(submodelModifiable, andY00.getPinY(), Y00, new Point(95, 15), new Point(95, 12.5));
		new GUIWire(submodelModifiable, andY01.getPinY(), Y01, new Point(95, 40), new Point(95, 37.5));
		new GUIWire(submodelModifiable, andY10.getPinY(), Y10, new Point(95, 65), new Point(95, 62.5));
		new GUIWire(submodelModifiable, andY11.getPinY(), Y11, new Point(95, 90), new Point(95, 87.5));
	}

	public Pin getPinS0()
	{
		return pinS0;
	}

	public Pin getPinS1()
	{
		return pinS1;
	}

	public Pin getPinY00()
	{
		return pinY00;
	}

	public Pin getPinY01()
	{
		return pinY01;
	}

	public Pin getPinY10()
	{
		return pinY10;
	}

	public Pin getPinY11()
	{
		return pinY11;
	}
}