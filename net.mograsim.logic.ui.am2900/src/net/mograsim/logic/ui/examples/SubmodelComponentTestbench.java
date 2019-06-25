package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.am2901.GUIAm2901;
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
		SimpleRectangularSubmodelComponent comp = new GUIAm2901(model);

		comp.moveTo(100, 0);
		for (int i = 0; i < comp.getInputPinNames().size(); i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPin(comp.getInputPinNames().get(i)), sw.getOutputPin());
		}
		for (int i = 0; i < comp.getOutputPinNames().size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, comp.getPin(comp.getOutputPinNames().get(i)), bd.getInputPin());
		}
	}
}