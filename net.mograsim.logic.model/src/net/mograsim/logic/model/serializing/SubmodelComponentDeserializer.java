package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.util.Map;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 */
public final class SubmodelComponentDeserializer
{
	/**
	 * Like {@link #create(ViewModelModifiable, String, String)}, but using the default name.
	 */
	public static SubmodelComponent create(ViewModelModifiable model, String path)
	{
		return create(model, path, null);
	}

	/**
	 * Creates a {@link SubmodelComponent} from the {@link SubmodelComponentParams} located at the given path as a JSON file. The returned
	 * SubmodelComponent is a {@link DeserializedSubmodelComponent}.
	 * 
	 * @param path The path of the file describing the {@link SubmodelComponentParams}, which define the new {@link SubmodelComponent}
	 * @return A new SubmodelComponent, as described in the file located at the given path
	 */
	public static SubmodelComponent create(ViewModelModifiable model, String path, String name)
	{
		try
		{
			SubmodelComponentParams params = SubmodelComponentParams.readJson(path);
			SubmodelComponent ret = create(model, params, name);
			return ret;
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to construct GUICustomComponent. Parameters were not found.", e);
		}
	}

	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams}. The returned SubmodelComponent is a
	 * {@link DeserializedSubmodelComponent}.
	 * 
	 * @param params The parameters describing the {@link SubmodelComponent}
	 * 
	 * @return A new SubmodelComponent, as described by the {@link SubmodelComponentParams}
	 */
	public static SubmodelComponent create(ViewModelModifiable model, SubmodelComponentParams params, String name)
	{
		DeserializedSubmodelComponent comp = createSubmodelComponent(model, params, name);
		initSubmodel(comp, params.submodel);
		return comp;
	}

	private static DeserializedSubmodelComponent createSubmodelComponent(ViewModelModifiable model, SubmodelComponentParams params,
			String name)
	{
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model, name);
		comp.setSubmodelScale(params.submodel.innerScale);
		comp.setOutlineRenderer(CodeSnippetSupplier.outlineRendererSupplier.getSnippetSupplier(params.outlineRendererSnippetID).create(comp,
				params.outlineRendererParams));
		comp.setSymbolRenderer(CodeSnippetSupplier.symbolRendererSupplier.getSnippetSupplier(params.symbolRendererSnippetID).create(comp,
				params.symbolRendererParams));
		// TODO high level states
		comp.setSize(params.width, params.height);
		for (InterfacePinParams iPinParams : params.interfacePins)
			comp.addSubmodelInterface(
					new MovablePin(comp, iPinParams.name, iPinParams.logicWidth, iPinParams.location.x, iPinParams.location.y));
		return comp;
	}

	@SuppressWarnings("unused") // GUIWire being created
	private static void initSubmodel(DeserializedSubmodelComponent comp, SubmodelParameters params)
	{
		ViewModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		Map<String, GUIComponent> componentsByName = submodelModifiable.getComponentsByName();
		GUIComponent[] components = new GUIComponent[params.subComps.length];
		for (int i = 0; i < components.length; i++)
		{
			InnerComponentParams cParams = params.subComps[i];
			components[i] = IndirectGUIComponentCreator.createComponent(submodelModifiable, cParams.id, cParams.params, cParams.name);
			components[i].moveTo(cParams.pos.x, cParams.pos.y);
		}

		for (int i = 0; i < params.innerWires.length; i++)
		{
			InnerWireParams innerWire = params.innerWires[i];
			new GUIWire(submodelModifiable, componentsByName.get(innerWire.pin1.compName).getPin(innerWire.pin1.pinName),
					componentsByName.get(innerWire.pin2.compName).getPin(innerWire.pin2.pinName), innerWire.path);
		}
	}
}
