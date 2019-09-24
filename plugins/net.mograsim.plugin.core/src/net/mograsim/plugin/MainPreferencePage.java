package net.mograsim.plugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
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
		Composite parent = getFieldEditorParent();
		addField(new BooleanFieldEditor("net.mograsim.logic.model.debug.openhlsshell", "Open the debug HLS shell", parent));
		addField(new IntegerFieldEditor("net.mograsim.logic.model.debug.hlsshelldepth",
				"Depth of components to list in the debug HLS shell", parent));
		// TODO add other preferences
	}
}