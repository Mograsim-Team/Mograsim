package era.mi.gui.examples;

import org.eclipse.swt.SWT;

import era.mi.gui.LogicUIStandalone;
import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.components.GUINotGate;
import era.mi.gui.model.wires.GUIWire;
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

		new GUIWire(model, andGate.getPins().get(0), notGate.getPins().get(1), new Point(20, 50));

		LogicUIStandalone ui = new LogicUIStandalone(model);

		ui.getLogicUICanvas().addListener(SWT.KeyDown, e -> notGate.moveTo(150, 10));
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