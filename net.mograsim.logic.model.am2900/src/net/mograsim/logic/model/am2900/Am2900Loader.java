package net.mograsim.logic.model.am2900;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import net.mograsim.logic.model.serializing.ClassLoaderBasedResourceLoader;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class Am2900Loader implements BundleActivator
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
		ClassLoaderBasedResourceLoader resourceLoader = ClassLoaderBasedResourceLoader.create(Am2900Loader.class.getClassLoader());
		IndirectGUIComponentCreator.registerResourceLoader(resourceLoader, "Am2900Loader");
		IndirectGUIComponentCreator.loadStandardComponentIDs(Am2900Loader.class.getResourceAsStream("standardComponentIDMapping.json"));
//		System.out.println("SETUP DONE"); // TODO: Debug
	}
}
