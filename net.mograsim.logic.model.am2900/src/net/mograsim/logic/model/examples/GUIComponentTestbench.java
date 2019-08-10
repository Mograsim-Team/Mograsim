package net.mograsim.logic.model.examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.am2900.components.GUImux4_12;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent.Usage;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;

public class GUIComponentTestbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(GUIComponentTestbench::createTestbench);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createTestbench(ViewModelModifiable model)
	{
//		GUIComponent comp = IndirectGUIComponentCreator.createComponent(model, "GUIAm2901", "Am2901");
		GUIComponent comp = new GUImux4_12(model, "c");

		// guess which pins are outputs and which are inputs
		// TODO this code exists four times... but it seems too "hacky" to put it in a helper class
		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		if (comp instanceof SimpleRectangularHardcodedGUIComponent)
		{
			SimpleRectangularHardcodedGUIComponent compCasted = (SimpleRectangularHardcodedGUIComponent) comp;
			for (Pin p : comp.getPins().values())
				if (compCasted.getPinUsage(p) == Usage.INPUT)
					inputPinNames.add(p.name);
				else
					outputPinNames.add(p.name);
		} else
			for (Pin p : comp.getPins().values())
				if (p.getRelX() == 0)
					inputPinNames.add(p.name);
				else
					outputPinNames.add(p.name);

		inputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));
		outputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));

		comp.moveTo(100, 0);
		for (int i = 0; i < inputPinNames.size(); i++)
		{
			String pinName = inputPinNames.get(i);
			GUIManualSwitch sw = new GUIManualSwitch(model, comp.getPin(pinName).logicWidth);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPin(pinName), sw.getOutputPin());
		}
		for (int i = 0; i < outputPinNames.size(); i++)
		{
			String pinName = outputPinNames.get(i);
			GUIBitDisplay bd = new GUIBitDisplay(model, comp.getPin(pinName).logicWidth);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, comp.getPin(pinName), bd.getInputPin());
		}
	}
}