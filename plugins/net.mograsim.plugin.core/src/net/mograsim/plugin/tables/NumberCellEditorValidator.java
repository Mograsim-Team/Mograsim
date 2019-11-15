package net.mograsim.plugin.tables;

import org.eclipse.jface.viewers.ICellEditorValidator;

import net.mograsim.plugin.asm.AsmNumberUtil;

public class NumberCellEditorValidator implements ICellEditorValidator
{
	private final boolean allowX;

	public NumberCellEditorValidator(boolean allowX)
	{
		this.allowX = allowX;
	}

	@Override
	public String isValid(Object value)
	{
		return !(allowX && value.equals("X")) && AsmNumberUtil.NumberType.NONE.equals(AsmNumberUtil.getType((String) value))
				? (String) value + " is not a valid number"
				: null;
	}
}