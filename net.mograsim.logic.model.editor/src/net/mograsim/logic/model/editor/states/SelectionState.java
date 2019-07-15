package net.mograsim.logic.model.editor.states;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas.ZoomedRenderer;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.Selection;
import net.mograsim.logic.model.editor.handles.Handle;
import net.mograsim.logic.model.editor.handles.PinHandle;
import net.mograsim.logic.model.editor.handles.WireHandle;
import net.mograsim.logic.model.editor.handles.Handle.HandleClickInfo;
import net.mograsim.logic.model.editor.handles.WireHandle.WireHandleClickInfo;
import net.mograsim.logic.model.editor.ui.DialogManager;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;

public class SelectionState extends EditorState
{
	private final ZoomedRenderer outlineRenderer = gc ->
	{
		if (editor.getSelection().size() > 1)
		{
			gc.setLineWidth(1);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
			gc.drawRectangle(editor.getSelection().getBounds());
		}
	};

	public SelectionState(Editor editor, StateManager manager)
	{
		super(editor, manager);
	}

	@Override
	public void add()
	{
		Point curserPos = editor.userInput.getWorldMousePosition();
		editor.addComponent(curserPos.x, curserPos.y);
	}

	@Override
	public void delete()
	{
		editor.deleteSelection();
	}

	@Override
	public void copy()
	{
		editor.copy();
	}

	@Override
	public void paste()
	{
		Point curserPos = editor.userInput.getWorldMousePosition();
		editor.paste(curserPos.x, curserPos.y);
	}

	@Override
	public void duplicate()
	{
		editor.duplicate();
	}

	@Override
	public void grab()
	{
		if (!editor.getSelection().isEmpty())
			manager.setState(new GrabState(editor, manager));
	}

	@Override
	public void boxSelect()
	{
		manager.setState(new BoxSelectionState(editor, manager));
	}

	@Override
	public void onEntry()
	{
		editor.gui.logicCanvas.addZoomedRenderer(outlineRenderer);
		editor.gui.logicCanvas.redrawThreadsafe();
	}

	@Override
	public void onExit()
	{
		editor.gui.logicCanvas.removeZoomedRenderer(outlineRenderer);
		editor.gui.logicCanvas.redrawThreadsafe();
	}

	@Override
	public void clickedEmpty(Point clicked, int stateMask)
	{
		editor.getSelection().clear();
		if ((stateMask & SWT.ALT) == SWT.ALT)
		{
			String[] result = DialogManager.openMultiTextDialog("Add Pin...", "Add", "Cancel", "Name", "Logic Width");
			if (result != null)
			{
				try
				{
					Pin p = editor.toBeEdited.addSubmodelInterface(
							new MovablePin(editor.toBeEdited, result[0], Integer.parseInt(result[1]), clicked.x, clicked.y));
					editor.handleManager.getInterfacePinHandle(p).reqMove(clicked.x, clicked.y);
				}
				catch (NumberFormatException e)
				{
					editor.dialogManager.openWarningDialog("Failed to create Pin!", "Bit width must be a number!");
				}
			}
		}
	}

	@Override
	public boolean clickedHandle(HandleClickInfo handleClickInfo)
	{
		switch (handleClickInfo.clicked.getType())
		{
		case INTERFACE_PIN:
			if ((handleClickInfo.stateMask & SWT.CTRL) == SWT.CTRL)
			{
				manager.setState(new CreateWireState(editor, manager, (PinHandle) handleClickInfo.clicked));
				break;
			}
		case CORNER:
		case COMPONENT:
		case WIRE_POINT:
			boolean additive = (handleClickInfo.stateMask & SWT.SHIFT) == SWT.SHIFT;
			select(handleClickInfo.clicked, additive);
			break;
		case STATIC_PIN:
			if ((handleClickInfo.stateMask & SWT.CTRL) == SWT.CTRL)
				manager.setState(new CreateWireState(editor, manager, (PinHandle) handleClickInfo.clicked));
			else
				return false;
			break;
		case WIRE:
			if ((handleClickInfo.stateMask & SWT.CTRL) == SWT.CTRL)
			{
				WireHandleClickInfo info = (WireHandleClickInfo) handleClickInfo;
				WireHandle clicked = (WireHandle) info.clicked;
				clicked.parent.insertPathPoint(info.posOnWire, info.segment);
			} else
			{
				additive = (handleClickInfo.stateMask & SWT.SHIFT) == SWT.SHIFT;
				select(handleClickInfo.clicked, additive);
			}
			break;
		default:
			return false;
		}
		return true;
	}

	private void select(Handle h, boolean additive)
	{
		Selection sel = editor.getSelection();
		if (sel.contains(h))
			if (additive)
				sel.remove(h);
			else
			{
				if (editor.getSelection().size() > 1)
				{
					sel.clear();
					sel.add(h);
				} else
					sel.clear();
			}
		else
		{
			if (!additive)
				sel.clear();
			sel.add(h);
		}
	}
}
