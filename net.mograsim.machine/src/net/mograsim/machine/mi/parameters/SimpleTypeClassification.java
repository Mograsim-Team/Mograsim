package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class SimpleTypeClassification implements ParameterClassification
{
	private ParameterType expectedType;
	
	public SimpleTypeClassification(ParameterType expectedType)
	{
		this.expectedType = expectedType;
	}

	@Override
	public boolean conforms(MicroInstructionParameter param)
	{
		return expectedType.equals(param.getType());
	}

	@Override
	public ParameterType getExpectedType()
	{
		return expectedType;
	}
}
