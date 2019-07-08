package net.mograsim.logic.ui.serializing;

import java.io.IOException;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.SubmodelParameters;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.SubmodelParameters.InnerComponentParams;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 */
public final class SubmodelComponentDeserializer
{
	/**
	 * Creates a {@link SubmodelComponent} from the {@link SubmodelComponentParams}, specified at the given path. The returned
	 * SubmodelComponent can also be e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the
	 * {@link SubmodelComponentParams} describe.
	 * 
	 * @param path The path of the file describing the {@link SubmodelComponentParams}, which define the new {@link SubmodelComponent}
	 * @return A new SubmodelComponent, as described in the file located at the given path
	 */
	public static SubmodelComponent create(ViewModelModifiable model, String path)
	{
		try
		{
			SubmodelComponentParams params = SubmodelComponentParams.readJson(path);
			SubmodelComponent ret = create(model, params);
			return ret;
		}
		catch (IOException e)
		{
			System.err.println("Failed to construct GUICustomComponent. Parameters were not found.");
			e.printStackTrace();
		}
		return new SimpleRectangularSubmodelComponent(model, 0, "ERROR");
	}

	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams}. The returned SubmodelComponent can also be
	 * e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the {@link SubmodelComponentParams} describe.
	 * 
	 * @param params The parameters describing the {@link SubmodelComponent}
	 * 
	 * @return A new SubmodelComponent, as described by the {@link SubmodelComponentParams}
	 */
	public static SubmodelComponent create(ViewModelModifiable model, SubmodelComponentParams params)
	{
		DeserializedSubmodelComponent comp = createSubmodelComponent(model, params);
		initSubmodel(comp, params.submodel);
		return comp;
	}

	private static DeserializedSubmodelComponent createSubmodelComponent(ViewModelModifiable model, SubmodelComponentParams params)
	{
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model);
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

	@SuppressWarnings("unused")
	private static void initSubmodel(DeserializedSubmodelComponent comp, SubmodelParameters params)
	{
		GUIComponent[] components = new GUIComponent[params.subComps.length];
		for (int i = 0; i < components.length; i++)
		{
			InnerComponentParams cParams = params.subComps[i];
			String path = cParams.id;
			components[i] = IndirectGUIComponentCreator.createComponent(comp.getSubmodelModifiable(), cParams.id, cParams.params);
			components[i].moveTo(cParams.pos.x, cParams.pos.y);
		}

		for (int i = 0; i < params.innerWires.length; i++)
		{
			InnerWireParams innerWire = params.innerWires[i];
			new GUIWire(comp.getSubmodelModifiable(),
					comp.getSubmodelModifiable().getComponents().get(innerWire.pin1.compId).getPin(innerWire.pin1.pinName),
					comp.getSubmodelModifiable().getComponents().get(innerWire.pin2.compId).getPin(innerWire.pin2.pinName), innerWire.path);
		}
	}
}
