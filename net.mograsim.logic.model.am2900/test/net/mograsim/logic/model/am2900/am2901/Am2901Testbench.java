package net.mograsim.logic.model.am2900.am2901;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.SimpleLogicUIStandalone.VisualisationObjects;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelAndGate;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.atomic.ModelNotGate;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.ModellingTool;

@Deprecated
public class Am2901Testbench
{
	public static void main(String[] args)
	{
		Am2900Loader.setup();
		SimpleLogicUIStandalone.executeVisualisation(Am2901Testbench::createTestbench, Am2901Testbench::beforeRun);
	}

	public static void createTestbench(LogicModelModifiable model)
	{
		ModelComponent comp = IndirectModelComponentCreator.createComponent(model, "Am2901");
		ModellingTool tool = ModellingTool.createFor(model);

		comp.moveTo(240, 0);

		ModelManualSwitch enable = new ModelManualSwitch(model, 1);
		ModelWireCrossPoint wcp0 = new ModelWireCrossPoint(model, 1);
		ModelNotGate not1 = new ModelNotGate(model, 1);
		ModelNotGate not2 = new ModelNotGate(model, 1);
		ModelNotGate not3 = new ModelNotGate(model, 1);
		ModelAndGate and = new ModelAndGate(model, 1);
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

		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		for (Pin p : comp.getPins().values())
			if (p.usage == PinUsage.INPUT)
				inputPinNames.add(p.name);
			else
				outputPinNames.add(p.name);

		inputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));
		outputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));

		for (int i = 0; i < inputPinNames.size(); i++)
		{
			double x = 55 + 70 * (i % 2);
			double y = 10 * i;

			ModelWireCrossPoint wcp = new ModelWireCrossPoint(model, 1);
			ModelComponent d_ff = IndirectModelComponentCreator.createComponent(model, "dff");
			ModelManualSwitch sw = new ModelManualSwitch(model, 1);

			tool.connect(last, wcp);
			tool.connect(wcp, d_ff, "C");
			tool.connect(sw, d_ff, "", "D");
			tool.connect(d_ff, comp, "Q", inputPinNames.get(i));
			last = wcp.getPin();

			ModelTextComponent label = new ModelTextComponent(model, inputPinNames.get(i));

			sw.moveTo(x, y + 7.5);
			wcp.moveTo(160, y);
			d_ff.moveTo(170, y);
			label.moveTo(x - 48, y + 8);
		}

		for (int i = 0; i < outputPinNames.size(); i++)
		{
			double x = 300 + 75 * (i % 2);
			double y = 10 * i - 2.5;
			ModelBitDisplay bd = new ModelBitDisplay(model, 1);
			bd.moveTo(x, y);
			tool.connect(bd.getInputPin(), comp, outputPinNames.get(i));

			ModelTextComponent label = new ModelTextComponent(model, outputPinNames.get(i));
			label.moveTo(x + 25, y);
		}
	}

	public static void beforeRun(VisualisationObjects vis)
	{
		vis.model.getComponentsByName().values().forEach(c ->
		{
			if (c instanceof ModelManualSwitch)
			{
				ModelManualSwitch cCasted = (ModelManualSwitch) c;
				cCasted.setHighLevelState("out", BitVector.of(Bit.ZERO, cCasted.logicWidth));
			}
		});
	}
}