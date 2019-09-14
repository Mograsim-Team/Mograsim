package net.mograsim.plugin.tables;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

public class LazyTableViewer extends TableViewer
{
	public static Color highlightColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);

	public LazyTableViewer(Composite parent, int style)
	{
		super(parent, style | SWT.VIRTUAL);
	}

	public LazyTableViewer(Composite parent)
	{
		super(parent);
	}

	public LazyTableViewer(Table table)
	{
		super(table);
	}

	public void highlightRow(int index, boolean highlight)
	{
		Table table = getTable();
		if (index < 0 || index >= table.getItemCount())
			return;
		table.getItem(index).setBackground(highlight ? highlightColor : table.getBackground());
		System.out.println("Infinite loop!!!");
		((ILazyContentProvider) getContentProvider()).updateElement(index);
	}

	@Override
	public void setContentProvider(IContentProvider provider)
	{
		if (!(provider instanceof ILazyContentProvider))
			throw new IllegalArgumentException("Content provider must be an ILazyContentProvider");
		super.setContentProvider(provider);
	}

	@Override
	public void refresh()
	{
		Table t = getTable();
		ILazyContentProvider provider = (ILazyContentProvider) getContentProvider();
		doClearAll();
		int startIndex = t.getTopIndex();
		int numRows = t.getBounds().height / t.getItemHeight();
		int endIndex = Integer.min(startIndex + numRows + 5, doGetItemCount());

		for (int i = startIndex; i < endIndex; i++)
		{
			provider.updateElement(i);
		}
	}
}
