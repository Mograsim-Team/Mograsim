package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemory.ActiveMicroInstructionChangedListener;

public class ActiveInstructionPreviewContentProvider implements InstructionTableContentProvider, ActiveMicroInstructionChangedListener
{
	private TableViewer viewer;
	private MicroInstructionMemory memory;
	private InstructionTableRow activeRow;

	public ActiveInstructionPreviewContentProvider(TableViewer viewer)
	{
		this.viewer = viewer;
		viewer.setItemCount(1);
	}

	@Override
	public void activeMicroInstructionChanged(long address)
	{
		this.activeRow = new InstructionTableRow(address, memory);
		viewer.refresh();
	}

	@Override
	public void update(long address)
	{
		// Nothing to do here
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (oldInput != null)
			((MicroInstructionMemory) oldInput).deregisterActiveMicroInstructionChangedListener(this);

		memory = (MicroInstructionMemory) newInput;
		if (memory != null)
		{
			activeMicroInstructionChanged(0);
			memory.registerActiveMicroInstructionChangedListener(this);
		}
	}

	@Override
	public void updateElement(int index)
	{
		viewer.replace(activeRow, index);
	}
}
