package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;

public class GUIAndGate extends RectangularShapedGUIGate
{
	public GUIAndGate(ViewModel model)
	{
		super(model, "&", false);
		setInputCount(2);
	}
}