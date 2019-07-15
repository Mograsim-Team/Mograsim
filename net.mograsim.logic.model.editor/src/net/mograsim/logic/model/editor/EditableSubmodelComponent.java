package net.mograsim.logic.model.editor;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.preferences.Preferences;

public class EditableSubmodelComponent extends DeserializedSubmodelComponent
{
	private static final double labelFontHeight = 5;
	private static final double pinNameFontHeight = 3;
	private String label;

	public EditableSubmodelComponent(ViewModelModifiable model, String label)
	{
		super(model, label, null, null); //TODO: set name properly
		this.label = label;
		setSubmodelScale(0.2);
		addSubmodelInterface(new MovablePin(this, "A Pin", 1, 0, 10));

	}

	public ViewModelModifiable getSubmodelModifiable()
	{
		return submodelModifiable;
	}

	@Override
	protected void renderOutline(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
	}

	@Override
	protected void renderSymbol(GeneralGC gc, Rectangle visibleRegion)
	{
		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), labelFontHeight, oldFont.getStyle()));
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (getWidth() - textExtent.x) / 2, getPosY() + (getHeight() - textExtent.y) / 2,
				true);
		gc.setFont(new Font(oldFont.getName(), pinNameFontHeight, oldFont.getStyle()));
		for (String name : pinsUnmodifiable.keySet())
		{
			Pin p = pinsUnmodifiable.get(name);
			Point pos = p.getPos();
			gc.drawText(name, pos.x, pos.y, true);
		}
		gc.setFont(oldFont);
	}

	public void setSubmodelScale(double scale)
	{
		super.setSubmodelScale(scale);
	}

	public double getSubmodelScale()
	{
		return super.getSubmodelScale();
	}

	public void setSize(double width, double height)
	{
		super.setSize(width, height);
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
