package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;

public class GUINotGate extends RectangularShapedGUIGate
{
	public GUINotGate(ViewModel model)
	{
		super(model, "1", true);
		setInputCount(1);
	}
}