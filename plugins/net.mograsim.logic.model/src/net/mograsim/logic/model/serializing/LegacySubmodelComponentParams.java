package net.mograsim.logic.model.serializing;

import java.io.IOException;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.util.JsonHandler;

/**
 * This class contains all the information necessary to create a new {@link SubmodelComponent}
 */
public class LegacySubmodelComponentParams
{
	// basic stuff
	public double width, height;
	public LegacyInterfacePinParams[] interfacePins;
	public LegacySubmodelParameters submodel;

	// functionality that needs to be expressed in Java code
	public String symbolRendererSnippetID;
	public JsonElement symbolRendererParams;

	public String outlineRendererSnippetID;
	public JsonElement outlineRendererParams;

	public String highLevelStateHandlerSnippetID;
	public JsonElement highLevelStateHandlerParams;

	public static class LegacyInterfacePinParams
	{
		public Point location;
		public String name;
		public int logicWidth;
	}

	public static class LegacySubmodelParameters
	{
		public double innerScale;
		public LegacyInnerComponentParams[] subComps;
		public LegacyInnerWireParams[] innerWires;

		public static class LegacyInnerComponentParams
		{
			public String id;
			public String name;
			public Point pos;
			public JsonElement params;
		}

		public static class LegacyInnerWireParams
		{
			public LegacyInnerPinParams pin1, pin2;
			public String name;
			public Point[] path;

			public static class LegacyInnerPinParams
			{
				public String compName;
				public String pinName;
			}
		}
	}

	public static LegacySubmodelComponentParams readJson(String path) throws IOException
	{
		return JsonHandler.readJson(path, LegacySubmodelComponentParams.class);
	}

	/**
	 * Writes this {@link LegacySubmodelComponentParams} object into a file in json format. The correct file extension is important! Check
	 * {@link LegacySubmodelComponentParams}.fileExtension
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