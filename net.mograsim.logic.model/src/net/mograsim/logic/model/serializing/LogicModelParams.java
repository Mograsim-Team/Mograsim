package net.mograsim.logic.model.serializing;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.util.Version;

public class LogicModelParams extends SerializablePojo
{
	public ComponentParams[] components;
	public WireParams[] wires;

	public LogicModelParams(Version version)
	{
		super(version);
	}

	public static class ComponentParams
	{
		public String id;
		public String name;
		public Point pos;
		public JsonElement params;
	}

	public static class WireParams
	{
		public PinParams pin1, pin2;
		public String name;
		public Point[] path;

		public static class PinParams
		{
			public String compName;
			public String pinName;
		}
	}
}