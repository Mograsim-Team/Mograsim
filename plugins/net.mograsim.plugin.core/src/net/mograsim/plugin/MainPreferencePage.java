package net.mograsim.plugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MainPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public MainPreferencePage()
	{
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(MograsimActivator.instance().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors()
	{
		addField(new BooleanFieldEditor("net.mograsim.logic.model.debug.openhlsshell", "Open the debug HLS shell", getFieldEditorParent()));
		// TODO add other preferences
	}
}