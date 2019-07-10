package net.mograsim.logic.model.serializing;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.snippets.Renderer;

public class DeserializedSubmodelComponent extends SubmodelComponent
{
	public Renderer outlineRenderer;
	public Renderer symbolRenderer;

	public DeserializedSubmodelComponent(ViewModelModifiable model, String name)
	{
		super(model, name);
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

	public void setSymbolRenderer(Renderer symbolRenderer)
	{
		this.symbolRenderer = symbolRenderer;
	}

	public ViewModelModifiable getSubmodelModifiable()
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
	protected Pin addSubmodelInterface(MovablePin supermodelPin)
	{
		return super.addSubmodelInterface(supermodelPin);
	}
}