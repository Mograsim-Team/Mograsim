package net.mograsim.logic.model.serializing;

public class ReflectionHelper
{
	private ReflectionHelper()
	{
	}

	public static Class<?> tryInvokeStaticInitializer(String className, String errorMessageFormat)
	{
		return tryInvokeStaticInitializer(className, errorMessageFormat, ReflectionHelper.class.getClassLoader());
	}

	public static Class<?> tryInvokeStaticInitializer(String className, String errorMessageFormat, ClassLoader classLoader)
	{
		try
		{
			return tryInvokeStaticInitializer(className, classLoader);
		}
		catch (ClassNotFoundException e)
		{
			System.err.printf(errorMessageFormat, className, "ClassNotFoundException thrown: " + e.getMessage());
			return null;
		}
	}

	public static Class<?> tryInvokeStaticInitializer(String className) throws ClassNotFoundException
	{
		return tryInvokeStaticInitializer(className, ReflectionHelper.class.getClassLoader());
	}

	public static Class<?> tryInvokeStaticInitializer(String className, ClassLoader classLoader) throws ClassNotFoundException
	{
		return Class.forName(className, true, classLoader);
	}
}