package net.mograsim.plugin.tables.mi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import net.mograsim.machine.MemoryObserver;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryParseException;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.plugin.tables.RadixSelector;

public class InstructionView extends EditorPart implements MemoryObserver
{
	private LazyTableViewer viewer;
	private TableViewerColumn[] columns = new TableViewerColumn[0];
	private MicroInstructionDefinition miDef;
	private MicroInstructionMemory memory;
	private DisplaySettings displaySettings;
	private InstructionTableContentProvider provider;
	private int highlighted = 0;
	private boolean dirty = false;
	private String machineName;

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent)
	{
		provider = new InstructionTableContentProvider();
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		displaySettings = new DisplaySettings();
		new RadixSelector(parent, displaySettings);

		addActivationButton(parent);
		viewer = new LazyTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		setViewerInput(memory);
		getSite().setSelectionProvider(viewer);

		GridData viewerData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewerData.horizontalSpan = 3;
		viewer.getTable().setLayoutData(viewerData);

		displaySettings.addObserver(() -> viewer.refresh());
	}

	private void addActivationButton(Composite parent)
	{
		Button activationButton = new Button(parent, SWT.PUSH);
		activationButton.setText("Set Active");
		activationButton.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.PUSH)
					MachineContext.getInstance().getMachine().getMicroInstructionMemory().bind(memory);
				// TODO register this in project context
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
	}

	public void highlight(int index)
	{
		viewer.highlightRow(highlighted, false);
		viewer.highlightRow(index, true);
		viewer.getTable().setTopIndex(index);
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		this.memory = memory;
		this.miDef = memory.getDefinition().getMicroInstructionDefinition();
		this.memory.registerObserver(this);
		setViewerInput(memory);
	}

	private void setViewerInput(MicroInstructionMemory memory)
	{
		if (viewer != null)
		{
			deleteColumns();
			viewer.setInput(memory);
			createColumns();
		}
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

		TableViewerColumn col = createTableViewerColumn("Address", generateLongestHexStrings(12));
		columns[0] = col;
		col.setLabelProvider(new AddressLabelProvider());

		int bit = miDef.sizeInBits();
		ParameterClassification[] classes = miDef.getParameterClassifications();

		for (int i = 0; i < size; i++)
		{
			int startBit = bit - 1;
			int endBit = bit = bit - classes[i].getExpectedBits();
			String name = startBit == endBit ? Integer.toString(startBit) : startBit + "..." + endBit;

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

			col = createTableViewerColumn(name, longestPossibleContents);
			columns[i + 1] = col;
			createEditingAndLabel(col, miDef, i);
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
		col.getColumn().setToolTipText(miDef.getParameterDescription(index).orElse(""));
	}

	private TableViewerColumn createTableViewerColumn(String title, String... longestPossibleContents)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
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
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
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
		viewer.getControl().setFocus();
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
		FileDialog d = new FileDialog(viewer.getTable().getShell(), SWT.SAVE);
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
	}

	private void setDirty(boolean value)
	{
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}
}
