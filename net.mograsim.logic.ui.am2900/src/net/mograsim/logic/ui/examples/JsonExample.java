package net.mograsim.logic.ui.examples;

import java.io.IOException;
import java.util.HashMap;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIhalfadder;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.ui.serializing.SubmodelComponentDeserializer;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams;

public class JsonExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(JsonExample::mappingTest);
	}

	public static void mappingTest(ViewModelModifiable model)
	{
		IndirectGUIComponentCreator.create(model, "GUIAm2901", new HashMap<String, Object>());
	}

	private static class TestComponent extends SimpleRectangularSubmodelComponent
	{
		protected TestComponent(ViewModelModifiable model)
		{
			super(model, 1, "Test");
			setSubmodelScale(.4);
			setInputPins("Input pin #0");
			SubmodelComponentDeserializer.create(submodelModifiable, "HalfAdder.json");
		}
	}

	// Execute only after HalfAdder.json has been created
	public static void refJsonFromJsonTest(ViewModelModifiable model)
	{
		TestComponent t = new TestComponent(model);
		t.calculateParams().writeJson("Test.json");
		SubmodelComponent c = SubmodelComponentDeserializer.create(model, "Test.json");
		c.moveTo(0, 50);
	}

	public static void createHalfAdderExample(ViewModelModifiable model)
	{
		GUIhalfadder tmp = new GUIhalfadder(model);
		tmp.moveTo(1000, 50);
		SubmodelComponentParams p = tmp.calculateParams();
		try
		{
			p.writeJson("HalfAdder.json");
			p = SubmodelComponentParams.readJson("HalfAdder.json");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		SubmodelComponentDeserializer.create(model, p);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createFromJsonExample(ViewModelModifiable model)
	{
		SimpleRectangularSubmodelComponent tmp = new GUIfulladder(model);
		SubmodelComponentParams pC = tmp.calculateParams();
		tmp.moveTo(1000, 100);
		try
		{
			pC.writeJson("FullAdder.json");
			pC = SubmodelComponentParams.readJson("FullAdder.json");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		SimpleRectangularSubmodelComponent adder = (SimpleRectangularSubmodelComponent) SubmodelComponentDeserializer.create(model,
				"FullAdder.json");

		GUIManualSwitch swA = new GUIManualSwitch(model);
		swA.moveTo(0, 0);
		GUIManualSwitch swB = new GUIManualSwitch(model);
		swB.moveTo(0, 25);
		GUIManualSwitch swC = new GUIManualSwitch(model);
		swC.moveTo(0, 50);

		adder.moveTo(30, 10);
		GUIBitDisplay bdY = new GUIBitDisplay(model);
		bdY.moveTo(90, 12.5);
		GUIBitDisplay bdZ = new GUIBitDisplay(model);
		bdZ.moveTo(90, 30);

		new GUIWire(model, swA.getOutputPin(), adder.getPin("A"));
		new GUIWire(model, swB.getOutputPin(), adder.getPin("B"));
		new GUIWire(model, swC.getOutputPin(), adder.getPin("C"));

		new GUIWire(model, adder.getPin("Y"), bdY.getInputPin());
		new GUIWire(model, adder.getPin("Z"), bdZ.getInputPin());

		SubmodelComponent adder2 = SubmodelComponentDeserializer.create(model, pC);

		swA = new GUIManualSwitch(model);
		swA.moveTo(0, 70);
		swB = new GUIManualSwitch(model);
		swB.moveTo(0, 85);
		swC = new GUIManualSwitch(model);
		swC.moveTo(0, 100);

		adder2.moveTo(30, 80);
		bdY = new GUIBitDisplay(model);
		bdY.moveTo(90, 70);
		bdZ = new GUIBitDisplay(model);
		bdZ.moveTo(90, 85);

		new GUIWire(model, swA.getOutputPin(), adder2.getPin("A"));
		new GUIWire(model, swB.getOutputPin(), adder2.getPin("B"));
		new GUIWire(model, swC.getOutputPin(), adder2.getPin("C"));

		new GUIWire(model, adder2.getPin("Y"), bdY.getInputPin());
		new GUIWire(model, adder2.getPin("Z"), bdZ.getInputPin());
	}
}