package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.AtomicAm2901NANDBased;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public class AtomicAm2901Example
{
	public static void main(String[] args)
	{
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 1;
		params.wireTravelTime = 1;
		SimpleLogicUIStandalone.executeVisualisation(AtomicAm2901Example::createAm2901Example, params);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createAm2901Example(ViewModelModifiable model)
	{
		AtomicAm2901NANDBased am2901 = new AtomicAm2901NANDBased(model);
		am2901.moveTo(100, 0);
		for (int i = 0; i < am2901.inputNames.size(); i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, am2901.getPin(am2901.inputNames.get(i)), sw.getOutputPin());
		}
		for (int i = 0; i < am2901.outputNames.size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, am2901.getPin(am2901.outputNames.get(i)), bd.getInputPin());
		}
	}
}