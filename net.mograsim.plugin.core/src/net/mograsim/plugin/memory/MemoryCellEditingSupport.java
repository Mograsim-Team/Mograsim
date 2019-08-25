package net.mograsim.plugin.memory;

import java.math.BigInteger;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.memory.MemoryView.DisplaySettings;

public class MemoryCellEditingSupport extends EditingSupport
{
	private final TableViewer viewer;
	private final CellEditor editor;
	private final DisplaySettings displaySettings;

	public MemoryCellEditingSupport(TableViewer viewer, DisplaySettings displaySettings)
	{
		super(viewer);
		this.viewer = viewer;
		this.displaySettings = displaySettings;
		editor = new TextCellEditor(viewer.getTable());
		editor.setValidator(new NumberCellEditorValidator());
	}

	@Override
	protected boolean canEdit(Object element)
	{
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		return editor;
	}

	@Override
	protected Object getValue(Object element)
	{
		MemoryTableRow row = (MemoryTableRow) element;
		return AsmNumberUtil.toString(row.getMemory().getCellAsBigInteger(row.address), displaySettings.getDataNumberType());
	}

	@Override
	protected void setValue(Object element, Object userInput)
	{
		MemoryTableRow row = (MemoryTableRow) element;
		try
		{
			row.getMemory().setCellAsBigInteger(row.address, AsmNumberUtil.valueOf((String) userInput));
		}
		catch (@SuppressWarnings("unused") NumberFormatException e)
		{
			row.getMemory().setCellAsBigInteger(row.address, BigInteger.valueOf(0));
		}
		viewer.update(element, null);
	}
}