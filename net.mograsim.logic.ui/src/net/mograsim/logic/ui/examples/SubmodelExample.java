package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.TestSubmodelNANDComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class SubmodelExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(SubmodelExample::createSubmodelExample);
	}

	@SuppressWarnings("unused") // GUIWires being created
	public static void createSubmodelExample(ViewModelModifiable model)
	{
		GUIManualSwitch swA = new GUIManualSwitch(model);
		swA.moveTo(0, 0);
		GUIManualSwitch swB = new GUIManualSwitch(model);
		swB.moveTo(0, 25);
		TestSubmodelNANDComponent nand = new TestSubmodelNANDComponent(model);
		nand.moveTo(30, 10);
		GUIBitDisplay bdY = new GUIBitDisplay(model);
		bdY.moveTo(70, 12.5);

		new GUIWire(model, swA.getOutputPin(), nand.getPins().get(0));
		new GUIWire(model, swB.getOutputPin(), nand.getPins().get(1));
		new GUIWire(model, nand.getPins().get(2), bdY.getInputPin());
	}
}