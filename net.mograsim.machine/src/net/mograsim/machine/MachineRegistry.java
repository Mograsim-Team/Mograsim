package net.mograsim.machine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.e4.core.di.annotations.Execute;

public class MachineRegistry
{
	private static final String MACHINE_EXT_ID = "net.mograsim.machine.machinedefinition";

	private static Set<MachineDefinition> installedMachines = new HashSet<>();

	@Execute
	public void execute(IExtensionRegistry registry)
	{
		System.out.println(Arrays.toString(registry.getExtensionPoints("net.mograsim.machine")));
		IConfigurationElement[] config = registry.getConfigurationElementsFor(MACHINE_EXT_ID);
		try
		{
			for (IConfigurationElement e : config)
			{
				final Object o = e.createExecutableExtension("class");
				if (o instanceof MachineDefinition)
				{
					executeExtension(o);
				} else
				{
					System.err.println("Invalid machine definition: " + o);
				}
			}
		}
		catch (CoreException ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	private void executeExtension(final Object o)
	{
		ISafeRunnable runnable = new ISafeRunnable()
		{
			@Override
			public void handleException(Throwable e)
			{
				System.out.println("Exception in client");
			}

			@Override
			public void run() throws Exception
			{
				System.out.println(((MachineDefinition) o).getAddressBits());
			}
		};
		SafeRunner.run(runnable);
	}

	public static Set<MachineDefinition> getinstalledMachines()
	{
		return Collections.unmodifiableSet(installedMachines);
	}
}
