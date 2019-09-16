package net.mograsim.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import net.mograsim.machine.MachineRegistry;

public final class MograsimActivator extends AbstractUIPlugin
{
	private static MograsimActivator instance;

	public MograsimActivator()
	{
		if (instance != null)
			throw new IllegalStateException("MograsimActivator already created!");
		instance = this;
		MachineRegistry.reload();
	}

	public static MograsimActivator instance()
	{
		if (instance == null)
			throw new IllegalStateException("MograsimActivator not yet created!");
		return instance;
	}
}