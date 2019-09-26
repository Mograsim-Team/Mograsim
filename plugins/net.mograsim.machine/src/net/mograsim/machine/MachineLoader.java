package net.mograsim.machine;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import net.mograsim.logic.model.serializing.ClassLoaderBasedResourceLoader;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class MachineLoader implements BundleActivator
{
	private static AtomicBoolean activated = new AtomicBoolean(false);

	@Override
	public void start(BundleContext context) throws Exception
	{
		setup();
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		// nothing
	}

	public static void setup()
	{
		if (activated.getAndSet(true))
			return;
		ClassLoaderBasedResourceLoader resourceLoader = ClassLoaderBasedResourceLoader.create(MachineLoader.class.getClassLoader());
		IndirectModelComponentCreator.registerResourceLoader(resourceLoader, "MachineLoader");
		IndirectModelComponentCreator.loadStandardComponentIDs(MachineLoader.class.getResourceAsStream("standardComponentIDMapping.json"));
	}
}
