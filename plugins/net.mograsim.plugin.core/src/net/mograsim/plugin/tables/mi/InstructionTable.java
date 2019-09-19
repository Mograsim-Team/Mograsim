package net.mograsim.plugin.tables.mi;

import java.util.Arrays;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;

public class InstructionTable
{
	protected DisplaySettings displaySettings;
	protected LazyTableViewer viewer;
	private TableViewerColumn[] columns = new TableViewerColumn[0];
	private MicroInstructionDefinition miDef;
	private MicroInstructionMemory memory;
	private InstructionTableContentProvider provider;

	public InstructionTable(Composite parent, DisplaySettings displaySettings)
	{
		viewer = new LazyTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		this.displaySettings = displaySettings;

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);

		GridData viewerData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewerData.horizontalSpan = 3;
		viewer.getTable().setLayoutData(viewerData);

		displaySettings.addObserver(() -> viewer.refresh());
	}

	private void deleteColumns()
	{
		for (TableViewerColumn col : columns)
			col.getColumn().dispose();
	}

	private void createColumns()
	{
		int size = miDef.size();
		columns = new TableViewerColumn[size + 1];

		TableViewerColumn col = createTableViewerColumn("Address");
		columns[0] = col;
		col.setLabelProvider(new AddressLabelProvider());

		String[] columnTitles = new String[size];

		int bit = miDef.sizeInBits();
		ParameterClassification[] classes = miDef.getParameterClassifications();

		for (int i = 0; i < size; i++)
		{
			int startBit = bit - 1;
			int endBit = bit = bit - classes[i].getExpectedBits();
			String columnTitle = calculateColumnTitle(startBit, endBit);
			columnTitles[i] = columnTitle;
			col = createTableViewerColumn(columnTitle);
			columns[i + 1] = col;
			createEditingAndLabel(col, miDef, i);
		}

		calculateOptimalColumnSize(0, "Address", generateLongestHexStrings(12));

		for (int i = 0; i < size; i++)
		{
			String[] longestPossibleContents;
			switch (classes[i].getExpectedType())
			{
			case INTEGER_IMMEDIATE:
				longestPossibleContents = generateLongestHexStrings(classes[i].getExpectedBits());
				break;
			case BOOLEAN_IMMEDIATE:
			case MNEMONIC:
				longestPossibleContents = ((MnemonicFamily) classes[i]).getStringValues();
				break;
			default:
				longestPossibleContents = new String[0];
				break;
			}
			calculateOptimalColumnSize(i + 1, columnTitles[i], longestPossibleContents);
		}
	}

	private static String calculateColumnTitle(int startBit, int endBit)
	{
		return startBit == endBit ? Integer.toString(startBit) : startBit + "..." + endBit;
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		this.memory = memory;
		this.miDef = memory.getDefinition().getMicroInstructionDefinition();
		setViewerInput(memory);
	}

	private static final String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	private static String[] generateLongestHexStrings(int bitWidth)
	{
		return Arrays.stream(HEX_DIGITS).map(s -> "0x" + s.repeat((bitWidth + 3) / 4)).toArray(String[]::new);
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
//			viewerColumn.setEditingSupport(editingSupport)
			support = new MnemonicEditingSupport(viewer, miDef, index, this.provider);
			provider = new ParameterLabelProvider(index);
			break;
		default:
			throw new IllegalStateException(
					"Unable to create EditingSupport for unknown ParameterType " + parameterClassification.getExpectedType());
		}
		col.setEditingSupport(support);
		col.setLabelProvider(provider);
		col.getColumn().setToolTipText(miDef.getParameterDescription(index).orElse(""));
	}

	private TableViewerColumn createTableViewerColumn(String title)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
	}

	private void calculateOptimalColumnSize(int i, String title, String... longestPossibleContents)
	{
		TableColumn column = viewer.getTable().getColumn(i);
		int maxWidth = 0;
		for (String s : longestPossibleContents)
		{
			column.setText(s);
			column.pack();
			if (column.getWidth() > maxWidth)
				maxWidth = column.getWidth();
		}
		column.setText(title);
		column.pack();
		if (column.getWidth() < maxWidth)
			column.setWidth(maxWidth);
	}

	public LazyTableViewer getTableViewer()
	{
		return viewer;
	}

	public MicroInstructionMemory getMicroInstructionMemory()
	{
		return memory;
	}

	public void setContentProvider(InstructionTableContentProvider provider)
	{
		this.provider = provider;
		viewer.setContentProvider(provider);
	}

	private void setViewerInput(MicroInstructionMemory memory)
	{
		deleteColumns();
		viewer.setInput(memory);
		createColumns();
	}

	public void refresh()
	{
		Display.getDefault().asyncExec(() -> viewer.refresh());
	}
}
