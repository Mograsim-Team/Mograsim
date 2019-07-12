package net.mograsim.logic.ui.editor;

import java.io.IOException;

import net.mograsim.logic.ui.editor.DialogManager.InteractiveDialog;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.SubmodelComponentDeserializer;
import net.mograsim.logic.ui.util.JsonHandler;

public class SaveLoadManager
{
	private String savePath = null;
	private Editor editor;

	public SaveLoadManager(Editor editor)
	{
		this.editor = editor;
	}

	public void save()
	{
		if (savePath == null)
			openSaveAsDialog();
		else
			innerSave();
	}

	public void openSaveAsDialog()
	{
		InteractiveDialog d = new InteractiveDialog("Save as...", "Save", "Cancel", "Path");
		d.open();
		
		if(InteractiveDialog.InteractiveDialogState.ACCEPTED.equals(d.getState()))
		{
			savePath = d.getText();
			innerSave();
		}
	}

	private void innerSave()
	{
		try
		{
			JsonHandler.writeJson(editor.toBeEdited.calculateParams(c ->
			{
				if (Editor.identifierPerComponent.containsKey(c))
					return Editor.identifierPerComponent.get(c);
				return "class:" + c.getClass().getCanonicalName();
			}), savePath);
		} catch (IOException e)
		{
			savePath = null;
			System.err.println("Failed to save component!");
			e.printStackTrace();
		}
	}

	public static void openLoadDialog()
	{
		InteractiveDialog load = new InteractiveDialog("Load Component...", "Load", "Cancel", "Path");
		load.open();
		if(InteractiveDialog.InteractiveDialogState.ACCEPTED.equals(load.getState()))
		{
			new Editor((DeserializedSubmodelComponent) SubmodelComponentDeserializer
					.create(new ViewModelModifiable(), load.getText()));
		}
	}
}
