package net.mograsim.logic.ui.am2900;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.atomic.GUIAndGate;
import net.mograsim.logic.ui.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.atomic.GUINotGate;
import net.mograsim.logic.ui.model.components.atomic.TextComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIdff;
import net.mograsim.logic.ui.model.components.mi.nandbased.am2901.GUIAm2901;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.util.ModellingTool;

public class Am2901Testbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(Am2901Testbench::createTestbench);
	}

	public static void createTestbench(ViewModelModifiable model)
	{
		SimpleRectangularSubmodelComponent comp = new GUIAm2901(model);
		ModellingTool tool = ModellingTool.createFor(model);

		comp.moveTo(240, 0);

		GUIManualSwitch enable = new GUIManualSwitch(model);
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

		for (int i = 0; i < comp.getInputPinNames().size(); i++)
		{
			double x = 55 + 70 * (i % 2);
			double y = 10 * i;

			WireCrossPoint wcp = new WireCrossPoint(model, 1);
			GUIComponent d_ff = new GUIdff(model);
			GUIManualSwitch sw = new GUIManualSwitch(model);

			tool.connect(last, wcp);
			tool.connect(wcp, d_ff, "C");
			tool.connect(sw, d_ff, "", "D");
			tool.connect(d_ff, comp, "Q", comp.getInputPinNames().get(i));
			last = wcp.getPin();

			TextComponent label = new TextComponent(model, comp.getInputPinNames().get(i));

			sw.moveTo(x, y + 7.5);
			wcp.moveTo(160, y);
			d_ff.moveTo(170, y);
			label.moveTo(x - 25, y + 15);
		}

		for (int i = 0; i < comp.getOutputPinNames().size(); i++)
		{
			double x = 300 + 75 * (i % 2);
			double y = 10 * i - 2.5;
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(x, y);
			tool.connect(bd.getInputPin(), comp, comp.getOutputPinNames().get(i));

			TextComponent label = new TextComponent(model, comp.getOutputPinNames().get(i));
			label.moveTo(x + 50, y + 8);
		}
	}
}