package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900MPROMDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.mi.components.ModelMPROM;

public class ModelAm2900MPROM extends ModelMPROM
{
	public ModelAm2900MPROM(LogicModelModifiable model, String name)
	{
		super(model, Am2900MPROMDefinition.instance, name);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MPROM";
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MPROM.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MPROM(m, n);
		});
	}
}