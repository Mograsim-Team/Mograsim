package net.mograsim.plugin.tables;

import java.math.BigInteger;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class AddressLabelProvider extends ColumnLabelProvider
{
	@Override
	public String getText(Object element)
	{
		@SuppressWarnings("rawtypes")
		TableRow row = (TableRow) element;
		return AsmNumberUtil.toString(BigInteger.valueOf(row.address), NumberType.HEXADECIMAL);
	}
}
