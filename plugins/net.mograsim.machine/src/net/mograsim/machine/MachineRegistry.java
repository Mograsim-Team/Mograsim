package net.mograsim.machine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

public class MachineRegistry
{
	private static final String MACHINE_EXT_ID = "net.mograsim.machine.machine_definition";

	private static final Map<String, MachineDefinition> installedMachines = new HashMap<>();

	private static void reload()
	{
		installedMachines.clear();
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

	public static void initialize()
	{
		reload();
		Platform.getExtensionRegistry().addListener(new IRegistryEventListener()
		{

			@Override
			public void removed(IExtensionPoint[] extensionPoints)
			{
				// nothing?
			}

			@Override
			public void removed(IExtension[] extensions)
			{
				reload();
			}

			@Override
			public void added(IExtensionPoint[] extensionPoints)
			{
				// nothing?
			}

			@Override
			public void added(IExtension[] extensions)
			{
				reload();
			}
		}, MACHINE_EXT_ID);
	}

	public static Map<String, MachineDefinition> getInstalledMachines()
	{
		return Collections.unmodifiableMap(installedMachines);
	}

	public static MachineDefinition getMachine(String id)
	{
		return installedMachines.get(id);
	}
}
