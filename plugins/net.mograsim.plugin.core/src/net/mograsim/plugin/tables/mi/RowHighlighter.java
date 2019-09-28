package net.mograsim.plugin.tables.mi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import net.mograsim.plugin.tables.LazyTableViewer;

public class RowHighlighter
{
	private int highlighted = -1, toHighlight;
	private LazyTableViewer viewer;
	private AtomicBoolean waiting = new AtomicBoolean();
	private ColorProvider cProv;

	public RowHighlighter(LazyTableViewer viewer, ColorProvider cProv)
	{
		this.viewer = viewer;
		this.cProv = cProv;
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
		Table table = viewer.getTable();
		cProv.highlight(row);
		if (row != -1)
		{
			table.showItem(table.getItem(Math.min(table.getItemCount(), row + 2)));
			table.showItem(table.getItem(row));
			Optional.of(table.getItem(row).getData()).ifPresent(d -> viewer.update(d, null));
		}
		if (highlighted != -1)
			Optional.of(table.getItem(highlighted).getData()).ifPresent(d -> viewer.update(d, null));
		highlighted = row;
	}
}
