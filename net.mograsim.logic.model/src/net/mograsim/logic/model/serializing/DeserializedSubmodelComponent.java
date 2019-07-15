package net.mograsim.logic.model.serializing;

import java.util.Map;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.Renderer;

//TODO serialize handlers
public class DeserializedSubmodelComponent extends SubmodelComponent
{
	/**
	 * If a DeserializedSubmodelComponent is part of another SubmodelComponent, when it it serialized, it should not return its internal
	 * structure, but rather the component ID used to create it.
	 * 
	 * @see SubmodelComponentSerializer#deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)
	 *      SubmodelComponentSerializer.deserialize(...)
	 * @see SubmodelComponentSerializer#serialize(SubmodelComponent, java.util.function.Function) SubmodelComponentSerializer.serialize(...)
	 */
	public final String idForSerializingOverride;
	/**
	 * See {@link #idForSerializingOverride}
	 */
	public final JsonElement paramsForSerializingOverride;

	private Renderer outlineRenderer;
	private Renderer symbolRenderer;
	private HighLevelStateHandler highLevelStateHandler;

	public DeserializedSubmodelComponent(ViewModelModifiable model, String name, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride)
	{
		super(model, name);
		this.idForSerializingOverride = idForSerializingOverride;
		this.paramsForSerializingOverride = paramsForSerializingOverride;
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		return highLevelStateHandler.getHighLevelState(stateID);
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		highLevelStateHandler.setHighLevelState(stateID, newState);
	}

	@Override
	protected void renderOutline(GeneralGC gc, Rectangle visibleRegion)
	{
		if (outlineRenderer != null)
			outlineRenderer.render(gc, visibleRegion);
	}

	@Override
	protected void renderSymbol(GeneralGC gc, Rectangle visibleRegion)
	{
		if (symbolRenderer != null)
			symbolRenderer.render(gc, visibleRegion);
	}

	public void setOutlineRenderer(Renderer outlineRenderer)
	{
		this.outlineRenderer = outlineRenderer;
	}

	public Renderer getOutlineRenderer()
	{
		return outlineRenderer;
	}

	public void setSymbolRenderer(Renderer symbolRenderer)
	{
		this.symbolRenderer = symbolRenderer;
	}

	public Renderer getSymbolRenderer()
	{
		return symbolRenderer;
	}

	public void setHighLevelStateHandler(HighLevelStateHandler highLevelStateHandler)
	{
		this.highLevelStateHandler = highLevelStateHandler;
	}

	public HighLevelStateHandler getHighLevelStateHandler()
	{
		return highLevelStateHandler;
	}

	public ViewModelModifiable getSubmodelModifiable()
	{
		return submodelModifiable;
	}

	@Override
	public double getSubmodelScale()
	{
		return super.getSubmodelScale();
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
	public Map<String, MovablePin> getSubmodelMovablePins()
	{
		return super.getSubmodelMovablePins();
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

	// TODO static initializer
}