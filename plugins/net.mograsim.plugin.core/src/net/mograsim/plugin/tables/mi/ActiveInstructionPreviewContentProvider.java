package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import net.mograsim.machine.Machine;
import net.mograsim.machine.Machine.ActiveMicroInstructionChangedListener;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.plugin.util.SingleSWTRequest;

public class ActiveInstructionPreviewContentProvider implements InstructionTableContentProvider
{
	private TableViewer viewer;
	private MicroInstructionMemory memory;
	private InstructionTableRow activeRow;
	private Machine machine;
	private SingleSWTRequest requester = new SingleSWTRequest();

	private ActiveMicroInstructionChangedListener instChanged = (oldAddress, newAddress) ->
	{
		activeRow = new InstructionTableRow(Long.max(0, newAddress), memory);
		requester.request(() -> updateElement(0));
	};

	public ActiveInstructionPreviewContentProvider(TableViewer viewer)
	{
		this.viewer = viewer;
		viewer.setItemCount(1);
	}

	@Override
	public void update(long address)
	{
		// Nothing to do here
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		memory = (MicroInstructionMemory) newInput;
	}

	public void setMachine(Machine newMachine)
	{
		if (machine != null)
			machine.removeActiveMicroInstructionChangedListener(instChanged);

		machine = newMachine;
		if (machine != null)
		{
			instChanged.instructionChanged(-1, 0);
			machine.addActiveMicroInstructionChangedListener(instChanged);
		}
	}

	@Override
	public void updateElement(int index)
	{
		if (activeRow != null && !viewer.getControl().isDisposed())
			viewer.replace(activeRow, index);
	}
}
