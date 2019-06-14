package net.mograsim.logic.ui.model.components.params;

import java.io.IOException;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class SubComponentParams
{
	public final static String fileExtension = ".sc";
	public double width, height;
	public InterfacePinParams[] interfacePins;
	public GeneralComponentParams composition;

	public static class InterfacePinParams
	{
		public Point location;
		public int logicWidth;
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