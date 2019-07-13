package net.mograsim.logic.model.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasOverlay;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class EditorGUI
{
	final Display display;
	final Shell shell;
	public final EditorCanvas logicCanvas;
	private final List addList;

	public EditorGUI(Editor editor)
	{
		display = Display.getDefault();
		shell = new Shell(display);
		
		//Layout
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		layout.numColumns = 2;
		
		GridData d = new GridData();
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;
		addList = new List(shell, SWT.FILL);
		addList.setLayoutData(d);
		refreshAddList();
		
		d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = SWT.FILL;
		
		logicCanvas = new EditorCanvas(shell, SWT.TRAIL, editor);
		logicCanvas.setLayoutData(d);
		
		
		new EditorUserInput(editor, this);
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(logicCanvas);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		new ZoomableCanvasOverlay(logicCanvas, null).enableScale();
	}
	
	public void refreshAddList()
	{
		addList.setItems(IndirectGUIComponentCreator.getStandardComponentIDs().toArray(String[]::new));
		addList.select(0);
	}
	
	public String getAddListSelected()
	{
		String[] selection = addList.getSelection();
		if(selection.length == 0)
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
