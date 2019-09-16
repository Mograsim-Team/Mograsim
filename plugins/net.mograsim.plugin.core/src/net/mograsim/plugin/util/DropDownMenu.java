package net.mograsim.plugin.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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
			item.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					entry.listener.widgetSelected(e);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					widgetSelected(e);
				}
			});
			item.setText(entry.title);
		}

		button.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				Rectangle rect = button.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = button.getParent().toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}
		});
	}

	public Button getButton()
	{
		return button;
	}

	public static class DropDownEntry
	{
		public final String title;
		public final EntrySelectedListener listener;

		public DropDownEntry(String title, EntrySelectedListener listener)
		{
			super();
			this.title = title;
			this.listener = listener;
		}
	}

	@FunctionalInterface
	public static interface EntrySelectedListener
	{
		public void widgetSelected(SelectionEvent e);
	}
}
