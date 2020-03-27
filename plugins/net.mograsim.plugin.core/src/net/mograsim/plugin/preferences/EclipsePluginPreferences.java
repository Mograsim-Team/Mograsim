package net.mograsim.plugin.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.themes.ITheme;

public class EclipsePluginPreferences extends EclipsePreferences implements PluginPreferences
{
	public EclipsePluginPreferences(ITheme theme, IPreferenceStore prefs)
	{
		super(theme, prefs, new DefaultPluginPreferences());
	}
}
