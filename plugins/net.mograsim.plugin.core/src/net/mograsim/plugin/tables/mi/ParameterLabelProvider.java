package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

public class ParameterLabelProvider extends ColumnLabelProvider
{
	private final int index;
	private final ColorProvider cProv;

	public ParameterLabelProvider(ColorProvider cProv, int index)
	{
		super();
		this.index = index;
		this.cProv = cProv;
	}

	@Override
	public String getText(Object element)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		return row.data.getCell(row.address).getParameter(index).toString();
	}

	@Override
	public Color getBackground(Object element)
	{
		return cProv.getBackground(element, index);
	}

	@Override
	public Color getForeground(Object element)
	{
		return cProv.getForeground(element, index);
	}
}
