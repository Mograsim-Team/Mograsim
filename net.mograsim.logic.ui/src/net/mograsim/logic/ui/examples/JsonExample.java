package net.mograsim.logic.ui.examples;

import java.io.IOException;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUICustomComponentCreator;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIhalfadder;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class JsonExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(JsonExample::refJsonFromJsonTest);
	}

	private static class TestComponent extends SimpleRectangularSubmodelComponent
	{
		protected TestComponent(ViewModelModifiable model)
		{
			super(model, 1, "Test");
			setSubmodelScale(.4);
			setInputPins("Test input pin");
			GUICustomComponentCreator.create(submodelModifiable, "HalfAdder.json");
		}
	}

	// Execute only after HalfAdder.json has been created
	public static void refJsonFromJsonTest(ViewModelModifiable model)
	{
		TestComponent t = new TestComponent(model);
		t.calculateParams().writeJson("Test.json");
		SubmodelComponent c = GUICustomComponentCreator.create(model, "Test.json");
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

		GUICustomComponentCreator.create(model, p, "");
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

		SimpleRectangularSubmodelComponent adder = (SimpleRectangularSubmodelComponent) GUICustomComponentCreator.create(model,
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

		new GUIWire(model, swA.getOutputPin(), adder.getInputPins().get(0));
		new GUIWire(model, swB.getOutputPin(), adder.getInputPins().get(1));
		new GUIWire(model, swC.getOutputPin(), adder.getInputPins().get(2));

		new GUIWire(model, adder.getOutputPins().get(0), bdY.getInputPin());
		new GUIWire(model, adder.getOutputPins().get(1), bdZ.getInputPin());

		SubmodelComponent adder2 = GUICustomComponentCreator.create(model, pC, "");

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

		new GUIWire(model, swA.getOutputPin(), adder2.getPins().get(0));
		new GUIWire(model, swB.getOutputPin(), adder2.getPins().get(1));
		new GUIWire(model, swC.getOutputPin(), adder2.getPins().get(2));

		new GUIWire(model, adder2.getPins().get(3), bdY.getInputPin());
		new GUIWire(model, adder2.getPins().get(4), bdZ.getInputPin());
	}
}