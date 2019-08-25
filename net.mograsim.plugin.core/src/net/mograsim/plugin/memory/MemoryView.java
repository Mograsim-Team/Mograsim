package net.mograsim.plugin.memory;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import net.mograsim.machine.DefaultMainMemoryDefinition;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.standard.memory.WordAddressableMemory;
import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class MemoryView extends ViewPart
{
	private TableViewer viewer;
	private MemoryTableContentProvider provider;
	private DisplaySettings displaySettings;
	private String addressFormat;

	@Override
	public void createPartControl(Composite parent)
	{
		// TODO: externalize Strings!
		provider = new MemoryTableContentProvider();
		displaySettings = new DisplaySettings();
		displaySettings.setDataNumberType(NumberType.HEXADECIMAL);

		GridLayout layout = new GridLayout(6, false);
		parent.setLayout(layout);
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
			catch (@SuppressWarnings("unused") NumberFormatException ex)
			{
				// Nothing to do here
			}
//			fromText.setText(AsmNumberUtil.toString(BigInteger.valueOf(provider.getLowerBound()), NumberType.HEXADECIMAL));
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
			catch (@SuppressWarnings("unused") NumberFormatException ex)
			{
				// Nothing to do here
			}
//			amountText.setText(Integer.toString(provider.getAmount()));
		});

		setupRadixSelector(parent);

		createViewer(parent);
	}

	private void setupRadixSelector(Composite parent)
	{
		Label radixLabel = new Label(parent, SWT.NONE);
		radixLabel.setText("Radix: ");
		Combo selectRadix = new Combo(parent, SWT.READ_ONLY);

		String entries[] = new String[] { "Binary", "Octal", "Decimal", "Hexadecimal" };
		NumberType corTypes[] = new NumberType[] { NumberType.BINARY, NumberType.OCTAL, NumberType.DECIMAL, NumberType.HEXADECIMAL };
		selectRadix.setItems(entries);
		selectRadix.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int index = selectRadix.getSelectionIndex();
				if (index == -1)
					displaySettings.setDataNumberType(NumberType.HEXADECIMAL);
				else
				{
					displaySettings.setDataNumberType(corTypes[index]);
				}
				viewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
	}

	private void createViewer(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setContentProvider(provider);
		setMemoryBinding(new WordAddressableMemory(new DefaultMainMemoryDefinition(8, 8, 8L, 256L)));
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
		col.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				MemoryTableRow row = (MemoryTableRow) element;
				return String.format(addressFormat, row.address);// TODO: make the string length dependent on memory address length
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				MemoryTableRow row = (MemoryTableRow) element;
				return AsmNumberUtil.toString(row.getMemory().getCellAsBigInteger(row.address), displaySettings.getDataNumberType());
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

	public void setMemoryBinding(MainMemory m)
	{
		int hexAddressLength = Long.toUnsignedString(m.getDefinition().getMaximalAddress(), 16).length();
		addressFormat = "0x%0" + hexAddressLength + "X";
		viewer.setInput(m);
	}

	public static class DisplaySettings
	{
		private AsmNumberUtil.NumberType dataNumberType;

		public AsmNumberUtil.NumberType getDataNumberType()
		{
			return dataNumberType;
		}

		public void setDataNumberType(AsmNumberUtil.NumberType dataNumberType)
		{
			this.dataNumberType = dataNumberType;
		}
	}
}
