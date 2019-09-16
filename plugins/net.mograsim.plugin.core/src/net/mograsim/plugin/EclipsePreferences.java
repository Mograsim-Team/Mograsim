package net.mograsim.plugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.themes.ITheme;

import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.DefaultPreferences;
import net.mograsim.preferences.Preferences;

public class EclipsePreferences extends Preferences
{
	private final ITheme theme;
	private final IPreferenceStore prefs;
	private final Preferences defaultPrefs;

	public EclipsePreferences(ITheme theme, IPreferenceStore prefs)
	{
		this.theme = theme;
		this.prefs = prefs;
		this.defaultPrefs = new DefaultPreferences();
	}

	@Override
	public boolean getBoolean(String name)
	{
		prefs.setDefault(name, defaultPrefs.getBoolean(name));
		return prefs.getBoolean(name);
	}

	@Override
	public int getInt(String name)
	{
		prefs.setDefault(name, defaultPrefs.getInt(name));
		return prefs.getInt(name);
	}

	@Override
	public double getDouble(String name)
	{
		prefs.setDefault(name, defaultPrefs.getDouble(name));
		return prefs.getDouble(name);
	}

	@Override
	public ColorDefinition getColorDefinition(String name)
	{
		RGB rgb = getColorRegistry().getRGB(name);
		if (rgb == null)
		{
			StatusManager.getManager().handle(new Status(IStatus.ERROR, "net.mograsim.plugin.core", "No color for name " + name));
			return null;
		}
		return new ColorDefinition(rgb.red, rgb.green, rgb.blue);
	}

	@Override
	public Color getColor(String name)
	{
		return getColorRegistry().get(name);
	}

	private ColorRegistry getColorRegistry()
	{
		return theme.getColorRegistry();
	}
}