package era.mi.gui.examples;

import org.eclipse.swt.SWT;

import era.mi.gui.LogicUIStandalone;
import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.components.GUIManualSwitch;
import era.mi.gui.model.components.GUINotGate;
import era.mi.gui.model.wires.GUIWire;
import era.mi.gui.model.wires.WireCrossPoint;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class Playground
{
	public static void main(String[] args)
	{
		ViewModel model = new ViewModel();
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

		LogicUIStandalone ui = new LogicUIStandalone(model);

		ui.getLogicUICanvas().addListener(SWT.KeyDown, e -> notGate.moveTo(100, 10));
		ui.run();
	}

	public static void addComponentsAndWires(LogicUIStandalone ui, ViewModel model)
	{
		GUIAndGate andGate = new GUIAndGate(model, 1);
		andGate.moveTo(10, 10);
		GUINotGate notGate = new GUINotGate(model, 1);
		notGate.moveTo(10, 40);

		new GUIWire(model, andGate.getPins().get(0), notGate.getPins().get(1), new Point(20, 50));

		ui.getLogicUICanvas().addListener(SWT.KeyDown, e -> notGate.moveTo(150, 10));
	}
}