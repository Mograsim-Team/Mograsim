package net.mograsim.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import net.mograsim.machine.MachineRegistry;

public final class MograsimActivator extends AbstractUIPlugin
{

	public MograsimActivator()
	{
		MachineRegistry.reload();
	}
}
