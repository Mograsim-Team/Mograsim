package net.mograsim.plugin.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.themes.ITheme;

import net.mograsim.plugin.MograsimActivator;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.Preferences;

public class EclipsePreferences implements Preferences
{
	private final ITheme theme;
	private final IPreferenceStore prefs;
	private final Preferences defaultPrefs;

	private final ListenerManager<Boolean> booleanListeners;
	private final ListenerManager<Integer> intListeners;
	private final ListenerManager<Double> doubleListeners;
	private final ListenerManager<String> stringListeners;
	private final ListenerManager<ColorDefinition> colorDefinitionListeners;

	protected EclipsePreferences(ITheme theme, IPreferenceStore prefs, Preferences defaultPrefs)
	{
		this.theme = theme;
		this.prefs = prefs;
		this.defaultPrefs = defaultPrefs;

		this.booleanListeners = new ListenerManager<>(this::getBoolean);
		this.intListeners = new ListenerManager<>(this::getInt);
		this.doubleListeners = new ListenerManager<>(this::getDouble);
		this.stringListeners = new ListenerManager<>(this::getString);
		this.colorDefinitionListeners = new ListenerManager<>(this::getColorDefinition);
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
	public String getString(String name)
	{
		prefs.setDefault(name, defaultPrefs.getString(name));
		return prefs.getString(name);
	}

	@Override
	public ColorDefinition getColorDefinition(String name)
	{
		RGB rgb = getColorRegistry().getRGB(name);
		if (rgb == null)
		{
			StatusManager.getManager().handle(new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, "No color for name " + name));
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

	@Override
	public void addBooleanListener(String name, Consumer<Boolean> listener)
	{
		booleanListeners.addListener(name, listener);
	}

	@Override
	public void removeBooleanListener(String name, Consumer<Boolean> listener)
	{
		booleanListeners.removeListener(name, listener);
	}

	@Override
	public void addIntListener(String name, Consumer<Integer> listener)
	{
		intListeners.addListener(name, listener);
	}

	@Override
	public void removeIntListener(String name, Consumer<Integer> listener)
	{
		intListeners.removeListener(name, listener);
	}

	@Override
	public void addDoubleListener(String name, Consumer<Double> listener)
	{
		doubleListeners.addListener(name, listener);
	}

	@Override
	public void removeDoubleListener(String name, Consumer<Double> listener)
	{
		doubleListeners.removeListener(name, listener);
	}

	@Override
	public void addStringListener(String name, Consumer<String> listener)
	{
		stringListeners.addListener(name, listener);
	}

	@Override
	public void removeStringListener(String name, Consumer<String> listener)
	{
		stringListeners.removeListener(name, listener);
	}

	@Override
	public void addColorDefinitionListener(String name, Consumer<ColorDefinition> listener)
	{
		colorDefinitionListeners.addListener(name, listener);
	}

	@Override
	public void removeColorDefinitionListener(String name, Consumer<ColorDefinition> listener)
	{
		colorDefinitionListeners.removeListener(name, listener);
	}

	private class ListenerManager<P>
	{
		private final Map<String, Set<Consumer<P>>> listenersPerName;
		private final IPropertyChangeListener eclipseListener;

		public ListenerManager(Function<String, P> preferenceGetter)
		{
			this.listenersPerName = new HashMap<>();
			this.eclipseListener = e ->
			{
				String name = e.getProperty();
				synchronized (listenersPerName)
				{
					Set<Consumer<P>> listenersThisName = listenersPerName.get(name);
					if (listenersThisName != null)
					{
						P p = preferenceGetter.apply(name);
						listenersThisName.forEach(l -> l.accept(p));
					}
				}
			};
		}

		public void addListener(String name, Consumer<P> listener)
		{
			synchronized (listenersPerName)
			{
				boolean wasEmpty = listenersPerName.isEmpty();
				listenersPerName.computeIfAbsent(name, n -> new HashSet<>()).add(listener);
				if (wasEmpty)
					prefs.addPropertyChangeListener(eclipseListener);
			}
		}

		public void removeListener(String name, Consumer<P> listener)
		{
			synchronized (listenersPerName)
			{
				Set<Consumer<P>> listenersThisName = listenersPerName.get(name);
				if (listenersThisName != null)
				{
					listenersThisName.remove(listener);
					if (listenersThisName.isEmpty())
						listenersPerName.remove(name);
				}
				if (listenersPerName.isEmpty())
					prefs.addPropertyChangeListener(eclipseListener);
			}
		}
	}
}