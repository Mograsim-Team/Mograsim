package net.mograsim.plugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.themes.ITheme;

import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.preferences.EclipsePluginPreferences;
import net.mograsim.plugin.preferences.EclipseRenderPreferences;
import net.mograsim.plugin.preferences.PluginPreferences;
import net.mograsim.preferences.Preferences;

public final class MograsimActivator extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "net.mograsim.plugin.core";

	private static MograsimActivator instance;

	public static MograsimActivator instance()
	{
		if (instance == null)
			throw new IllegalStateException("MograsimActivator not yet created!");
		return instance;
	}

	private final RenderPreferences renderPrefs;
	private final PluginPreferences pluginPrefs;

	public MograsimActivator()
	{
		if (instance != null)
			throw new IllegalStateException("MograsimActivator already created!");
		instance = this;

		MachineRegistry.initialize();
		ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
		IPreferenceStore preferenceStore = getPreferenceStore();
		renderPrefs = new EclipseRenderPreferences(currentTheme, preferenceStore);
		pluginPrefs = new EclipsePluginPreferences(currentTheme, preferenceStore);
	}

	public RenderPreferences getRenderPrefs()
	{
		return renderPrefs;
	}

	public Preferences getPluginPrefs()
	{
		return pluginPrefs;
	}
}