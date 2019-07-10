package net.mograsim.logic.model.examples;

import java.io.IOException;

import com.google.gson.JsonNull;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.components.mi.nandbased.GUI_rsLatch;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.serializing.SubmodelComponentDeserializer;
import net.mograsim.logic.model.serializing.SubmodelComponentParams;
import net.mograsim.logic.model.util.JsonHandler;

public class JsonExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(JsonExample::basicTest);
	}

	public static void mappingTest(ViewModelModifiable model)
	{
		IndirectGUIComponentCreator.createComponent(model, "Am2901", JsonNull.INSTANCE, "Am2901 instance");
	}

	private static class TestComponent extends SimpleRectangularSubmodelComponent
	{
		protected TestComponent(ViewModelModifiable model, String name)
		{
			super(model, 1, "Test", name);
			setSubmodelScale(.4);
			setInputPins("Input pin #0");
			SubmodelComponentDeserializer.create(submodelModifiable, "HalfAdder.json", "halfadder");
		}
	}

	@SuppressWarnings("unused") // GUIWires being created
	private static void basicTest(ViewModelModifiable viewModel)
	{
		GUI_rsLatch comp = new GUI_rsLatch(viewModel, "Original RS latch");
		comp.moveTo(30, 0);
		SubmodelComponentParams params = comp.calculateParams();
		String jsonString = JsonHandler.toJson(params);
		System.out.println(jsonString);
		SubmodelComponentParams paramsD = JsonHandler.fromJson(jsonString, SubmodelComponentParams.class);
		SubmodelComponent componentD = SubmodelComponentDeserializer.create(viewModel, paramsD, "Deserialized RS latch");
		componentD.moveTo(30, 50);
		double h = 0;
		for (String s : comp.getInputPinNames())
		{
			GUIManualSwitch sw = new GUIManualSwitch(viewModel);
			sw.moveTo(0, h);
			new GUIWire(viewModel, sw.getOutputPin(), comp.getPin(s));
			sw = new GUIManualSwitch(viewModel);
			sw.moveTo(0, h + 50);
			new GUIWire(viewModel, sw.getOutputPin(), componentD.getPin(s));
			h += 20;
		}
		h = 0;
		for (String s : comp.getOutputPinNames())
		{
			GUIBitDisplay bd = new GUIBitDisplay(viewModel);
			bd.moveTo(80, h);
			new GUIWire(viewModel, bd.getInputPin(), comp.getPin(s));
			bd = new GUIBitDisplay(viewModel);
			bd.moveTo(80, h + 50);
			new GUIWire(viewModel, bd.getInputPin(), componentD.getPin(s));
			h += 20;
		}
	}

	// Execute only after HalfAdder.json has been created
	public static void refJsonFromJsonTest(ViewModelModifiable model)
	{
		TestComponent t = new TestComponent(model, "Original component");
		t.calculateParams().writeJson("Test.json");
		SubmodelComponent c = SubmodelComponentDeserializer.create(model, "Test.json", "Deserialized component");
		c.moveTo(0, 50);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createFromJsonExample(ViewModelModifiable model)
	{
		SimpleRectangularSubmodelComponent tmp = new GUIfulladder(model, "Original full adder");
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
				"FullAdder.json", "Deserialized full adder");

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

		SubmodelComponent adder2 = SubmodelComponentDeserializer.create(model, pC, "Full adder created from params instance");

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