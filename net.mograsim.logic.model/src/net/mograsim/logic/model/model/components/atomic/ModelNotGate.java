package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreNotGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelNotGate extends SimpleRectangularModelGate
{
	public ModelNotGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelNotGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "NotGate", "1", true, logicWidth, name);
		setInputCount(1);
	}

	static
	{
		ViewLogicModelAdapter
				.addComponentAdapter(new SimpleGateAdapter<>(ModelNotGate.class, (t, p, o, i) -> new CoreNotGate(t, p, i[0], o)));
		IndirectModelComponentCreator.setComponentSupplier(ModelNotGate.class.getCanonicalName(),
				(m, p, n) -> new ModelNotGate(m, p.getAsInt(), n));
	}
}