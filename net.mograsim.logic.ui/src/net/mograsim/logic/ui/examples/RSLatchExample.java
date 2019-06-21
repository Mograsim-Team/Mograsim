package net.mograsim.logic.ui.examples;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.GUINotGate;
import net.mograsim.logic.ui.model.components.GUIOrGate;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class RSLatchExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(RSLatchExample::createRSLatchExample);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createRSLatchExample(ViewModelModifiable model)
	{
		GUIManualSwitch rIn = new GUIManualSwitch(model);
		rIn.moveTo(100, 100);
		GUIManualSwitch sIn = new GUIManualSwitch(model);
		sIn.moveTo(100, 200);

		GUIOrGate or1 = new GUIOrGate(model, 1);
		or1.moveTo(160, 102.5);
		new GUIWire(model, rIn.getOutputPin(), or1.getPin("A"));

		GUIOrGate or2 = new GUIOrGate(model, 1);
		or2.moveTo(160, 192.5);
		new GUIWire(model, sIn.getOutputPin(), or2.getPin("B"));

		GUINotGate not1 = new GUINotGate(model, 1);
		not1.moveTo(200, 107.5);
		new GUIWire(model, or1.getPin("Y"), not1.getPin("A"));

		GUINotGate not2 = new GUINotGate(model, 1);
		not2.moveTo(200, 197.5);
		new GUIWire(model, or2.getPin("Y"), not2.getPin("A"));

		WireCrossPoint p1 = new WireCrossPoint(model, 1);
		p1.moveCenterTo(250, 112.5);
		new GUIWire(model, not1.getPin("Y"), p1);
		new GUIWire(model, p1, or2.getPin("A"), new Point(250, 130), new Point(140, 185), new Point(140, 197.5));

		WireCrossPoint p2 = new WireCrossPoint(model, 1);
		p2.moveCenterTo(250, 202.5);
		new GUIWire(model, not2.getPin("Y"), p2);
		new GUIWire(model, p2, or1.getPin("B"), new Point(250, 185), new Point(140, 130), new Point(140, 117.5));

		WireCrossPoint o1 = new WireCrossPoint(model, 1);
		o1.moveCenterTo(270, 112.5);
		new GUIWire(model, p1, o1);

		WireCrossPoint o2 = new WireCrossPoint(model, 1);
		o2.moveCenterTo(270, 202.5);
		new GUIWire(model, p2, o2);
	}
}