package net.mograsim.logic.ui.examples;

import java.io.IOException;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.GUICustomComponentCreator;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIhalfadder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUImux1;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUImux1_4;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIsel2_4;
import net.mograsim.logic.ui.model.components.params.RectComponentParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class JsonExample
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(JsonExample::createFromJsonExample);
	}

	private static class TestComponent extends SimpleRectangularSubmodelComponent
	{
		protected TestComponent(ViewModelModifiable model)
		{
			super(model, 1, "Test");
			setInputCount(1);
			setSubmodelScale(.4);
			GUICustomComponentCreator.create(submodelModifiable, "HalfAdder.rc");
		}
	}

	// Execute only after HalfAdder.rc has been created
	public static void refJsonFromJsonTest(ViewModelModifiable model)
	{
		TestComponent t = new TestComponent(model);
		t.calculateParams().writeJson("Test.sc");
		SubmodelComponent c = GUICustomComponentCreator.create(model, "Test.sc");
		c.moveTo(0, 50);

	}

	public static void createHalfAdderExample(ViewModelModifiable model)
	{
		GUIhalfadder tmp = new GUIhalfadder(model);
		tmp.moveTo(1000, 50);
		RectComponentParams p = tmp.calculateRectParams();
		SubComponentParams pC = tmp.calculateParams();
		try
		{
			p.writeJson("HalfAdder.rc");
			pC.writeJson("HalfAdder.sc");
			p = RectComponentParams.readJson("HalfAdder.rc");
			pC = SubComponentParams.readJson("HalfAdder.sc");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		SubmodelComponent adder = GUICustomComponentCreator.create(model, p, "");
		adder = GUICustomComponentCreator.create(model, pC, "");
		adder.moveTo(0, 200);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createFromJsonExample(ViewModelModifiable model)
	{
		SimpleRectangularSubmodelComponent tmp = new GUIhalfadder(model);
		tmp.moveTo(1000, 50);
		RectComponentParams p = tmp.calculateRectParams();
		try
		{
			p.writeJson("HalfAdder.rc");
			p = RectComponentParams.readJson("HalfAdder.rc");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		tmp = new GUIfulladder(model);
		SubComponentParams pC = tmp.calculateParams();
		tmp.moveTo(1000, 100);
		try
		{
			pC.writeJson("FullAdder.sc");
			pC = SubComponentParams.readJson("FullAdder.sc");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		SimpleRectangularSubmodelComponent adder = new GUIfulladder(model);

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