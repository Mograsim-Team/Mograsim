package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CyclingCellEditor extends CellEditor
{
	private final int size;
	private int index = -1;

	public CyclingCellEditor(int size)
	{
		super();
		this.size = size;
	}

	public CyclingCellEditor(Composite parent, int size)
	{
		super(parent);
		this.size = size;
	}

	public CyclingCellEditor(Composite parent, int size, int style)
	{
		super(parent, style);
		this.size = size;
	}

	@Override
	protected Control createControl(Composite parent)
	{
		// Nothing to return
		return null;
	}

	@Override
	protected Object doGetValue()
	{
		return index;
	}

	@Override
	protected void doSetFocus()
	{
		// Nothing to focus
	}

	@Override
	protected void doSetValue(Object index)
	{
		this.index = (Integer) index;
	}

	@Override
	public void activate()
	{
		index = (index + 1) % size;
		fireApplyEditorValue();
	}
}
