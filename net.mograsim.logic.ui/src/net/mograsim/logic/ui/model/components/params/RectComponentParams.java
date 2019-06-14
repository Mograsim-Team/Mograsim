package net.mograsim.logic.ui.model.components.params;

import java.io.IOException;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class RectComponentParams
{
	public final static String fileExtension = ".rc";
	public String displayName;
	public int inputCount, outputCount, logicWidth;
	public GeneralComponentParams composition;

	public static class InnerComponentParams
	{
		public Point pos;
		public String type;
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

	public static RectComponentParams readJson(String path) throws IOException
	{
		return JsonHandler.readJson(path, RectComponentParams.class);
	}

	/**
	 * Writes this {@link RectComponentParams} object into a file in json format. The correct file extension is important! Check
	 * {@link RectComponentParams}.fileExtension
	 */
	public void writeJson(String path)
	{
		try
		{
			JsonHandler.writeJson(this, path);
		}
		catch (IOException e)
		{
			System.err.println("Failed to write RectComponentParams to file");
			e.printStackTrace();
		}
	}
}
