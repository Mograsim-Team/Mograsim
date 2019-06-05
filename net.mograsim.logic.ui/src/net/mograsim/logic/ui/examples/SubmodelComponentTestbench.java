package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIdlatch4;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class SubmodelComponentTestbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(SubmodelComponentTestbench::createTestbench);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createTestbench(ViewModelModifiable model)
	{
		SubmodelComponent comp = new GUIdlatch4(model);
		int inputCount = 5;

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