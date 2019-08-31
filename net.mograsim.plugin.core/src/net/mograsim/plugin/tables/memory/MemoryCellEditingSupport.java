package net.mograsim.plugin.tables.memory;

import java.math.BigInteger;

import org.eclipse.jface.viewers.TableViewer;

import net.mograsim.plugin.tables.NumberCellEditingSupport;

public class MemoryCellEditingSupport extends NumberCellEditingSupport
{
	public MemoryCellEditingSupport(TableViewer viewer, DisplaySettings displaySettings)
	{
		super(viewer, displaySettings);
	}

	@Override
	protected void setAsBigInteger(Object element, BigInteger value)
	{
		MemoryTableRow row = (MemoryTableRow) element;
		row.getMemory().setCellAsBigInteger(row.address, value);
	}

	@Override
	protected BigInteger getAsBigInteger(Object element)
	{
		MemoryTableRow row = (MemoryTableRow) element;
		return row.getMemory().getCellAsBigInteger(row.address);
	}
}