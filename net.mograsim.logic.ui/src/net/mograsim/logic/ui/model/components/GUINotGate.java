package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;

public class GUINotGate extends SimpleRectangularGUIGate
{
	public GUINotGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "1", true);
		setInputCount(1);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUINotGate.class, (t, p, o, i) -> new NotGate(t, p, i[0], o)));
	}
}