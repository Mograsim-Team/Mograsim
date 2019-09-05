package net.mograsim.logic.model.serializing;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.Renderer;

public class DeserializedSubmodelComponent extends SubmodelComponent
{
	/**
	 * If a DeserializedSubmodelComponent is part of another SubmodelComponent, when it it serialized, it should not return its internal
	 * structure, but rather the component ID used to create it.
	 * 
	 * @see SubmodelComponentSerializer#deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)
	 *      SubmodelComponentSerializer.deserialize(...)
	 * @see SubmodelComponentSerializer#serialize(SubmodelComponent, java.util.function.Function) SubmodelComponentSerializer.serialize(...)
	 */
	public final String idForSerializingOverride;
	/**
	 * See {@link #idForSerializingOverride}
	 */
	public final JsonElement paramsForSerializingOverride;

	public DeserializedSubmodelComponent(LogicModelModifiable model, String name, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride)
	{
		super(model, name, false);
		this.idForSerializingOverride = idForSerializingOverride;
		this.paramsForSerializingOverride = paramsForSerializingOverride;
		init();
	}

	@Override
	public void setSymbolRenderer(Renderer symbolRenderer)
	{
		super.setSymbolRenderer(symbolRenderer);
	}

	@Override
	public void setOutlineRenderer(Renderer outlineRenderer)
	{
		super.setOutlineRenderer(outlineRenderer);
	}

	@Override
	public void setHighLevelStateHandler(HighLevelStateHandler handler)
	{
		super.setHighLevelStateHandler(handler);
	}

	public LogicModelModifiable getSubmodelModifiable()
	{
		return submodelModifiable;
	}

	@Override
	public void setSubmodelScale(double submodelScale)
	{
		super.setSubmodelScale(submodelScale);
	}

	@Override
	public void setSize(double width, double height)
	{
		super.setSize(width, height);
	}

	@Override
	public Pin addSubmodelInterface(MovablePin supermodelPin)
	{
		return super.addSubmodelInterface(supermodelPin);
	}

	@Override
	public void removeSubmodelInterface(String name)
	{
		super.removeSubmodelInterface(name);
	}
}