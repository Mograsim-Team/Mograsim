package net.mograsim.logic.ui.examples;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.serializing.SubmodelComponentDeserializer;

public class SubmodelComponentTestbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(SubmodelComponentTestbench::createTestbench);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createTestbench(ViewModelModifiable model)
	{
		SubmodelComponent comp = SubmodelComponentDeserializer.create(model, "components/am2901/GUIAm2901.json");

		// guess which pins are outputs and which are inputs
		// TODO this code exists three times... but it seems too "hacky" to put it in a helper class
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