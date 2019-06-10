package net.mograsim.logic.ui.model.components;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class ComponentParams
{
	private static Gson parser = new GsonBuilder().setPrettyPrinting().create();
	String displayName;
	int inputCount, outputCount, logicWidth;
	double innerScale;

	InnerComponentParams[] subComps;
	InnerWireParams[] innerWires;

	public static class InnerComponentParams
	{
		Point pos;
		String type;
		int logicWidth;
	}

	public static class InnerWireParams
	{
		InnerPinParams pin1, pin2;
		Point[] path;
	}

	public static class InnerPinParams
	{
		int compId, pinIndex;
	}

	@SuppressWarnings("resource")
	public static ComponentParams readJSON(String path) throws IOException
	{
		FileReader reader = new FileReader(path);
		ComponentParams params = parser.fromJson(new FileReader(path), ComponentParams.class);
		reader.close();
		return params;
	}

	public void writeJSON(String path) throws IOException
	{
		@SuppressWarnings("resource")
		FileWriter writer = new FileWriter(path);
		writer.write(parser.toJson(this));
		writer.close();
	}
}
