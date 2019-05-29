package mograsim.logic.ui.model.components;

import mograsim.logic.ui.model.ViewModel;

public class GUIAndGate extends SimpleRectangularGUIGate
{
	public GUIAndGate(ViewModel model, int logicWidth)
	{
		super(model, logicWidth, "&", false);
		setInputCount(2);// TODO make variable
	}
}