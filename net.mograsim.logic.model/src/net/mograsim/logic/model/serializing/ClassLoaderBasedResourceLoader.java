package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;

public abstract class ClassLoaderBasedResourceLoader implements ResourceLoader
{
	@Override
	public InputStream loadResource(String path) throws IOException
	{
		return getClassLoader().getResourceAsStream(path);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return ReflectionHelper.tryInvokeStaticInitializer(name, getClassLoader());
	}

	public abstract ClassLoader getClassLoader();

	public static ClassLoaderBasedResourceLoader create(ClassLoader loader)
	{
		return new ClassLoaderBasedResourceLoader()
		{
			@Override
			public ClassLoader getClassLoader()
			{
				return loader;
			}
		};
	}
}