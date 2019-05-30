package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModel;

public class GUIOrGate extends SimpleRectangularGUIGate
{
	public GUIOrGate(ViewModel model, int logicWidth)
	{
		super(model, logicWidth, "\u22651", false);// ">=1"
		setInputCount(2);
	}
}