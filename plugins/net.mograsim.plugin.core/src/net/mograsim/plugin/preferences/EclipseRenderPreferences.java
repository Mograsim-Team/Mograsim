package net.mograsim.plugin.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.themes.ITheme;

import net.mograsim.logic.model.preferences.DefaultRenderPreferences;
import net.mograsim.logic.model.preferences.RenderPreferences;

public class EclipseRenderPreferences extends EclipsePreferences implements RenderPreferences
{
	public EclipseRenderPreferences(ITheme theme, IPreferenceStore prefs)
	{
		super(theme, prefs, new DefaultRenderPreferences());
	}
}
