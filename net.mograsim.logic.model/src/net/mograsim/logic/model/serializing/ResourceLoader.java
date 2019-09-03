package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;

import net.mograsim.logic.model.model.components.GUIComponent;

/**
 * For loading JSON {@link GUIComponent}s from other OSGI-Modules or jar-Files.
 * <p>
 * An implementation must conform to either of the following rules:
 * <ul>
 * <li>A ResourceLoader class can be referenced directly in <code>resource:qualifiedClassName:jsonResourcePath</code> and is then
 * instantiated using the public default constructor.</li>
 * <li>A class referenced in <code>resource:qualifiedClassName:jsonResourcePath</code> provides a public static method called
 * <b><code>resourceLoader</code></b> that returns an (non-null) object of the type {@link ResourceLoader}</li>
 * </ul>
 *
 */
public interface ResourceLoader
{
	InputStream loadResource(String path) throws IOException;
}
