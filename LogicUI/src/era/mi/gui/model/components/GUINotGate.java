package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;

public class GUINotGate extends SimpleRectangularGUIGate
{
	public GUINotGate(ViewModel model, int logicWidth)
	{
		super(model, logicWidth, "1", true);
		setInputCount(1);
	}
}