package net.mograsim.logic.model.am2900;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIAndGate;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.components.atomic.GUINotGate;
import net.mograsim.logic.model.model.components.atomic.TextComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.util.ModellingTool;

public class Am2901Testbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(Am2901Testbench::createTestbench);
	}

	public static void createTestbench(ViewModelModifiable model)
	{
		GUIComponent comp = IndirectGUIComponentCreator.createComponent(model, "GUIAm2901");
		ModellingTool tool = ModellingTool.createFor(model);

		comp.moveTo(240, 0);

		GUIManualSwitch enable = new GUIManualSwitch(model, 1);
		WireCrossPoint wcp0 = new WireCrossPoint(model, 1);
		GUINotGate not1 = new GUINotGate(model, 1);
		GUINotGate not2 = new GUINotGate(model, 1);
		GUINotGate not3 = new GUINotGate(model, 1);
		GUIAndGate and = new GUIAndGate(model, 1);
		tool.connect(wcp0, enable, "");
		tool.connect(wcp0, and, "A");
		tool.connect(wcp0, not1, "A");
		tool.connect(not1, not2, "Y", "A");
		tool.connect(not2, not3, "Y", "A");
		tool.connect(not3, and, "Y", "B");
		enable.moveTo(20, -32.5);
		wcp0.moveTo(35, -26);
		not1.moveTo(50, -20);
		not2.moveTo(80, -20);
		not3.moveTo(110, -20);
		and.moveTo(135, -30);
		Pin last = and.getPin("Y");

		// guess which pins are outputs and which are inputs
		// TODO this code exists four times... but it seems too "hacky" to put it in a helper class
		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		for (Pin p : comp.getPins().values())
			if (p.getRelX() == 0)
				inputPinNames.add(p.name);
			else
				outputPinNames.add(p.name);

		inputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));
		outputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));

		for (int i = 0; i < inputPinNames.size(); i++)
		{
			double x = 55 + 70 * (i % 2);
			double y = 10 * i;

			WireCrossPoint wcp = new WireCrossPoint(model, 1);
			GUIComponent d_ff = IndirectGUIComponentCreator.createComponent(model, "GUIdff");
			GUIManualSwitch sw = new GUIManualSwitch(model, 1);

			tool.connect(last, wcp);
			tool.connect(wcp, d_ff, "C");
			tool.connect(sw, d_ff, "", "D");
			tool.connect(d_ff, comp, "Q", inputPinNames.get(i));
			last = wcp.getPin();

			TextComponent label = new TextComponent(model, inputPinNames.get(i));

			sw.moveTo(x, y + 7.5);
			wcp.moveTo(160, y);
			d_ff.moveTo(170, y);
			label.moveTo(x - 48, y + 8);
		}

		for (int i = 0; i < outputPinNames.size(); i++)
		{
			double x = 300 + 75 * (i % 2);
			double y = 10 * i - 2.5;
			GUIBitDisplay bd = new GUIBitDisplay(model, 1);
			bd.moveTo(x, y);
			tool.connect(bd.getInputPin(), comp, outputPinNames.get(i));

			TextComponent label = new TextComponent(model, outputPinNames.get(i));
			label.moveTo(x + 25, y);
		}
	}
}