package net.mograsim.logic.model.verilog.converter;

import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;

public class ComponentConversionResult
{
	private final ModelComponentToVerilogComponentDeclarationMapping mapping;
	private final VerilogComponentImplementation implementation;

	public ComponentConversionResult(ModelComponentToVerilogComponentDeclarationMapping mapping,
			VerilogComponentImplementation implementation)
	{
		this.mapping = mapping;
		this.implementation = implementation;
	}

	public ModelComponentToVerilogComponentDeclarationMapping getMapping()
	{
		return mapping;
	}

	public VerilogComponentImplementation getImplementation()
	{
		return implementation;
	}
}
