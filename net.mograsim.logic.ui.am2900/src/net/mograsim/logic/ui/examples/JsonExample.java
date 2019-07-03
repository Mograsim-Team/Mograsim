package net.mograsim.logic.ui.examples;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUI_rsLatch;
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
		SimpleLogicUIStandalone.executeVisualisation(JsonExample::basicTest);
	}

	public static void mappingTest(ViewModelModifiable model)
	{
		IndirectGUIComponentCreator.createComponent(model, "Am2901", JsonNull.INSTANCE);
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

	@SuppressWarnings("unused") // GUIWires being created
	private static void basicTest(ViewModelModifiable viewModel)
	{
		GUI_rsLatch comp = new GUI_rsLatch(viewModel);
		comp.moveTo(30, 0);
		SubmodelComponentParams params = comp.calculateParams();
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(params);
		System.out.println(jsonString);
		SubmodelComponent deserialized = SubmodelComponentDeserializer.create(viewModel,
				new Gson().fromJson(jsonString, SubmodelComponentParams.class));
		deserialized.moveTo(30, 50);
		double h = 0;
		for (String s : comp.getInputPinNames())
		{
			GUIManualSwitch sw = new GUIManualSwitch(viewModel);
			sw.moveTo(0, h);
			new GUIWire(viewModel, sw.getOutputPin(), comp.getPin(s));
			sw = new GUIManualSwitch(viewModel);
			sw.moveTo(0, h + 50);
			new GUIWire(viewModel, sw.getOutputPin(), deserialized.getPin(s));
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
			new GUIWire(viewModel, bd.getInputPin(), deserialized.getPin(s));
			h += 20;
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