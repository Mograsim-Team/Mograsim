package net.mograsim.plugin;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import net.mograsim.machine.MachineRegistry;
import net.mograsim.preferences.Preferences;

public final class MograsimActivator extends AbstractUIPlugin
{
	private static MograsimActivator instance;

	public MograsimActivator()
	{
		if (instance != null)
			throw new IllegalStateException("MograsimActivator already created!");
		instance = this;
		MachineRegistry.initialize();
		Preferences.setPreferences(new EclipsePreferences(PlatformUI.getWorkbench().getThemeManager().getCurrentTheme(),
				MograsimActivator.instance().getPreferenceStore()));
	}

	public static MograsimActivator instance()
	{
		if (instance == null)
			throw new IllegalStateException("MograsimActivator not yet created!");
		return instance;
	}
}