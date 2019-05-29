package era.mi.gui.examples;

import era.mi.gui.LogicUIStandalone;
import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.components.GUIManualSwitch;
import era.mi.gui.model.components.GUINotGate;
import era.mi.gui.model.components.GUIOrGate;
import era.mi.gui.model.wires.GUIWire;
import era.mi.gui.model.wires.WireCrossPoint;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class Playground
{
	public static void main(String[] args)
	{
		ViewModel model = new ViewModel();
		createRSLatchExample(model);
		LogicUIStandalone ui = new LogicUIStandalone(model);
		ui.run();
	}

	private static void createRSLatchExample(ViewModel model)
	{
		GUIManualSwitch rIn = new GUIManualSwitch(model);
		rIn.moveTo(100, 100);
		GUIManualSwitch sIn = new GUIManualSwitch(model);
		sIn.moveTo(100, 200);

		GUIOrGate or1 = new GUIOrGate(model, 1);
		or1.moveTo(160, 102.5);
		new GUIWire(model, rIn.getOutputPin(), or1.getInputPins().get(0));

		GUIOrGate or2 = new GUIOrGate(model, 1);
		or2.moveTo(160, 192.5);
		new GUIWire(model, sIn.getOutputPin(), or2.getInputPins().get(1));

		GUINotGate not1 = new GUINotGate(model, 1);
		not1.moveTo(200, 107.5);
		new GUIWire(model, or1.getOutputPin(), not1.getInputPins().get(0));

		GUINotGate not2 = new GUINotGate(model, 1);
		not2.moveTo(200, 197.5);
		new GUIWire(model, or2.getOutputPin(), not2.getInputPins().get(0));

		WireCrossPoint p1 = new WireCrossPoint(model, 1);
		p1.moveTo(250, 112.5);
		new GUIWire(model, not1.getOutputPin(), p1.getPin());
		new GUIWire(model, p1.getPin(), or2.getInputPins().get(0), new Point(250, 130), new Point(140, 185), new Point(140, 197.5));

		WireCrossPoint p2 = new WireCrossPoint(model, 1);
		p2.moveTo(250, 202.5);
		new GUIWire(model, not2.getOutputPin(), p2.getPin());
		new GUIWire(model, p2.getPin(), or1.getInputPins().get(1), new Point(250, 185), new Point(140, 130), new Point(140, 117.5));

		WireCrossPoint o1 = new WireCrossPoint(model, 1);
		o1.moveTo(270, 112.5);
		new GUIWire(model, p1.getPin(), o1.getPin());

		WireCrossPoint o2 = new WireCrossPoint(model, 1);
		o2.moveTo(270, 202.5);
		new GUIWire(model, p2.getPin(), o2.getPin());
	}

	@SuppressWarnings("unused")
	private static void createBasicExample(ViewModel model)
	{
		GUIAndGate andGate = new GUIAndGate(model, 1);
		andGate.moveTo(10, 10);
		GUINotGate notGate = new GUINotGate(model, 1);
		notGate.moveTo(10, 40);

		WireCrossPoint wcp1 = new WireCrossPoint(model, 1);
		wcp1.moveTo(150, 10);

		new GUIWire(model, andGate.getOutputPin(), notGate.getInputPins().get(0), new Point(60, 50));
		new GUIWire(model, notGate.getOutputPin(), wcp1.getPin());

		GUIManualSwitch sw1 = new GUIManualSwitch(model);
		sw1.moveTo(-20, 0);
		GUIManualSwitch sw2 = new GUIManualSwitch(model);
		sw2.moveTo(-20, 50);

		new GUIWire(model, sw1.getOutputPin(), andGate.getInputPins().get(0));
		new GUIWire(model, sw2.getOutputPin(), andGate.getInputPins().get(1));
	}
}