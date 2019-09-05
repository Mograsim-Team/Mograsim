package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreOrGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelOrGate extends SimpleRectangularModelGate
{
	public ModelOrGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelOrGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "OrGate", "\u22651", false, logicWidth, name);// ">=1"
		setInputCount(2);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(ModelOrGate.class, CoreOrGate::new));
		IndirectModelComponentCreator.setComponentSupplier(ModelOrGate.class.getCanonicalName(),
				(m, p, n) -> new ModelOrGate(m, p.getAsInt(), n));
	}
}