package net.mograsim.plugin.tables.mi;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import net.mograsim.plugin.tables.LazyTableViewer;

public class RowHighlighter
{
	private int highlighted, toHighlight;
	private LazyTableViewer viewer;
	private AtomicBoolean waiting = new AtomicBoolean();

	public RowHighlighter(LazyTableViewer viewer)
	{
		this.viewer = viewer;
	}

	public void highlight(int row)
	{
		synchronized (waiting)
		{
			toHighlight = row;
			if (!waiting.get())
			{
				waiting.set(true);
				Display.getDefault().asyncExec(() ->
				{
					synchronized (waiting)
					{
						waiting.set(false);
						if (!viewer.getTable().isDisposed())
							innerHighlight(toHighlight);
					}
				});
			}
		}
	}

	private void innerHighlight(int row)
	{
		viewer.highlightRow(highlighted, false);
		highlighted = row;
		if (row != -1)
		{
			viewer.highlightRow(row, true);
			Table table = viewer.getTable();
			table.showItem(table.getItem(Math.min(table.getItemCount(), row + 2)));
			table.showItem(table.getItem(row));
		}
	}
}
