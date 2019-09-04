package net.mograsim.plugin.tables;

import java.math.BigInteger;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import net.mograsim.plugin.asm.AsmNumberUtil;

public abstract class NumberColumnLabelProvider extends ColumnLabelProvider
{
	private DisplaySettings displaySettings;

	public NumberColumnLabelProvider(DisplaySettings displaySettings)
	{
		this.displaySettings = displaySettings;
	}

	@Override
	public String getText(Object element)
	{
		return AsmNumberUtil.toString(getAsBigInteger(element), displaySettings.getDataNumberType());
	}

	public abstract BigInteger getAsBigInteger(Object element);
}