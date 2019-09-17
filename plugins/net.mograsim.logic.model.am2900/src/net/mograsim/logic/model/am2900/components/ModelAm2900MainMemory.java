package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900MainMemoryDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.standard.memory.ModelWordAddressableMemory;

public class ModelAm2900MainMemory extends ModelWordAddressableMemory
{
	public ModelAm2900MainMemory(LogicModelModifiable model, String name)
	{
		super(model, Am2900MainMemoryDefinition.instance, name);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MainMemory";
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MainMemory.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MainMemory(m, n);
		});
	}
}