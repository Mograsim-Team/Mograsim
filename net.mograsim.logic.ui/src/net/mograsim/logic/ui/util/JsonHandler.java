package net.mograsim.logic.ui.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler
{
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
			String json = bf.lines().dropWhile(s -> s.length() == 0 || s.charAt(0) != '{').reduce("", (x, y) -> x.concat(y));
			T params = parser.fromJson(json, type);
			return params;
		}
	}

	public static void writeJson(Object o, String path) throws IOException
	{
		try (FileWriter writer = new FileWriter(path))
		{
			writer.write(String.format("mograsim version: %s\n", Version.jsonCompVersion.toString()));
			writer.write(parser.toJson(o));
		}
	}
}
