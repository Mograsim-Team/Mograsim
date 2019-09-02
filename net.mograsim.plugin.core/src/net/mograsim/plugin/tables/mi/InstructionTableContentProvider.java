package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import net.mograsim.machine.mi.MicroprogramMemory;

public class InstructionTableContentProvider implements ILazyContentProvider
{
	private TableViewer viewer;
	private MicroprogramMemory memory;

	@Override
	public void updateElement(int index)
	{
		viewer.replace(memory.getCell(index), index);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = (TableViewer) viewer;
		this.memory = (MicroprogramMemory) newInput;
		if (this.memory != null)
			this.viewer.setItemCount((int) memory.size());
	}
}
