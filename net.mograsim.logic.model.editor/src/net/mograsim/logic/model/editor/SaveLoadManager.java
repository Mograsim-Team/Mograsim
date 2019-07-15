package net.mograsim.logic.model.editor;

import java.io.IOException;

import net.mograsim.logic.model.editor.ui.DialogManager;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;

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
		String result[] = DialogManager.openMultiTextDialog("Save as...", "Save", "Cancel", "Path");
		if (result != null)
		{
			savePath = result[0];
			innerSave();
		}
	}

	private void innerSave()
	{
		try
		{
			SubmodelComponentSerializer.serialize(editor.toBeEdited, c ->
			{
				if (Editor.identifierPerComponent.containsKey(c))
					return Editor.identifierPerComponent.get(c);
				return "class:" + c.getClass().getCanonicalName();
			}, savePath);
		}
		catch (IOException e)
		{
			savePath = null;
			System.err.println("Failed to save component!");
			e.printStackTrace();
		}
	}

	public static void openLoadDialog() throws IOException
	{
		String[] result = DialogManager.openMultiTextDialog("Load Component...", "Load", "Cancel", "Path");
		if (result != null)
		{
			new Editor((DeserializedSubmodelComponent) SubmodelComponentSerializer.deserialize(new ViewModelModifiable(), result[0]));
		}
	}
}
