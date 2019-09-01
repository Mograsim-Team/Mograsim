package net.mograsim.plugin.tables.memory;

import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class DisplaySettings
{
	private NumberType dataNumberType;

	public DisplaySettings(NumberType dataNumberType)
	{
		this.dataNumberType = dataNumberType;
	}

	public NumberType getDataNumberType()
	{
		return dataNumberType;
	}

	public void setDataNumberType(NumberType dataNumberType)
	{
		this.dataNumberType = dataNumberType;
	}
}