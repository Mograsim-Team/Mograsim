package era.mi.gui.examples;

import era.mi.gui.LogicUICanvas;
import era.mi.gui.LogicUIStandalone;
import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.components.GUINotGate;

public class Playground
{
	private static final int WIRE_DELAY = 10;
	private static final int OR_DELAY = 50;
	private static final int NOT_DELAY = 50;

	public static void main(String[] args)
	{
		ViewModel model = new ViewModel();
		LogicUIStandalone ui = new LogicUIStandalone(model);
		addComponentsAndWires(ui.getLogicUICanvas(), model);
		ui.run();
	}

	public static void addComponentsAndWires(LogicUICanvas ui, ViewModel model)
	{
		GUIAndGate andGate = ui.addComponent(new GUIAndGate(model));
		andGate.moveTo(10, 10);
		GUINotGate notGate = ui.addComponent(new GUINotGate(model));
		notGate.moveTo(10, 40);

		ui.addWire(andGate.getPins().get(0), notGate.getPins().get(1));
	}
}