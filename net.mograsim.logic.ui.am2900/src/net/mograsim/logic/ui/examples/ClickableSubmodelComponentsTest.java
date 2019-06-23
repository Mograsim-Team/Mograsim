package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;

public class ClickableSubmodelComponentsTest
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(ClickableSubmodelComponentsTest::createExample);
	}

	public static void createExample(ViewModelModifiable model)
	{
		SimpleRectangularSubmodelComponent comp = new SimpleRectangularSubmodelComponent(model, 1, "")
		{
			{
				setSubmodelScale(.4);
				setOutputPins("O0");

				GUIManualSwitch sw = new GUIManualSwitch(submodelModifiable);
				GUIBitDisplay bd = new GUIBitDisplay(submodelModifiable);

				sw.moveTo(10, 5);
				bd.moveTo(50, 5);

			}
		};
		comp.moveTo(10, 10);
	}
}