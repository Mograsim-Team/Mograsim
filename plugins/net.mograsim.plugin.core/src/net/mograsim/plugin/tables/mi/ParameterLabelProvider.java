package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class ParameterLabelProvider extends ColumnLabelProvider
{
	private final int index;

	public ParameterLabelProvider(int index)
	{
		super();
		this.index = index;
	}

	@Override
	public String getText(Object element)
	{
		return ((InstructionTableRow) element).data.getParameter(index).toString();
	}
}
