package net.mograsim.plugin.editors;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.MicroInstructionMemoryParseException;
import net.mograsim.machine.standard.memory.MainMemoryParser;
import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.ProjectMachineContext;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.plugin.tables.NumberColumnLabelProvider;
import net.mograsim.plugin.tables.RadixSelector;
import net.mograsim.plugin.tables.memory.MemoryCellEditingSupport;
import net.mograsim.plugin.tables.memory.MemoryTableContentProvider;
import net.mograsim.plugin.tables.memory.MemoryTableRow;
import net.mograsim.plugin.tables.memory.NumberVerifyListener;

public class MemoryEditor extends EditorPart
{
	private MachineContext context;

	private MainMemory memory;

	private LazyTableViewer viewer;
	private MemoryTableContentProvider provider;
	private DisplaySettings displaySettings;

	private boolean dirty;

	private final MemoryCellModifiedListener memListener;

	private final static String font = "net.mograsim.plugin.memory.table_font";
	private IPropertyChangeListener fontChangeListener;

	public MemoryEditor()
	{
		memListener = this::cellModified;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		provider = new MemoryTableContentProvider();
		displaySettings = new DisplaySettings();

		parent.setLayout(new GridLayout(7, false));

		createHeader(parent);
		createViewer(parent);

		displaySettings.addObserver(() -> viewer.refresh());
	}

	@SuppressWarnings("unused") // RadixSelector and exceptions
	private void createHeader(Composite parent)
	{
		Label fromLabel = new Label(parent, SWT.NONE);
		fromLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		fromLabel.setText("Address: ");

		Text fromText = new Text(parent, SWT.BORDER);
		fromText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		NumberVerifyListener vl = new NumberVerifyListener();
		fromText.addVerifyListener(vl);
		fromText.setText("0");
		fromText.addModifyListener(e ->
		{
			try
			{
				provider.setLowerBound(AsmNumberUtil.valueOf(fromText.getText()).longValue());
				viewer.refresh();
			}
			catch (NumberFormatException x)
			{
				// Nothing to do here
			}
		});
		Label amountLabel = new Label(parent, SWT.NONE);
		amountLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		amountLabel.setText("Number of cells: ");
		Text amountText = new Text(parent, SWT.BORDER);
		amountText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		amountText.addVerifyListener(vl);
		amountText.setText("0");
		amountText.addModifyListener(e ->
		{
			try
			{
				provider.setAmount(AsmNumberUtil.valueOf(amountText.getText()).intValue());
				viewer.refresh();
			}
			catch (NumberFormatException x)
			{
				// Nothing to do here
			}
		});
		new RadixSelector(parent, displaySettings);

		addActivationButton(parent);
	}

	private void addActivationButton(Composite parent)
	{
		Button activationButton = new Button(parent, SWT.PUSH);
		activationButton.setText("Set Active");
		activationButton.addListener(SWT.Selection, e -> context.getActiveMachine().ifPresent(m -> m.getMainMemory().bind(memory)));
	}

	private void createViewer(Composite parent)
	{
		viewer = new LazyTableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		createColumns();
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		getSite().setSelectionProvider(viewer);// TODO what does this do?
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(fontChangeListener = (e) ->
		{
			if (IThemeManager.CHANGE_CURRENT_THEME.equals(e.getProperty()) || font.equals(e.getProperty()))
			{
				updateFont(themeManager.getCurrentTheme());
				viewer.refresh();
			}
		});
		updateFont(themeManager.getCurrentTheme());

		if (memory != null)
			viewer.setInput(memory);
	}

	private void updateFont(ITheme theme)
	{
		viewer.getTable().setFont(theme.getFontRegistry().get(font));
	}

	private void createColumns()
	{
		TableViewerColumn addrCol = createTableViewerColumn("Address", 100);
		addrCol.setLabelProvider(new AddressLabelProvider());

		TableViewerColumn dataCol = createTableViewerColumn("Data", 100);
		dataCol.setLabelProvider(new NumberColumnLabelProvider(displaySettings)
		{
			@Override
			public BigInteger getAsBigInteger(Object element)
			{
				MemoryTableRow row = (MemoryTableRow) element;
				return row.getMemory().getCellAsBigInteger(row.address);
			}

			@Override
			public int getBitLength(Object element)
			{
				return ((MemoryTableRow) element).getMemory().getDefinition().getCellWidth();
			}
		});
		dataCol.setEditingSupport(new MemoryCellEditingSupport(viewer, displaySettings));
	}

	private TableViewerColumn createTableViewerColumn(String title, int width)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(false);
		return viewerColumn;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			context = ProjectMachineContext.getMachineContextOf(fileInput.getFile().getProject());
			context.activateMachine();

			setPartName(fileInput.getName());
			try
			{
				open(fileInput.getFile());
			}
			catch (Exception e)
			{
				throw new PartInitException("Failed to read input!", e);
			}
		} else
			throw new IllegalArgumentException("MemoryEditor can only be used with Files");

		setSite(site);
		setInput(input);
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput)
			SafeRunnable.getRunner().run(() -> save(((IFileEditorInput) input).getFile(), monitor));
	}

	private void save(IFile file, IProgressMonitor monitor) throws CoreException, IOException
	{
		if (memory == null)
		{
			throw new MicroInstructionMemoryParseException("Failed to write MainMemory to File. No MainMemory assigned.");
		}
		try (InputStream toWrite = MainMemoryParser.write(memory))
		{
			file.setContents(toWrite, 0, monitor);
			setDirty(false);
		}
	}

	private void open(IFile file) throws IOException, CoreException
	{
		MainMemoryDefinition memDef = context.getMachineDefinition()
				.orElseThrow(() -> new MicroInstructionMemoryParseException("No MachineDefinition assigned!")).getMainMemoryDefinition();
		memory = MainMemoryParser.parseMemory(memDef, file.getContents());
		memory.registerCellModifiedListener(memListener);
		if (viewer != null)
			viewer.setInput(memory);
	}

	private void cellModified(@SuppressWarnings("unused") long address)
	{
		setDirty(true);
	}

	private void setDirty(boolean newDirty)
	{
		dirty = newDirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void setFocus()
	{
		viewer.getTable().setFocus();
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getWorkbench().getThemeManager().removePropertyChangeListener(fontChangeListener);
		if (memory != null)
			memory.deregisterCellModifiedListener(memListener);
		super.dispose();
	}
}