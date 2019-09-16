package net.mograsim.logic.model.serializing;

import net.mograsim.logic.model.util.Version;

/**
 * This class is the superclass of all POJOs that can be serialized to JSON.
 * 
 * @author Daniel Kirschten
 *
 */
public class SerializablePojo
{
	public Version version;

	public SerializablePojo(Version version)
	{
		this.version = version;
	}
}