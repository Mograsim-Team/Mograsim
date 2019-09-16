package net.mograsim.logic.model.examples;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;

public class ClickableSubmodelComponentsTest
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(ClickableSubmodelComponentsTest::createExample);
	}

	public static void createExample(LogicModelModifiable model)
	{
		@SuppressWarnings("unused") // Wire
		SimpleRectangularSubmodelComponent comp = new SimpleRectangularSubmodelComponent(model, 1, "")
		{
			{
				setSubmodelScale(.4);
				setOutputPins("O0");

				ModelManualSwitch sw = new ModelManualSwitch(submodelModifiable, 1);
				ModelBitDisplay bd = new ModelBitDisplay(submodelModifiable, 1);

				sw.moveTo(10, 5);
				bd.moveTo(50, 5);

				new ModelWire(submodelModifiable, sw.getOutputPin(), bd.getInputPin());

			}
		};
		comp.moveTo(10, 10);
	}
}