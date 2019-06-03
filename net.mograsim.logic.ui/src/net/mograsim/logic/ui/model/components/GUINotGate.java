package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModelModifiable;

public class GUINotGate extends SimpleRectangularGUIGate
{
	public GUINotGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "1", true);
		setInputCount(1);
	}
}