package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ICellEditorValidator;

import net.mograsim.machine.mi.parameters.MnemonicFamily;

public class MnemonicCellEditorValidator implements ICellEditorValidator
{
	private MnemonicFamily family;

	public MnemonicCellEditorValidator(MnemonicFamily family)
	{
		this.family = family;
	}

	@Override
	public String isValid(Object value)
	{
		int index = (Integer) value;
		return index >= 0 && index < family.size() ? null
				: String.format("MnemonicFamily has %s elements, index %s is out of bounds", family.size(), value.toString());
	}

}
