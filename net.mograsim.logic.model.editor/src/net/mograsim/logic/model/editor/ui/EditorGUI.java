package net.mograsim.logic.model.editor.ui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.SaveLoadManager;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class EditorGUI
{
	public final Display display;
	public final Shell shell;
	public final EditorCanvas logicCanvas;
	private final List addList;
	private final Editor editor;

	public EditorGUI(Editor editor)
	{
		this.editor = editor;
		display = Display.getDefault();
		shell = new Shell(display);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);

		setupTopToolBar(shell);
		Composite innerComp = new Composite(shell, SWT.NONE);
		GridData innerCompData = new GridData();
		innerCompData.grabExcessHorizontalSpace = true;
		innerCompData.grabExcessVerticalSpace = true;
		innerCompData.horizontalAlignment = SWT.FILL;
		innerCompData.verticalAlignment = SWT.FILL;
		innerComp.setLayoutData(innerCompData);

		GridLayout innerLayout = new GridLayout();
		innerComp.setLayout(innerLayout);
		innerLayout.numColumns = 2;

		GridData d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;

		logicCanvas = new EditorCanvas(innerComp, SWT.TRAIL, editor);
		logicCanvas.setLayoutData(d);

		d = new GridData();
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;
		d.verticalSpan = 2;
		addList = new List(innerComp, SWT.V_SCROLL);
		addList.setLayoutData(d);
		refreshAddList();

		setupBottomToolBar(innerComp);

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(logicCanvas);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(logicCanvas, null).enableScale();
	}

	private ToolBar setupTopToolBar(Composite parent)
	{
		GridData d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;

		ToolBar toolBar = new ToolBar(parent, SWT.BORDER);
		toolBar.setLayoutData(d);

		ToolItem file = new ToolItem(toolBar, SWT.DROP_DOWN);

		// TODO
//		DropDownEntry newEntry = new DropDownEntry("New", e -> {
//		});
		DropDownEntry loadEntry = new DropDownEntry("Load", e ->
		{
			try
			{
				SaveLoadManager.openLoadDialog();
			}
			catch (IOException e1)
			{
				editor.dialogManager.openWarningDialog("Failed to load Component!", e1.getMessage());
			}
		});
		DropDownEntry saveEntry = new DropDownEntry("Save", e -> editor.save());
		DropDownEntry saveAsEntry = new DropDownEntry("Save as...", e -> editor.saveAs());

		DropDownEntry[] entries = new DropDownEntry[] { loadEntry, saveEntry, saveAsEntry };

		setupDrowpDownMenu(file, entries);

		file.setText("File");
		return toolBar;
	}

	private ToolBar setupBottomToolBar(Composite parent)
	{
		GridData d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;

		ToolBar toolBar = new ToolBar(parent, SWT.BORDER);
		toolBar.setLayoutData(d);

		ToolItem snappingLabel = new ToolItem(toolBar, SWT.NONE);
		snappingLabel.setText("Snapping:");

		ToolItem snappSelect = new ToolItem(toolBar, SWT.DROP_DOWN);
		DropDownEntry[] entries = new DropDownEntry[Editor.Snapping.values().length];
		int index = 0;
		for (Editor.Snapping sn : Editor.Snapping.values())
		{
			entries[index++] = new DropDownEntry(sn.toString(), e ->
			{
				editor.setSnapping(sn);
				snappSelect.setText(sn.toString());
			});
		}
		snappSelect.setText(editor.getSnapping().toString());
		setupDrowpDownMenu(snappSelect, entries);

		new ToolItem(toolBar, SWT.SEPARATOR);

		toolBar.pack();

		return toolBar;
	}

	private void setupDrowpDownMenu(ToolItem parent, DropDownEntry[] entries)
	{
		Menu menu = new Menu(shell, SWT.POP_UP);
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

		parent.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.detail == SWT.ARROW)
				{
					Rectangle rect = parent.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = parent.getParent().toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});
	}

	private static class DropDownEntry
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

	private static interface EntrySelectedListener
	{
		public void widgetSelected(SelectionEvent e);
	}

	public void refreshAddList()
	{
		addList.setItems(IndirectGUIComponentCreator.getStandardComponentIDs().toArray(String[]::new));
		addList.select(0);
	}

	public String getAddListSelected()
	{
		String[] selection = addList.getSelection();
		if (selection.length == 0)
			throw new IllegalStateException("Selection in the Add Component List may never be empty!");
		return selection[0];
	}

	public void open()
	{
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

}
