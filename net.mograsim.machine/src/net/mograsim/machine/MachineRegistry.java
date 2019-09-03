package net.mograsim.machine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class MachineRegistry
{
	private static final String MACHINE_EXT_ID = "net.mograsim.machine.machine_definition";

	private static Map<String, MachineDefinition> installedMachines = new HashMap<>();

	public static void reload()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		System.out.println(Arrays.toString(registry.getExtensionPoints("net.mograsim.machine")));
		IConfigurationElement[] config = registry.getConfigurationElementsFor(MACHINE_EXT_ID);
		try
		{
			for (IConfigurationElement e : config)
			{
				System.out.println(e.getNamespaceIdentifier());
				System.out.println(Arrays.toString(e.getAttributeNames()));
				final Object o = e.createExecutableExtension("class");
				final String id = e.getAttribute("unique_id");
				if (o instanceof MachineDefinition) 
				{
					System.out.println("Found " + id);
					installedMachines.put(id, (MachineDefinition) o);
				} else
				{
					System.err.println("Invalid machine definition: " + o + "(id=" + id + "");
				}
			}
		}
		catch (CoreException ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	public static Map<String, MachineDefinition> getinstalledMachines()
	{
		return Collections.unmodifiableMap(installedMachines);
	}
}
