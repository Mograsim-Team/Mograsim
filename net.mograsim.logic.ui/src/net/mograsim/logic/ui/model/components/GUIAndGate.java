package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModelModifiable;

public class GUIAndGate extends SimpleRectangularGUIGate
{
	public GUIAndGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "&", false);
		setInputCount(2);// TODO make variable
	}
}