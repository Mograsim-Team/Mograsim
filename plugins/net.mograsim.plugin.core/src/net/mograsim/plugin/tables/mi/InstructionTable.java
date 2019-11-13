package net.mograsim.plugin.tables.mi;

import java.util.Arrays;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.themes.IThemeManager;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.preferences.Preferences;

public class InstructionTable
{
	protected final DisplaySettings displaySettings;
	protected final LazyTableViewer viewer;
	private TableViewerColumn[] columns = new TableViewerColumn[0];
	private MicroInstructionDefinition miDef;
	private MicroInstructionMemory memory;
	private InstructionTableContentProvider provider;
	private final RowHighlighter highlighter;
	private final FontAndColorHelper cProv;

	private final boolean isEditable;

	public InstructionTable(Composite parent, DisplaySettings displaySettings, IThemeManager themeManager)
	{
		this(parent, displaySettings, themeManager, true);
	}

	public InstructionTable(Composite parent, DisplaySettings displaySettings, IThemeManager themeManager, boolean allowEditing)
	{
		viewer = new LazyTableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		this.displaySettings = displaySettings;
		this.cProv = new FontAndColorHelper(viewer, themeManager);
		this.highlighter = new RowHighlighter(viewer, cProv);
		this.isEditable = allowEditing;

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		table.addDisposeListener(e -> dispose());

		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer)
		{
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event)
			{
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		int features = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION;
		TableViewerEditor.create(viewer, focusCellManager, actSupport, features);

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
		viewer.getTable().setVisible(false);

		int size = miDef.size();
		columns = new TableViewerColumn[size + 1];

		TableViewerColumn col = createTableViewerColumn("Address", null);
		columns[0] = col;
		col.setLabelProvider(new AddressLabelProvider()
		{
			@Override
			public Color getBackground(Object element)
			{
				return cProv.getBackground(element, -1);
			}

			@Override
			public Color getForeground(Object element)
			{
				return cProv.getForeground(element, -1);
			}

			@Override
			public Font getFont(Object element)
			{
				return cProv.getFont(element, -1);
			}
		});

		String[] columnTitles = new String[size];

		int bit = miDef.sizeInBits();
		ParameterClassification[] classes = miDef.getParameterClassifications();

		for (int i = 0; i < size; i++)
		{
			int startBit = bit - 1;
			int endBit = bit = bit - classes[i].getExpectedBits();

			String description = miDef.getParameterDescription(i).orElse(null);
			String bitString = startBit == endBit ? Integer.toString(startBit) : startBit + "..." + endBit;
			String columnTitle, columnTooltip;
			if (useDescriptionAsColumnTitle(description))
			{
				columnTitle = description;
				columnTooltip = bitString;
			} else
			{
				columnTitle = bitString;
				columnTooltip = description;
			}
			columnTitles[i] = columnTitle;

			col = createTableViewerColumn(columnTitle, columnTooltip);
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

		viewer.getTable().setVisible(true);
	}

	private static boolean useDescriptionAsColumnTitle(String description)
	{
		return description != null && Preferences.current().getBoolean("net.mograsim.plugin.core.editors.mpm.descriptionascolumnname");
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		this.memory = memory;
		if (memory != null)
		{
			this.miDef = memory.getDefinition().getMicroInstructionDefinition();
			setViewerInput(memory);
		}
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
			support = isEditable ? new BooleanEditingSupport(viewer, miDef, index) : null;
			provider = new ParameterLabelProvider(cProv, index);
			break;
		case INTEGER_IMMEDIATE:
			support = isEditable ? new IntegerEditingSupport(viewer, miDef, index, displaySettings, this.provider) : null;
			provider = new IntegerColumnLabelProvider(displaySettings, cProv, index);
			break;
		case MNEMONIC:
			support = isEditable ? new MnemonicEditingSupport(viewer, miDef, index, this.provider) : null;
			provider = new ParameterLabelProvider(cProv, index);
			break;
		default:
			throw new IllegalStateException(
					"Unable to create EditingSupport for unknown ParameterType " + parameterClassification.getExpectedType());
		}
		if (isEditable)
			col.setEditingSupport(support);
		col.setLabelProvider(provider);
	}

	private TableViewerColumn createTableViewerColumn(String title, String toolTip)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setToolTipText(toolTip);
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

	private void dispose()
	{
		cProv.dispose();
		viewer.getTable().dispose();
	}

	public void highlight(int row)
	{
		highlighter.highlight(row);
	}
}
