package net.mograsim.logic.model.serializing;

import java.util.Map;

import net.mograsim.logic.model.util.Version;

public class StandardComponentIdMappingContainer extends SerializablePojo
{
	public Map<String, String> map;

	public StandardComponentIdMappingContainer(Version version)
	{
		super(version);
	}

	public Map<String, String> getMap()
	{
		return map;
	}
}
