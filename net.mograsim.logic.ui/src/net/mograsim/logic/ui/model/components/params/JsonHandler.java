package net.mograsim.logic.ui.model.components.params;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler
{
	private static Gson parser = new GsonBuilder().setPrettyPrinting().create();

	@SuppressWarnings("resource")
	public static <T> T readJson(String path, Class<T> type) throws IOException
	{
		FileReader reader = new FileReader(path);
		T params = parser.fromJson(new FileReader(path), type);
		reader.close();
		return params;
	}

	public static void writeJson(Object o, String path) throws IOException
	{
		@SuppressWarnings("resource")
		FileWriter writer = new FileWriter(path);
		writer.write(parser.toJson(o));
		writer.close(); // TODO: Insure that writer is closed
	}
}
