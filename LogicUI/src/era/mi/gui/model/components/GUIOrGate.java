package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;

public class GUIOrGate extends RectangularShapedGUIGate
{
	public GUIOrGate(ViewModel model)
	{
		super(model, "\u22651", false);// ">=1"
		setInputCount(2);
	}
}