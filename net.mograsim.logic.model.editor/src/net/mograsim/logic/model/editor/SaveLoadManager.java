package net.mograsim.logic.model.editor;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

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
			IdentifierGetter idGetter = new IdentifierGetter();
			idGetter.componentIDs = c -> getStandardID(c, IndirectGUIComponentCreator.getStandardComponentIDs(), true);
			idGetter.symbolRendererIDs = h -> getStandardID(h,
					SubmodelComponentSnippetSuppliers.symbolRendererSupplier.getStandardSnippetIDs());
			idGetter.outlineRendererIDs = h -> getStandardID(h,
					SubmodelComponentSnippetSuppliers.outlineRendererSupplier.getStandardSnippetIDs());
			idGetter.highLevelStateHandlerIDs = h -> getStandardID(h,
					SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier.getStandardSnippetIDs());
			idGetter.atomicHighLevelStateHandlerIDs = h -> getStandardID(h,
					StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier.getStandardSnippetIDs());
			idGetter.subcomponentHighLevelStateHandlerIDs = h -> getStandardID(h,
					StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier.getStandardSnippetIDs());
			SubmodelComponentSerializer.serialize(editor.toBeEdited, idGetter, savePath);
		}
		catch (IOException e)
		{
			savePath = null;
			System.err.println("Failed to save component!");
			e.printStackTrace();
		}
	}

	private static String getStandardID(Object o, Map<String, String> standardIDs)
	{
		return getStandardID(o, standardIDs, false);
	}

	private static String getStandardID(Object o, Map<String, String> standardIDs, boolean standardIDsHaveClassConcatenated)
	{
		String verboseID = (standardIDsHaveClassConcatenated ? "class:" : "") + o.getClass().getCanonicalName();
		return standardIDs.entrySet().stream().filter(e -> e.getValue().equals(verboseID)).map(Entry::getKey).findAny()
				.orElseGet(() -> (standardIDsHaveClassConcatenated ? "" : "class:") + verboseID);
	}

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
			new Editor((DeserializedSubmodelComponent) SubmodelComponentSerializer.deserialize(new ViewModelModifiable(), result));
		}
	}
}
