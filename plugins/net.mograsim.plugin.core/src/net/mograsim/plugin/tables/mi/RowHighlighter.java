package net.mograsim.plugin.tables.mi;

import java.util.Optional;

import org.eclipse.swt.widgets.Table;

import net.mograsim.plugin.tables.LazyTableViewer;
import net.mograsim.plugin.util.SingleSWTRequest;

public class RowHighlighter
{
	private int highlighted = -1;
	private LazyTableViewer viewer;
	private ColorProvider cProv;
	private SingleSWTRequest requester = new SingleSWTRequest();

	public RowHighlighter(LazyTableViewer viewer, ColorProvider cProv)
	{
		this.viewer = viewer;
		this.cProv = cProv;
	}

	public void highlight(int row)
	{
		requester.request(() ->
		{
			if (!viewer.getTable().isDisposed())
				innerHighlight(row);
		});
	}

	private void innerHighlight(int row)
	{
		Table table = viewer.getTable();
		cProv.highlight(row);
		if (row != -1)
		{
			table.showItem(table.getItem(Math.min(table.getItemCount(), row + 2)));
			table.showItem(table.getItem(row));
			Optional.ofNullable(table.getItem(row).getData()).ifPresent(d -> viewer.update(d, null));
		}
		if (highlighted != -1)
			Optional.ofNullable(table.getItem(highlighted).getData()).ifPresent(d -> viewer.update(d, null));
		highlighted = row;
	}
}
