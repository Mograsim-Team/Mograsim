package net.mograsim.logic.model.examples;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;

public class ClickableSubmodelComponentsTest
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(ClickableSubmodelComponentsTest::createExample);
	}

	public static void createExample(ViewModelModifiable model)
	{
		@SuppressWarnings("unused") // GUIWire
		SimpleRectangularSubmodelComponent comp = new SimpleRectangularSubmodelComponent(model, 1, "")
		{
			{
				setSubmodelScale(.4);
				setOutputPins("O0");

				GUIManualSwitch sw = new GUIManualSwitch(submodelModifiable, 1);
				GUIBitDisplay bd = new GUIBitDisplay(submodelModifiable, 1);

				sw.moveTo(10, 5);
				bd.moveTo(50, 5);

				new GUIWire(submodelModifiable, sw.getOutputPin(), bd.getInputPin());

			}
		};
		comp.moveTo(10, 10);
	}
}