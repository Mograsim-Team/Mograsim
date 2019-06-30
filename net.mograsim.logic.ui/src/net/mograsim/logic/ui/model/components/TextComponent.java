package net.mograsim.logic.ui.model.components;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.NoLogicAdapter;
import net.mograsim.preferences.Preferences;

public class TextComponent extends GUIComponent
{
	private final String text;
	private Point textExtent;

	public TextComponent(ViewModelModifiable model, String text)
	{
		super(model);
		this.text = text;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		textExtent = gc.textExtent(text);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.ui.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(text, getPosX(), getPosY(), true);
	}

	@Override
	public Rectangle getBounds()
	{
		if (textExtent == null)
			return super.getBounds();
		return new Rectangle(getPosX(), getPosY(), textExtent.x, textExtent.y);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new NoLogicAdapter<>(TextComponent.class));
	}
}
