package net.mograsim.logic.model.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.mograsim.logic.core.types.BitVector;

public class JsonHandler
{
	public final static Gson parser = new GsonBuilder().registerTypeAdapter(BitVector.class, new BitVectorAdapter()).setPrettyPrinting()
			.create();

	public static <T> T readJson(String path, Class<T> type) throws IOException
	{
		try (FileInputStream jsonStream = new FileInputStream(path))
		{
			return readJson(jsonStream, type);
		}
	}

	/**
	 * @param input The Stream is closed after being read
	 */
	public static <T> T readJson(InputStream input, Class<T> type) throws IOException
	{
		try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
				BufferedReader bf = new BufferedReader(reader))
		{
			return fromJson(bf.lines().collect(Collectors.joining("\n")), type);
		}
	}

	public static <T> T fromJson(String src, Class<T> type)
	{
		return parser.fromJson(src, type);
	}

	public static <T> T fromJson(JsonElement json, Class<T> type)
	{
		return parser.fromJson(json, type);
	}

	public static <T> T fromJsonTree(JsonElement src, Class<T> type)
	{
		return parser.fromJson(src, type);
	}

	public static void writeJson(Object o, String path) throws IOException
	{
		try (FileWriter writer = new FileWriter(path))
		{
			writer.write(toJson(o));
		}
	}

	public static String toJson(Object o)
	{
		return parser.toJson(o);
	}

	public static JsonElement toJsonTree(Object o)
	{
		return parser.toJsonTree(o);
	}
}