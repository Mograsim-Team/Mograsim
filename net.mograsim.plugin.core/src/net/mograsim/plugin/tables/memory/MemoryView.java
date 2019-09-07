package net.mograsim.plugin.tables.memory;

import java.math.BigInteger;
import java.util.Optional;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.standard.memory.WordAddressableMemory;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.MachineContext.ContextObserver;
import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.tables.AddressLabelProvider;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberColumnLabelProvider;
import net.mograsim.plugin.tables.RadixSelector;

public class MemoryView extends ViewPart implements ContextObserver
{
	private TableViewer viewer;
	private MemoryTableContentProvider provider;
	private DisplaySettings displaySettings;

	@Override
	public void createPartControl(Composite parent)
	{
		provider = new MemoryTableContentProvider();
		displaySettings = new DisplaySettings();

		GridLayout layout = new GridLayout(6, false);
		parent.setLayout(layout);

		createHeader(parent);
		createViewer(parent);

		displaySettings.addObserver(() -> viewer.refresh());

		setupContextBinding();
	}

	@SuppressWarnings("unused")
	private void createHeader(Composite parent)
	{
		Label fromLabel = new Label(parent, SWT.NONE);
		fromLabel.setText("Address: ");
		Text fromText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		fromText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		VerifyListener vl = new NumberVerifyListener();
		fromText.addVerifyListener(vl);
		fromText.setText("0");
		fromText.addModifyListener(e ->
		{
			try
			{
				provider.setLowerBound(AsmNumberUtil.valueOf(fromText.getText()).longValue());
				viewer.refresh();
			}
			catch (NumberFormatException ex)
			{
				// Nothing to do here
			}
		});

		Label amountLabel = new Label(parent, SWT.NONE);
		amountLabel.setText("Number of cells: ");
		Text amountText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		amountText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		amountText.addVerifyListener(vl);
		amountText.setText("0");
		amountText.addModifyListener(e ->
		{
			try
			{
				provider.setAmount(AsmNumberUtil.valueOf(amountText.getText()).intValue());
				viewer.refresh();
			}
			catch (NumberFormatException ex)
			{
				// Nothing to do here
			}
		});
		new RadixSelector(parent, displaySettings);
	}

	private void createViewer(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		createColumns();
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		bindMainMemory(new WordAddressableMemory(MainMemoryDefinition.create(8, 8, 8L, Long.MAX_VALUE)));
		getSite().setSelectionProvider(viewer);

		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = 6;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gd);
	}

	private void createColumns()
	{
		String[] titles = { "Address", "Data" };
		int[] bounds = { 100, 100 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new AddressLabelProvider());

		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new NumberColumnLabelProvider(displaySettings)
		{
			@Override
			public BigInteger getAsBigInteger(Object element)
			{
				MemoryTableRow row = (MemoryTableRow) element;
				return row.getMemory().getCellAsBigInteger(row.address);
			}

		});
		col.setEditingSupport(new MemoryCellEditingSupport(viewer, displaySettings));
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

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	private void bindMainMemory(MainMemory m)
	{
		viewer.setInput(m);
	}

	private void setupContextBinding()
	{
		MachineContext.getInstance().registerObserver(this);
	}

	@Override
	public void setMachine(Optional<Machine> machine)
	{
		if (machine.isPresent())
			bindMainMemory(machine.get().getMainMemory());
	}
}
