package net.mograsim.plugin.tables.mi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemory.ActiveMicroInstructionChangedListener;
import net.mograsim.machine.mi.MicroInstructionMemoryParseException;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.plugin.tables.RadixSelector;

public class InstructionView extends EditorPart implements MemoryCellModifiedListener, ActiveMicroInstructionChangedListener
{
	private InstructionTableContentProvider provider;
	private int highlighted = 0;
	private boolean dirty = false;
	private String machineName;
	private MicroInstructionMemory memory;
	private InstructionTable table;

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent)
	{
		provider = new InstructionTableLazyContentProvider();
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		DisplaySettings displaySettings = new DisplaySettings();
		new RadixSelector(parent, displaySettings);

		addActivationButton(parent);

		table = new InstructionTable(parent, displaySettings);
		table.setContentProvider(provider);
		table.bindMicroInstructionMemory(memory);

		GridData viewerData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewerData.horizontalSpan = 3;
		table.getTableViewer().getTable().setLayoutData(viewerData);
	}

	public void highlight(int index)
	{
		Display.getDefault().asyncExec(() ->
		{
			LazyTableViewer viewer = table.getTableViewer();
			viewer.highlightRow(highlighted, false);
			highlighted = index;
			viewer.highlightRow(index, true);
			viewer.getTable().showItem(viewer.getTable().getItem(Math.min((int) memory.getDefinition().getMaximalAddress(), index + 2)));
			viewer.getTable().showItem(viewer.getTable().getItem(index));
		});
	}

	private void addActivationButton(Composite parent)
	{
		Button activationButton = new Button(parent, SWT.PUSH);
		activationButton.setText("Set Active");
		activationButton.addListener(SWT.Selection,
				e -> MachineContext.getInstance().getMachine().getMicroInstructionMemory().bind(memory));
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		this.memory = memory;
		this.memory.registerCellModifiedListener(this);
		this.memory.registerActiveMicroInstructionChangedListener(this);
		if (table != null)
			table.bindMicroInstructionMemory(memory);
	}

	private void open(String file)
	{
		try (BufferedReader bf = new BufferedReader(new FileReader(file)))
		{
			machineName = bf.readLine();
			bindMicroInstructionMemory(MicroInstructionMemoryParser.parseMemory(machineName, bf));
		}
		catch (IOException | MicroInstructionMemoryParseException e)
		{
			e.printStackTrace();
		}
	}

	private void save(String file)
	{
		if (memory == null)
		{
			System.err.println("Failed to write MicroprogrammingMemory to File. No MicroprogrammingMemory assigned.");
			return;
		}
		try
		{
			MicroInstructionMemoryParser.write(memory, machineName, file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus()
	{
		table.getTableViewer().getControl().setFocus();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		IEditorInput input = getEditorInput();
		if (input instanceof IPathEditorInput)
		{
			IPathEditorInput pathInput = (IPathEditorInput) input;
			save(pathInput.getPath().toOSString());
			setDirty(false);
		}
	}

	@Override
	public void doSaveAs()
	{
		openSaveAsDialog();
	}

	private void openSaveAsDialog()
	{
		FileDialog d = new FileDialog(table.getTableViewer().getTable().getShell(), SWT.SAVE);
		d.open();
		String filename = d.getFileName();
		if (!filename.equals(""))
		{
			save(d.getFilterPath() + File.separator + filename);
			setDirty(false);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		if (input instanceof IPathEditorInput)
		{
			IPathEditorInput pathInput = (IPathEditorInput) input;
			setPartName(pathInput.getName());
			open(pathInput.getPath().toOSString());
		}
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	@Override
	public void update(long address)
	{
		setDirty(true);
		table.refresh();
	}

	private void setDirty(boolean value)
	{
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void activeMicroInstructionChanged(long address)
	{
		highlight((int) (address - memory.getDefinition().getMinimalAddress()));
	}
}
