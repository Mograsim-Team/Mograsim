package net.mograsim.logic.ui.model.components.atomic;

import net.mograsim.logic.core.components.gates.NandGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;

public class GUINandGate extends SimpleRectangularGUIGate
{
	public GUINandGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "&", true);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUINandGate.class, NandGate::new));
	}
}