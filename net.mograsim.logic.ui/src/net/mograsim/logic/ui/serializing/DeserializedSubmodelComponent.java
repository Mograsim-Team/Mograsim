package net.mograsim.logic.ui.serializing;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.serializing.snippets.Renderer;

public class DeserializedSubmodelComponent extends SubmodelComponent
{
	public Renderer outlineRenderer;
	public Renderer symbolRenderer;

	public DeserializedSubmodelComponent(ViewModelModifiable model)
	{
		super(model);
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