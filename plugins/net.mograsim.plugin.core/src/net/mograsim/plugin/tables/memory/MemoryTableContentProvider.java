package net.mograsim.plugin.tables.memory;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;

public class MemoryTableContentProvider implements ILazyContentProvider, MemoryCellModifiedListener
{
	private long lower, upper;
	private TableViewer viewer;
	public final static int MAX_VISIBLE_ROWS = 1_000;
	private BitVectorMemory memory;

	public void setBounds(long lower, long upper)
	{
		if (memory != null)
		{
			BitVectorMemoryDefinition definition = memory.getDefinition();
			this.lower = Long.min(definition.getMaximalAddress(), Long.max(definition.getMinimalAddress(), lower));
			this.upper = Long.max(this.lower, Long.min(definition.getMaximalAddress(), upper));
		} else
		{
			this.lower = lower;
			this.upper = Long.max(this.lower, upper);
		}
		updateItemCount();
	}

	public void updateItemCount()
	{
		if (viewer != null)
			viewer.setItemCount(getAmount());
	}

	public long getLowerBound()
	{
		return lower;
	}

	public long getUpperBound()
	{
		return upper;
	}

	public int getAmount()
	{
		return (int) (upper - lower + 1);
	}

	@Override
	public void updateElement(int index)
	{
		if (index < getAmount())
		{
			long address = lower + index;
			if (address <= memory.getDefinition().getMaximalAddress())
				viewer.replace(new MemoryTableRow(address, memory), index);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = (TableViewer) viewer;
		this.memory = (BitVectorMemory) newInput;
		if (oldInput != null)
			((BitVectorMemory) oldInput).deregisterCellModifiedListener(this);
		if (memory != null)
			memory.registerCellModifiedListener(this);
		setBounds(0, MAX_VISIBLE_ROWS);
	}

	@Override
	public void update(long address)
	{
		// TODO check if viewer.refresh() does what we expect
		Display.getDefault().asyncExec(address == -1 ? viewer::refresh : () -> updateElement((int) (address - lower)));
	}
}
