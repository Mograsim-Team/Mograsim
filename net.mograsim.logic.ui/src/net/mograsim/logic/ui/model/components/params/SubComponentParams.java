package net.mograsim.logic.ui.model.components.params;

import java.io.IOException;
import java.util.Map;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class SubComponentParams
{
	public String type;
	public final static String fileExtension = ".sc";
	public double width, height;
	public InterfacePinParams[] interfacePins;
	public GeneralComponentParams composition;
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

	public static SubComponentParams readJson(String path) throws IOException
	{
		return JsonHandler.readJson(path, SubComponentParams.class);
	}

	/**
	 * Writes this {@link SubComponentParams} object into a file in json format. The correct file extension is important! Check
	 * {@link SubComponentParams}.fileExtension
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