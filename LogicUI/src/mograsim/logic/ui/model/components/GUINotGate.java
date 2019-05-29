package mograsim.logic.ui.model.components;

import mograsim.logic.ui.model.ViewModel;

public class GUINotGate extends SimpleRectangularGUIGate
{
	public GUINotGate(ViewModel model, int logicWidth)
	{
		super(model, logicWidth, "1", true);
		setInputCount(1);
	}
}