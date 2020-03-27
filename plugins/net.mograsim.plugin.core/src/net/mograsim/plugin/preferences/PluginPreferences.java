package net.mograsim.plugin.preferences;

import net.mograsim.preferences.Preferences;

public interface PluginPreferences extends Preferences
{
	public static final String PREFIX = "net.mograsim.plugin.core.";

	public static final String MAX_MEMORY_CHANGE_INTERVAL = "net.mograsim.plugin.core.maxmemchangeinterval";
	public static final String SIMULATION_SPEED_PRECISION = "net.mograsim.plugin.core.simspeedprecision";
	public static final String MPM_EDITOR_BITS_AS_COLUMN_NAME = "net.mograsim.plugin.core.editors.mpm.bitsascolumnname";

}
