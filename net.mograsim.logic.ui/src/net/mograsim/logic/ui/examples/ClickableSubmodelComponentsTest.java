package net.mograsim.logic.ui.examples;

import net.mograsim.logic.ui.SimpleLogicUIStandalone;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;

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

				GUIManualSwitch sw = new GUIManualSwitch(submodelModifiable);
				GUIBitDisplay bd = new GUIBitDisplay(submodelModifiable);

				sw.moveTo(10, 5);
				bd.moveTo(50, 5);

				new GUIWire(submodelModifiable, sw.getOutputPin(), bd.getInputPin());

			}
		};
		comp.moveTo(10, 10);
	}
}