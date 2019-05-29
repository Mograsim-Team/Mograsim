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
	private static final int WIRE_DELAY = 10;
	private static final int OR_DELAY = 50;
	private static final int NOT_DELAY = 50;

	public static void main(String[] args)
	{
		ViewModel model = new ViewModel();
		LogicUIStandalone ui = new LogicUIStandalone(model);
		addComponentsAndWires(ui, model);
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