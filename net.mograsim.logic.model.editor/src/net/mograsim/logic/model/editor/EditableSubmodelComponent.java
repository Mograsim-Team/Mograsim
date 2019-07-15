package net.mograsim.logic.model.editor;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;

public class EditableSubmodelComponent extends DeserializedSubmodelComponent
{
	private static final double labelFontHeight = 5;
	private static final double pinNameFontHeight = 3;
	private String label;

	public EditableSubmodelComponent(ViewModelModifiable model, String label)
	{
		super(model, label, null, null); // TODO: set name properly
		this.label = label;
		setSubmodelScale(0.2);
		addSubmodelInterface(new MovablePin(this, "A Pin", 1, 0, 10));
		updateSymbolRenderer();
		setOutlineRenderer(new DefaultOutlineRenderer(this));
	}

	private void updateSymbolRenderer()
	{
		SimpleRectangularLikeParams rendererParams = new SimpleRectangularLikeParams();
		rendererParams.centerText = label;
		rendererParams.centerTextHeight = labelFontHeight;
		rendererParams.horizontalComponentCenter = getWidth() / 2;
		rendererParams.pinLabelHeight = pinNameFontHeight;
		rendererParams.pinLabelMargin = 0;
		setSymbolRenderer(new SimpleRectangularLikeSymbolRenderer(this, rendererParams));
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
		updateSymbolRenderer();
	}
}
