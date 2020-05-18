package net.mograsim.plugin;

import static net.mograsim.logic.model.preferences.RenderPreferences.ACTION_BUTTON;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_ONE_DASH;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_U_DASH;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_X_DASH;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_ZERO_DASH;
import static net.mograsim.logic.model.preferences.RenderPreferences.BIT_Z_DASH;
import static net.mograsim.logic.model.preferences.RenderPreferences.DEBUG_HLSSHELL_DEPTH;
import static net.mograsim.logic.model.preferences.RenderPreferences.DEBUG_OPEN_HLSSHELL;
import static net.mograsim.logic.model.preferences.RenderPreferences.DEFAULT_LINE_WIDTH;
import static net.mograsim.logic.model.preferences.RenderPreferences.DRAG_BUTTON;
import static net.mograsim.logic.model.preferences.RenderPreferences.WIRE_WIDTH_MULTIBIT;
import static net.mograsim.logic.model.preferences.RenderPreferences.WIRE_WIDTH_SINGLEBIT;
import static net.mograsim.logic.model.preferences.RenderPreferences.ZOOM_BUTTON;
import static net.mograsim.plugin.preferences.PluginPreferences.MPM_EDITOR_BITS_AS_COLUMN_NAME;

import java.util.function.Consumer;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.plugin.preferences.PluginPreferences;
import net.mograsim.plugin.util.DoubleFieldEditor;
import net.mograsim.preferences.Preferences;

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
		addBoolean(DEBUG_OPEN_HLSSHELL, "Open the debug HLS shell", parent);
		addInt(DEBUG_HLSSHELL_DEPTH, "Depth of components to list in the debug HLS shell (0: unbounded)", parent);
		addIntCombo(ACTION_BUTTON, "Mouse button for actions", MOUSE_BUTTONS, parent);
		addIntCombo(DRAG_BUTTON, "Mouse button for dragging", MOUSE_BUTTONS, parent);
		addIntCombo(ZOOM_BUTTON, "Mouse button for zooming", MOUSE_BUTTONS, parent);
		addBoolean(MPM_EDITOR_BITS_AS_COLUMN_NAME, "Use the raw bit indices of MPM columns as column titles in the MPM editor", parent);

		insertSeparator(parent);
		addDouble(WIRE_WIDTH_SINGLEBIT, "Line width for single-bit wires", parent);
		addDouble(WIRE_WIDTH_MULTIBIT, "Line width for multi-bit wires", parent);
		addDouble(DEFAULT_LINE_WIDTH, "Line width for other wires", parent);
		addDashesGroup(parent);
		// TODO add other preferences
	}

	private void addDashesGroup(Composite parent)
	{
		Composite dashesGroupParent = new Composite(parent, SWT.NONE);
		dashesGroupParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		GridLayout dashesGroupParentLayout = new GridLayout();
		dashesGroupParentLayout.marginWidth = 0;
		dashesGroupParentLayout.marginHeight = 0;
		dashesGroupParent.setLayout(dashesGroupParentLayout);

		Group dashesGroup = new Group(dashesGroupParent, SWT.NONE);
		dashesGroup.setText("Line dashes");
		dashesGroup.setLayout(new GridLayout());
		dashesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite content = new Composite(dashesGroup, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout contentLayout = new GridLayout(2, false);
		contentLayout.marginWidth = 0;
		contentLayout.marginHeight = 0;
		content.setLayout(contentLayout);

		Label dashesDescription = new Label(content, SWT.WRAP);
		dashesDescription.setText("Line dashes for single-bit wires displaying the respective value.\n"
				+ "Format: A comma-separated list of doubles. The first value is the width of the first segment,\n"
				+ "the second is the spacing between the first and second segments,\n"
				+ "the third is the width of the second segment and so on.");
		dashesDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		addString(BIT_ZERO_DASH, "0", content);
		addString(BIT_Z_DASH, "Z", content);
		addString(BIT_X_DASH, "X", content);
		addString(BIT_U_DASH, "U", content);
		addString(BIT_ONE_DASH, "1", content);
	}

	private void addBoolean(String name, String label, Composite parent)
	{
		defaultsPreferenceStore(name).getBoolean(name);
		addField(new BooleanFieldEditor(name, label, parent));
	}

	private void addInt(String name, String label, Composite parent)
	{
		defaultsPreferenceStore(name).getInt(name);
		addField(new IntegerFieldEditor(name, label, parent));
	}

	private void addDouble(String name, String label, Composite parent)
	{
		defaultsPreferenceStore(name).getDouble(name);
		addField(new DoubleFieldEditor(name, label, parent));
	}

	private void addIntCombo(String name, String label, String[][] entryNamesAndValues, Composite parent)
	{
		addCombo(name, label, entryNamesAndValues, parent, defaultsPreferenceStore(name)::getInt);
	}

	private void addCombo(String name, String label, String[][] entryNamesAndValues, Composite parent, Consumer<String> initializeDefault)
	{
		initializeDefault.accept(name);
		addField(new ComboFieldEditor(name, label, entryNamesAndValues, parent));
	}

	private void addString(String name, String label, Composite parent)
	{
		defaultsPreferenceStore(name).getString(name);
		addField(new StringFieldEditor(name, label, parent));
	}

	private static void insertSeparator(Composite parent)
	{
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
	}

	private static Preferences defaultsPreferenceStore(String name)
	{
		// TODO is it a good idea to list all preference systems here?
		// Maybe rather split the page into one per preference system
		MograsimActivator mograsim = MograsimActivator.instance();
		if (name.startsWith(RenderPreferences.PREFIX))
			return mograsim.getRenderPrefs();
		else if (name.startsWith(PluginPreferences.PREFIX))
			return mograsim.getPluginPrefs();
		else
			throw new IllegalArgumentException("The preference system containing " + name + " is not known");
	}
}