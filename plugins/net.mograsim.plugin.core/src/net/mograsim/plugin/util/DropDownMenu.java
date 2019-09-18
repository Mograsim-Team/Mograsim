package net.mograsim.plugin.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class DropDownMenu
{
	private Button button;

	public DropDownMenu(Composite parent, String label, DropDownEntry... entries)
	{
		button = new Button(parent, SWT.PUSH);
		button.setText(label);
		setupDrowpDownMenu(entries);
	}

	private void setupDrowpDownMenu(DropDownEntry[] entries)
	{
		Menu menu = new Menu(button);
		for (DropDownEntry entry : entries)
		{
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.addListener(SWT.Selection, e -> entry.onSelected.run());
			item.setText(entry.title);
		}

		button.addListener(SWT.Selection, e ->
		{
			Rectangle rect = button.getBounds();
			Point pt = new Point(rect.x, rect.y + rect.height);
			pt = button.getParent().toDisplay(pt);
			menu.setLocation(pt.x, pt.y);
			menu.setVisible(true);
		});
	}

	public Button getButton()
	{
		return button;
	}

	public static class DropDownEntry
	{
		public final String title;
		public final Runnable onSelected;

		public DropDownEntry(String title, Runnable onSelected)
		{
			this.title = title;
			this.onSelected = onSelected;
		}
	}
}
