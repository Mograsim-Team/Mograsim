package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreOrGate;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelOrGate extends SimpleRectangularModelGate
{
	public ModelOrGate(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelOrGate(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, "OrGate", "\u22651", false, logicWidth, name);// ">=1"
		setInputCount(2);
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new SimpleGateAdapter<>(ModelOrGate.class, CoreOrGate::new));
		IndirectModelComponentCreator.setComponentSupplier(ModelOrGate.class.getCanonicalName(),
				(m, p, n) -> new ModelOrGate(m, p.getAsInt(), n));
	}
}