package net.mograsim.plugin.tables.mi;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import net.mograsim.machine.Machine;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryParseException;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.MachineContext.ContextObserver;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.plugin.tables.RadixSelector;
import net.mograsim.plugin.util.DropDownMenu;
import net.mograsim.plugin.util.DropDownMenu.DropDownEntry;

public class InstructionView extends ViewPart implements ContextObserver
{
	private String saveLoc = null;
	private LazyTableViewer viewer;
	private TableViewerColumn[] columns = new TableViewerColumn[0];
	private MicroInstructionDefinition miDef;
	private MicroInstructionMemory memory;
	private DisplaySettings displaySettings;
	private InstructionTableContentProvider provider;

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent)
	{
		provider = new InstructionTableContentProvider();
		GridLayout layout = new GridLayout(3, false);
		setupMenuButtons(parent);

		displaySettings = new DisplaySettings();
		new RadixSelector(parent, displaySettings);

		parent.setLayout(layout);
		viewer = new LazyTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		getSite().setSelectionProvider(viewer);

		GridData viewerData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewerData.horizontalSpan = 3;
		viewer.getTable().setLayoutData(viewerData);

		displaySettings.addObserver(() -> viewer.refreshLazy());
		MachineContext.getInstance().registerObserver(this);
	}

	@SuppressWarnings("unused")
	private void setupMenuButtons(Composite parent)
	{
		DropDownEntry open = new DropDownEntry("Open", (e) ->
		{
			FileDialog d = new FileDialog(parent.getShell(), SWT.NONE);
			d.open();
			String filename = d.getFileName();
			if (!filename.equals(""))
				open(d.getFilterPath() + File.separator + filename);
		});

		DropDownEntry save = new DropDownEntry("Save", (e) ->
		{
			if (saveLoc == null)
				openSaveAsDialog(parent);
			save(saveLoc);
		});
		DropDownEntry saveAs = new DropDownEntry("SaveAs", (e) ->
		{
			openSaveAsDialog(parent);
			save(saveLoc);
		});
		new DropDownMenu(parent, "File", open, save, saveAs);
	}

	private void openSaveAsDialog(Composite parent)
	{
		FileDialog d = new FileDialog(parent.getShell(), SWT.SAVE);
		d.open();
		String filename = d.getFileName();
		if (!filename.equals(""))
			saveLoc = d.getFilterPath() + File.separator + filename;
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		deleteColumns();
		this.memory = memory;
		viewer.setInput(memory);
		this.miDef = memory.getDefinition().getMicroInstructionDefinition();
		createColumns();
	}

	private void deleteColumns()
	{
		for (TableViewerColumn col : columns)
			col.getColumn().dispose();
	}

	private void createColumns()
	{
		TableViewerColumn col = createTableViewerColumn("Address", 200);
		col.setLabelProvider(new AddressLabelProvider());

		int size = miDef.size();
		int bit = 0;
		columns = new TableViewerColumn[size];
		ParameterClassification[] classes = miDef.getParameterClassifications();

		for (int i = 0; i < size; i++)
		{
			int startBit = bit;
			int endBit = (bit = bit + classes[i].getExpectedBits()) - 1;
			String name = startBit == endBit ? Integer.toString(startBit) : startBit + "..." + endBit;
			int bounds = 20 + 20 * classes[i].getExpectedBits();

			createEditingAndLabel(createTableViewerColumn(name, bounds), miDef, i);
		}

	}

	private void createEditingAndLabel(TableViewerColumn col, MicroInstructionDefinition miDef, int index)
	{
		ParameterClassification parameterClassification = miDef.getParameterClassifications()[index];
		EditingSupport support;
		ColumnLabelProvider provider;
		switch (parameterClassification.getExpectedType())
		{
		case BOOLEAN_IMMEDIATE:
			support = new BooleanEditingSupport(viewer, miDef, index);
			provider = new ParameterLabelProvider(index);
			break;
		case INTEGER_IMMEDIATE:
			support = new IntegerEditingSupport(viewer, miDef, index, displaySettings, this.provider);
			provider = new IntegerColumnLabelProvider(displaySettings, index);
			break;
		case MNEMONIC:
			support = new MnemonicEditingSupport(viewer, miDef, index, this.provider);
			provider = new ParameterLabelProvider(index);
			break;
		default:
			throw new IllegalStateException(
					"Unable to create EditingSupport for unknown ParameterType " + parameterClassification.getExpectedType());
		}
		col.setEditingSupport(support);
		col.setLabelProvider(provider);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
	}

	private void open(String file)
	{
		if (miDef == null)
		{
			System.err.println("Failed to parse MicroprogrammingMemory from File. No MicroInstructionDefinition assigned.");
			return;
		}
		try
		{
			MicroInstructionMemoryParser.parseMemory(memory, file);
			viewer.refresh();
			saveLoc = file;
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
		}
		if (saveLoc != null)
		{
			try
			{
				MicroInstructionMemoryParser.write(memory, file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	@Override
	public void setMachine(Optional<Machine> machine)
	{
		if (machine.isPresent())
		{
			Machine actualMachine = machine.get();
			bindMicroInstructionMemory(actualMachine.getMicroInstructionMemory());
		}
	}
}
