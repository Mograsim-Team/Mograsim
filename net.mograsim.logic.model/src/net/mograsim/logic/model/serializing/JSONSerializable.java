package net.mograsim.logic.model.serializing;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.util.JsonHandler;

public interface JSONSerializable
{
	public String getIDForSerializing(IdentifyParams idParams);

	public Object getParamsForSerializing(IdentifyParams idParams);

	public default JsonElement getParamsForSerializingJSON(IdentifyParams idParams)
	{
		return JsonHandler.toJsonTree(getParamsForSerializing(idParams));
	}
}