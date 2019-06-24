package net.mograsim.logic.ui.model.components;

import java.io.IOException;
import java.util.Map;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.util.JsonHandler;

/**
 * This class contains all the information necessary to create a new {@link SubmodelComponent}
 */
public class SubmodelComponentParams
{
	String type, name;
	double width, height;
	InterfacePinParams[] interfacePins;
	ComponentCompositionParams composition;
	Map<String, Object> specialized;

	public static class InterfacePinParams
	{
		Point location;
		String name;
		int logicWidth;
	}

	public static class InnerWireParams
	{
		InnerPinParams pin1, pin2;
		Point[] path;
	}

	public static class InnerPinParams
	{
		int compId;
		String pinName;
	}

	public static class ComponentCompositionParams
	{
		double innerScale;
		InnerComponentParams[] subComps;
		InnerWireParams[] innerWires;

		public static class InnerComponentParams
		{
			Point pos;
			String name;
			Map<String, Object> params;
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