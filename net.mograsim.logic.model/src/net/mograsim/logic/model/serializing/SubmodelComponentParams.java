package net.mograsim.logic.model.serializing;

import java.io.IOException;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.util.JsonHandler;

/**
 * This class contains all the information necessary to create a new {@link SubmodelComponent}
 */
public class SubmodelComponentParams
{
	// basic stuff
	public double width, height;
	public InterfacePinParams[] interfacePins;
	public SubmodelParameters submodel;

	// functionality that needs to be expressed in Java code
	public String outlineRendererSnippetID;
	public JsonElement outlineRendererParams;

	public String symbolRendererSnippetID;
	public JsonElement symbolRendererParams;

	public String highLevelStateHandlerSnippetID;
	public JsonElement highLevelStateHandlerParams;

	public static class InterfacePinParams
	{
		public Point location;
		public String name;
		public int logicWidth;
	}

	public static class SubmodelParameters
	{
		public double innerScale;
		public InnerComponentParams[] subComps;
		public InnerWireParams[] innerWires;

		public static class InnerComponentParams
		{
			public Point pos;
			public String id;
			public String name;
			public JsonElement params;
		}

		public static class InnerWireParams
		{
			public InnerPinParams pin1, pin2;
			public Point[] path;

			public static class InnerPinParams
			{
				public String compName;
				public String pinName;
			}
		}
	}

	public static SubmodelComponentParams readJson(String path) throws IOException
	{
		return JsonHandler.readJson(path, SubmodelComponentParams.class);
	}

	/**
	 * Writes this {@link SubmodelComponentParams} object into a file in json format. The correct file extension is important! Check
	 * {@link SubmodelComponentParams}.fileExtension
	 */
	public void writeJson(String path)
	{
		try
		{
			JsonHandler.writeJson(this, path);
		}
		catch (IOException e)
		{
			System.err.println("Failed to write SubComponentParams to file");
			e.printStackTrace();
		}
	}
}