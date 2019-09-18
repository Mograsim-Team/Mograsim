package net.mograsim.logic.model.editor;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
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
		Shell fdShell = new Shell();
		FileDialog fd = new FileDialog(fdShell, SWT.SAVE);
		fd.setText("Save as...");
		fd.setFilterExtensions(new String[] { "*.json" });
		String result = fd.open();
		fdShell.dispose();
		if (result != null)
		{
			savePath = result;
			innerSave();
		}
	}

	private void innerSave()
	{
		try
		{
			SubmodelComponentSerializer.serialize(editor.toBeEdited, savePath);
		}
		catch (IOException e)
		{
			savePath = null;
			System.err.println("Failed to save component!");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused") // Editor
	public static void openLoadDialog() throws IOException
	{
		Shell fdShell = new Shell();
		FileDialog fd = new FileDialog(fdShell, SWT.OPEN);
		fd.setText("Load component...");
		fd.setFilterExtensions(new String[] { "*.json" });
		String result = fd.open();
		fdShell.dispose();
		if (result != null)
		{
			new Editor((DeserializedSubmodelComponent) IndirectModelComponentCreator.createComponent(new LogicModelModifiable(),
					"jsonfile:" + result));
		}
	}
}
