package net.mograsim.plugin.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class OverlappingFillLayout extends Layout
{
	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
	{
		Point size = new Point(wHint == SWT.DEFAULT ? 0 : wHint, hHint == SWT.DEFAULT ? 0 : hHint);

		Control[] children = composite.getChildren();
		for (Control child : children)
		{
			Point childSize = child.computeSize(wHint, hHint, flushCache);
			size.x = Math.max(size.x, childSize.x);
			size.y = Math.max(size.y, childSize.y);
		}

		return size;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache)
	{
		Rectangle bounds = composite.getClientArea();

		Control[] children = composite.getChildren();
		for (Control child : children)
			child.setBounds(bounds);
	}
}