package net.mograsim.logic.model.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler
{
	// TODO: write versions differently
	private static Gson parser = new GsonBuilder().setPrettyPrinting().create();

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
		try (InputStreamReader reader = new InputStreamReader(input); BufferedReader bf = new BufferedReader(reader))
		{
			return fromJson(bf.lines().collect(Collectors.joining("\n")), type);
		}
	}

	public static <T> T fromJson(String src, Class<T> type)
	{
		// TODO actually parse and compare version
		String rawJson = src.lines().dropWhile(s -> s.length() == 0 || s.charAt(0) != '{').collect(Collectors.joining());
		return parser.fromJson(rawJson, type);
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
		return String.format("mograsim version: %s\n%s", Version.jsonCompVersion.toString(), parser.toJson(o));
	}
}
