package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModelModifiable;

public class GUIOrGate extends SimpleRectangularGUIGate
{
	public GUIOrGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "\u22651", false);// ">=1"
		setInputCount(2);
	}
}