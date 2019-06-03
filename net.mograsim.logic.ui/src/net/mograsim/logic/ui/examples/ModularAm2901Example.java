package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUI_rsLatch;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public class ModularAm2901Example
{
	public static void main(String[] args)
	{
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 1;
		params.wireTravelTime = 1;
		SimpleLogicUIStandalone.executeVisualisation(ModularAm2901Example::createAm2901Example, params);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createAm2901Example(ViewModelModifiable model)
	{
		SubmodelComponent comp = new GUI_rsLatch(model);
		int inputCount = 2;

		comp.moveTo(100, 0);
		for (int i = 0; i < inputCount; i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPins().get(i), sw.getOutputPin());
		}
		for (int i = inputCount; i < comp.getPins().size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(200, 20 * (i - inputCount));
			new GUIWire(model, comp.getPins().get(i), bd.getInputPin());
		}
	}
}