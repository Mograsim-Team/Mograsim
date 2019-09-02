package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.parameters.BooleanImmediate;

public class BooleanEditingSupport extends EditingSupport
{
	private final CheckboxCellEditor editor;
	private final TableViewer viewer;
	private final int index;

	public BooleanEditingSupport(TableViewer viewer, int index)
	{
		super(viewer);
		this.viewer = viewer;
		editor = new CheckboxCellEditor(viewer.getTable());
		this.index = index;
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
		return ((BooleanImmediate) ((MicroInstruction) element).getParameter(index)).getBooleanValue();
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		((MicroInstruction) element).setParameter(index, new BooleanImmediate((Boolean) value));
		viewer.update(element, null);
	}

}
