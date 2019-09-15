package net.mograsim.machine;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;

public abstract class ModelMemory extends ModelComponent
{
	private Renderer symbolRenderer;
	private Renderer outlineRenderer;
	protected final int width, height;

	protected ModelMemory(LogicModelModifiable model, int width, int height, String name, String centerText, boolean callInit)
	{
		super(model, name, false);
		this.width = width;
		this.height = height;

		SimpleRectangularLikeParams rendererParams = new SimpleRectangularLikeParams();
		rendererParams.centerText = centerText;
		rendererParams.centerTextHeight = 24;
		rendererParams.horizontalComponentCenter = width / 100;
		rendererParams.pinLabelHeight = 17.5;
		rendererParams.pinLabelMargin = 2.5;
		this.symbolRenderer = new SimpleRectangularLikeSymbolRenderer(this, rendererParams);
		this.outlineRenderer = new DefaultOutlineRenderer(this);

		setSize(width, height);

		if (callInit)
			init();
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		symbolRenderer.render(gc, visibleRegion);
		outlineRenderer.render(gc, visibleRegion);
	}
}
