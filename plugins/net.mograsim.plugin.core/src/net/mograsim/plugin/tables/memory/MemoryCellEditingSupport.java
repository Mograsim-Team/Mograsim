package net.mograsim.plugin.tables.memory;

import java.math.BigInteger;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;

import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberCellEditingSupport;

public class MemoryCellEditingSupport extends NumberCellEditingSupport
{
	public MemoryCellEditingSupport(TableViewer viewer, DisplaySettings displaySettings)
	{
		// TODO maybe allow X here too?
		super(viewer, displaySettings, false);
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

	@Override
	public int getBitLength(Object element)
	{
		return ((MemoryTableRow) element).getMemory().getDefinition().getCellWidth();
	}

	public Control getCellEditorControl()
	{
		return editor.getControl();
	}
}