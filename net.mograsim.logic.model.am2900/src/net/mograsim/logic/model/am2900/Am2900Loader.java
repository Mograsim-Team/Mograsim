package net.mograsim.logic.model.am2900;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.serializing.ResourceLoader;

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
		IndirectGUIComponentCreator.registerResourceLoader(new Am2900ResourceLoader(), "Am2900Loader");
		IndirectGUIComponentCreator.loadStandardComponentIDs(Am2900Loader.class.getResourceAsStream("standardComponentIDMapping.json"));
//		System.out.println("SETUP DONE"); // TODO: Debug
	}

	/**
	 * @see ResourceLoader
	 */
	public static ResourceLoader resourceLoader()
	{
		return new Am2900ResourceLoader();
	}

	static class Am2900ResourceLoader implements ResourceLoader
	{
		@Override
		public InputStream loadResource(String path) throws IOException
		{
			return Am2900ResourceLoader.class.getResourceAsStream(path);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException
		{
			return Class.forName(name, true, Am2900ResourceLoader.class.getClassLoader());
		}
	}
}
