package net.mograsim.logic.model.examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelComponentTestbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(ModelComponentTestbench::createTestbench);
	}

	@SuppressWarnings("unused") // for ModelWires being created
	public static void createTestbench(LogicModelModifiable model)
	{
		Am2900Loader.setup();
//		ModelComponent comp = new StrictAm2900MachineDefinition().createNew(model).getAm2900();
		ModelComponent comp = IndirectModelComponentCreator.createComponent(model, "ram5_12");

		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		for (Pin p : comp.getPins().values())
			if (p.usage == PinUsage.INPUT)
				inputPinNames.add(p.name);
			else
				// TODO handle TRISTATE pins
				outputPinNames.add(p.name);

		inputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));
		outputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));

		comp.moveTo(100, 0);
		for (int i = 0; i < inputPinNames.size(); i++)
		{
			String pinName = inputPinNames.get(i);
			ModelManualSwitch sw = new ModelManualSwitch(model, comp.getPin(pinName).logicWidth, pinName);
			sw.moveTo(0, 20 * i);
			new ModelTextComponent(model, pinName).moveTo(20, 20 * i);
			new ModelWire(model, comp.getPin(pinName), sw.getOutputPin());
		}
		for (int i = 0; i < outputPinNames.size(); i++)
		{
			String pinName = outputPinNames.get(i);
			ModelBitDisplay bd = new ModelBitDisplay(model, comp.getPin(pinName).logicWidth, pinName);
			bd.moveTo(200, 20 * i);
			new ModelTextComponent(model, pinName).moveTo(220, 20 * i);
			new ModelWire(model, comp.getPin(pinName), bd.getInputPin());
		}
	}
}