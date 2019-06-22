package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.core.components.gates.OrGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;

public class GUIOrGate extends SimpleRectangularGUIGate
{
	public GUIOrGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "\u22651", false);// ">=1"
		setInputCount(2);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUIOrGate.class, OrGate::new));
	}
}