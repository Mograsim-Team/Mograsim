package net.mograsim.plugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MainPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private static final String[][] MOUSE_BUTTONS = { { "left", "1" }, { "middle", "2" }, { "right", "3" }, { "4th", "4" },
			{ "5th", "5" } };

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
				"Depth of components to list in the debug HLS shell (0: unbounded)", parent));
		addField(new ComboFieldEditor("net.mograsim.logic.model.button.action", "Mouse button for actions", MOUSE_BUTTONS, parent));
		addField(new ComboFieldEditor("net.mograsim.logic.model.button.drag", "Mouse button for dragging", MOUSE_BUTTONS, parent));
		addField(new ComboFieldEditor("net.mograsim.logic.model.button.zoom", "Mouse button for zooming", MOUSE_BUTTONS, parent));
		addField(new BooleanFieldEditor("net.mograsim.plugin.core.editors.mpm.bitsascolumnname",
				"Use the raw bit indices of MPM columns as column titles in the MPM editor", parent));
		// TODO add other preferences
	}
}