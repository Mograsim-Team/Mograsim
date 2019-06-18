package net.mograsim.logic.ui.model.components.params;

import java.io.IOException;
import java.util.Map;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.components.SubmodelComponent;

/**
 * This class contains all the information necessary to create a new {@link SubmodelComponent}
 */
public class SubmodelComponentParams
{
	public String type;
	public double width, height;
	public InterfacePinParams[] interfacePins;
	public ComponentCompositionParams composition;
	public Map<String, Object> specialized;

	public static class InterfacePinParams
	{
		public Point location;
		public int logicWidth;
	}

	public static class InnerWireParams
	{
		public InnerPinParams pin1, pin2;
		public Point[] path;
	}

	public static class InnerPinParams
	{
		public int compId, pinIndex;
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