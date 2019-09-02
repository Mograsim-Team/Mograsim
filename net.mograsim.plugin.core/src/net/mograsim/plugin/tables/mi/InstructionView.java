package net.mograsim.plugin.tables.mi;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroprogramMemory;
import net.mograsim.machine.mi.MicroprogramMemoryParseException;
import net.mograsim.machine.mi.MicroprogramMemoryParser;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;
import net.mograsim.plugin.tables.memory.DisplaySettings;
import net.mograsim.plugin.util.DropDownMenu;
import net.mograsim.plugin.util.DropDownMenu.DropDownEntry;

public class InstructionView extends ViewPart
{
	private String saveLoc = null;
	private TableViewer viewer;
	private TableViewerColumn[] columns = new TableViewerColumn[0];
	private MicroInstructionDefinition miDef;
	private MicroprogramMemory memory;

	@Override
	public void createPartControl(Composite parent)
	{
		InstructionTableContentProvider provider = new InstructionTableContentProvider();
		GridLayout layout = new GridLayout(1, false);
		setupMenuButtons(parent);
		parent.setLayout(layout);
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);

		viewer.getTable().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
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

	public void bindMicroprogramMemory(MicroprogramMemory memory)
	{
		this.memory = memory;
		viewer.setInput(memory);
	}

	public void bindMicroInstructionDef(MicroInstructionDefinition miDef)
	{
		this.miDef = miDef;
		createColumns();
	}

	private void createColumns()
	{
		for (TableViewerColumn col : columns)
			col.getColumn().dispose();
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

			TableViewerColumn col = createTableViewerColumn(name, bounds);
			col.setLabelProvider(new ParameterLabelProvider(i));
			col.setEditingSupport(createEditingSupport(miDef, i));
		}

	}

	private EditingSupport createEditingSupport(MicroInstructionDefinition miDef, int index)
	{
		ParameterClassification parameterClassification = miDef.getParameterClassifications()[index];
		switch (parameterClassification.getExpectedType())
		{
		case BOOLEAN_IMMEDIATE:
			return new BooleanEditingSupport(viewer, index);
		case INTEGER_IMMEDIATE:
			return new IntegerEditingSupport(viewer, miDef, index, new DisplaySettings(NumberType.DECIMAL));
		case MNEMONIC:
			return new MnemonicEditingSupport(viewer, miDef, index);
		default:
			throw new IllegalStateException(
					"Unable to create EditingSupport for unknown ParameterType " + parameterClassification.getExpectedType());
		}
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
			MicroprogramMemory newMemory = MicroprogramMemoryParser.parseMemory(miDef, file);
			bindMicroprogramMemory(newMemory);
			saveLoc = file;
		}
		catch (IOException | MicroprogramMemoryParseException e)
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
				MicroprogramMemoryParser.write(memory, file);
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
}
