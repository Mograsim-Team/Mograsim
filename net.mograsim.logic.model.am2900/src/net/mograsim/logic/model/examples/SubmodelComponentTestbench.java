package net.mograsim.logic.model.examples;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class SubmodelComponentTestbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(SubmodelComponentTestbench::createTestbench);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createTestbench(ViewModelModifiable model)
	{
		GUIComponent comp = IndirectGUIComponentCreator.createComponent(model, "GUIAm2901", "Am2901");

		// guess which pins are outputs and which are inputs
		// TODO this code exists three times... but it seems too "hacky" to put it in a helper class
		// TODO sort pins correctly - use Y coordinate
		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		for (Pin p : comp.getPins().values())
			if (p.getRelX() == 0)
				inputPinNames.add(p.name);
			else
				outputPinNames.add(p.name);

		comp.moveTo(100, 0);
		for (int i = 0; i < inputPinNames.size(); i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPin(inputPinNames.get(i)), sw.getOutputPin());
		}
		for (int i = 0; i < outputPinNames.size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, comp.getPin(outputPinNames.get(i)), bd.getInputPin());
		}
	}
}