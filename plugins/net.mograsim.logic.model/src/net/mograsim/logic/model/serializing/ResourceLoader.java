package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;

import net.mograsim.logic.model.model.components.ModelComponent;

/**
 * For loading JSON {@link ModelComponent}s from other OSGI-Modules or jar-Files.
 */
public interface ResourceLoader
{
	InputStream loadResource(String path) throws IOException;

	Class<?> loadClass(String name) throws ClassNotFoundException;
}
