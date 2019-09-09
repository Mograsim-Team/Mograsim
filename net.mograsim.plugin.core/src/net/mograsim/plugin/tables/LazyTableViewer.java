package net.mograsim.plugin.tables;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class LazyTableViewer extends TableViewer
{

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

	@Override
	public void setContentProvider(IContentProvider provider)
	{
		if (!(provider instanceof ILazyContentProvider))
			throw new IllegalArgumentException("Content provider must be an ILazyContentProvider");
		super.setContentProvider(provider);
	}

	public void refreshLazy()
	{
		Table t = getTable();
		ILazyContentProvider provider = (ILazyContentProvider) getContentProvider();
		doClearAll();
		int startIndex = t.getTopIndex();
		int numRows = t.getBounds().height / t.getItemHeight();
		int endIndex = startIndex + numRows + 5;

		for (int i = startIndex; i < endIndex; i++)
		{
			provider.updateElement(i);
		}
	}
}
