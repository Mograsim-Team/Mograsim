package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModelModifiable;

public class GUINandGate extends SimpleRectangularGUIGate
{
	public GUINandGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "&", true);
		setInputCount(2);// TODO make variable
	}
}