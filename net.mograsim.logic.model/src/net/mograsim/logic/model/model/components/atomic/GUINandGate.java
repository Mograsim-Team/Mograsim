package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.NandGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUINandGate extends SimpleRectangularGUIGate
{
	public GUINandGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUINandGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "GUINandGate", "&", true, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUINandGate.class, NandGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUINandGate.class.getCanonicalName(),
				(m, p, n) -> new GUINandGate(m, p.getAsInt(), n));
	}
}