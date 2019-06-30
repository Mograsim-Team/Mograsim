package net.mograsim.logic.ui.model.components;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.NoLogicAdapter;
import net.mograsim.preferences.Preferences;

//TODO clean size calculation mess
public class TextComponent extends GUIComponent
{
	private final String text;

	public TextComponent(ViewModelModifiable model, String text)
	{
		super(model);
		this.text = text;
		// If size is unset, it defaults to 0, which could prohibit this component from being rendered, which would prohibit the size being
		// set to a better guess
		setSize(1, 1);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Point textExtent = gc.textExtent(text);
		setSize(textExtent.x, textExtent.y);

		Color textColor = Preferences.current().getColor("net.mograsim.logic.ui.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(text, getPosX(), getPosY(), true);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new NoLogicAdapter<>(TextComponent.class));
	}
}
