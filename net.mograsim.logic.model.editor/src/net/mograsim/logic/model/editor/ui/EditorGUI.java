package net.mograsim.logic.model.editor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

		// Layout
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		layout.numColumns = 2;

		GridData d = new GridData();
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;
		d.verticalSpan = 2;
		addList = new List(shell, SWT.V_SCROLL);
		addList.setLayoutData(d);
		refreshAddList();

		d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;

		logicCanvas = new EditorCanvas(shell, SWT.TRAIL, editor);
		logicCanvas.setLayoutData(d);

		setupToolBar();

		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(logicCanvas);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(logicCanvas, null).enableScale();
	}

	private ToolBar setupToolBar()
	{
		GridData d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;

		ToolBar toolBar = new ToolBar(shell, SWT.BORDER);
		toolBar.setLayoutData(d);

		ToolItem snappingLabel = new ToolItem(toolBar, SWT.NONE);
		snappingLabel.setText("Snapping:");
		
		Menu menu = new Menu(shell, SWT.POP_UP);
		ToolItem snappSelect = new ToolItem(toolBar, SWT.DROP_DOWN);
		for (Editor.Snapping sn : Editor.Snapping.values())
		{
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					editor.setSnapping(sn);
					snappSelect.setText(sn.toString());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {}
			});
			item.setText(sn.toString());
		}
		
		snappSelect.setText(editor.getSnapping().toString());
		snappSelect.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.detail == SWT.ARROW)
				{
					Rectangle rect = snappSelect.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = toolBar.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		toolBar.pack();

		return toolBar;
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
