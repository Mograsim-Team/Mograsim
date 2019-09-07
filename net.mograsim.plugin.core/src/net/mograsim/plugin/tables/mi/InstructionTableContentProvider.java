package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import net.mograsim.machine.mi.MicroInstructionMemory;

public class InstructionTableContentProvider implements ILazyContentProvider
{
	private TableViewer viewer;
	private MicroInstructionMemory memory;

	@Override
	public void updateElement(int index)
	{
		long address = memory.getDefinition().getMinimalAddress() + index;
		viewer.replace(new InstructionTableRow(address, memory.getCell(address)), index);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = (TableViewer) viewer;
		this.memory = (MicroInstructionMemory) newInput;
		if (this.memory != null)
			this.viewer.setItemCount((int) memory.size());
	}
}
