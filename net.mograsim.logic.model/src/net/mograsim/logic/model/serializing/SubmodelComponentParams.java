package net.mograsim.logic.model.serializing;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.util.Version;

/**
 * This class contains all the information necessary to create a new {@link SubmodelComponent}
 */
public class SubmodelComponentParams extends SerializablePojo
{
	// basic stuff
	public double width, height;
	public InterfacePinParams[] interfacePins;
	public double innerScale;
	public ViewModelParams submodel;

	// functionality that needs to be expressed in Java code
	public String symbolRendererSnippetID;
	public JsonElement symbolRendererParams;

	public String outlineRendererSnippetID;
	public JsonElement outlineRendererParams;

	public String highLevelStateHandlerSnippetID;
	public JsonElement highLevelStateHandlerParams;

	public SubmodelComponentParams(Version version)
	{
		super(version);
	}

	public static class InterfacePinParams
	{
		public Point location;
		public String name;
		public int logicWidth;
	}
}