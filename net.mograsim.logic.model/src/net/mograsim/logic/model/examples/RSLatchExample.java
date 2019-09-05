package net.mograsim.logic.model.examples;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.atomic.ModelNotGate;
import net.mograsim.logic.model.model.components.atomic.ModelOrGate;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;

public class RSLatchExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(RSLatchExample::createRSLatchExample);
	}

	@SuppressWarnings("unused") // for Wires being created
	public static void createRSLatchExample(ViewModelModifiable model)
	{
		ModelManualSwitch rIn = new ModelManualSwitch(model, 1);
		rIn.moveTo(100, 100);
		ModelManualSwitch sIn = new ModelManualSwitch(model, 1);
		sIn.moveTo(100, 200);

		ModelOrGate or1 = new ModelOrGate(model, 1);
		or1.moveTo(160, 102.5);
		new ModelWire(model, rIn.getOutputPin(), or1.getPin("A"));

		ModelOrGate or2 = new ModelOrGate(model, 1);
		or2.moveTo(160, 192.5);
		new ModelWire(model, sIn.getOutputPin(), or2.getPin("B"));

		ModelNotGate not1 = new ModelNotGate(model, 1);
		not1.moveTo(200, 107.5);
		new ModelWire(model, or1.getPin("Y"), not1.getPin("A"));

		ModelNotGate not2 = new ModelNotGate(model, 1);
		not2.moveTo(200, 197.5);
		new ModelWire(model, or2.getPin("Y"), not2.getPin("A"));

		ModelWireCrossPoint p1 = new ModelWireCrossPoint(model, 1);
		p1.moveCenterTo(250, 112.5);
		new ModelWire(model, not1.getPin("Y"), p1);
		new ModelWire(model, p1, or2.getPin("A"), new Point(250, 130), new Point(140, 185), new Point(140, 197.5));

		ModelWireCrossPoint p2 = new ModelWireCrossPoint(model, 1);
		p2.moveCenterTo(250, 202.5);
		new ModelWire(model, not2.getPin("Y"), p2);
		new ModelWire(model, p2, or1.getPin("B"), new Point(250, 185), new Point(140, 130), new Point(140, 117.5));

		ModelWireCrossPoint o1 = new ModelWireCrossPoint(model, 1);
		o1.moveCenterTo(270, 112.5);
		new ModelWire(model, p1, o1);

		ModelWireCrossPoint o2 = new ModelWireCrossPoint(model, 1);
		o2.moveCenterTo(270, 202.5);
		new ModelWire(model, p2, o2);
	}
}