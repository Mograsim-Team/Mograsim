package net.mograsim.plugin.tables;

import java.math.BigInteger;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import net.mograsim.plugin.asm.AsmNumberUtil;

public abstract class NumberCellEditingSupport extends EditingSupport
{
	private final TableViewer viewer;
	private final CellEditor editor;
	private final DisplaySettings displaySettings;

	public NumberCellEditingSupport(TableViewer viewer, DisplaySettings displaySettings)
	{
		super(viewer);
		this.viewer = viewer;
		this.displaySettings = displaySettings;
		editor = new TextCellEditor(viewer.getTable());
		editor.setValidator(new NumberCellEditorValidator());
	}

	@Override
	final protected boolean canEdit(Object element)
	{
		return true;
	}

	@Override
	final protected CellEditor getCellEditor(Object element)
	{
		return editor;
	}

	@Override
	final protected Object getValue(Object element)
	{
		return AsmNumberUtil.toString(getAsBigInteger(element), displaySettings.getDataNumberType());
	}

	@Override
	final protected void setValue(Object element, Object userInput)
	{
		try
		{
			setAsBigInteger(element, AsmNumberUtil.valueOf((String) userInput));
		}
		catch (@SuppressWarnings("unused") NumberFormatException e)
		{
			setAsBigInteger(element, BigInteger.valueOf(0));
		}
		viewer.update(element, null);
	}

	protected abstract void setAsBigInteger(Object element, BigInteger value);

	protected abstract BigInteger getAsBigInteger(Object element);
}
