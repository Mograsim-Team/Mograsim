package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900Machine;
import net.mograsim.logic.model.am2900.machine.Am2900MachineDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.standard.memory.ModelWordAddressableMemory;

public class ModelAm2900MainMemory extends ModelWordAddressableMemory<Am2900Machine>
{
	public ModelAm2900MainMemory(LogicModelModifiable model, String name)
	{
		super(model, Am2900MachineDefinition.getInstance().getMainMemoryDefinition(), name);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MainMemory";
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new ModelAm2900MainMemoryAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MainMemory.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MainMemory(m, n);
		});
	}
}