package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;

public class GUIAndGate extends SimpleRectangularGUIGate
{
	public GUIAndGate(ViewModel model, int logicWidth)
	{
		super(model, logicWidth, "&", false);
		setInputCount(2);// TODO make variable
	}
}