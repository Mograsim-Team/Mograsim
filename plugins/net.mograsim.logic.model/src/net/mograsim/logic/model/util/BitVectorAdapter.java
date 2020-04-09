package net.mograsim.logic.model.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;

public class BitVectorAdapter implements JsonSerializer<BitVector>, JsonDeserializer<BitVector>
{
	@Override
	public JsonElement serialize(BitVector src, Type typeOfSrc, JsonSerializationContext context)
	{
		return new JsonPrimitive(src.toBitstring());
	}

	@Override
	public BitVector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if (!typeOfT.equals(BitVector.class))
			throw new JsonParseException("Type mismatch");
		if (json.isJsonPrimitive())
			// don't check for isString, because the "input" could consist solely of ones and zeroes.
			return BitVector.parseBitstring(json.getAsString());

		return BitVector.of(((BitVectorPrimitive) context.deserialize(json, BitVectorPrimitive.class)).bits);
	}

	private static class BitVectorPrimitive
	{
		public Bit[] bits;
	}
}
