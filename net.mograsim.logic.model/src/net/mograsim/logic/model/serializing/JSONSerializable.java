package net.mograsim.logic.model.serializing;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.util.JsonHandler;

public interface JSONSerializable
{
	public Object getParamsForSerializing(IdentifierGetter idGetter);

	public default JsonElement getParamsForSerializingJSON(IdentifierGetter idGetter)
	{
		return JsonHandler.toJsonTree(getParamsForSerializing(idGetter));
	}
}