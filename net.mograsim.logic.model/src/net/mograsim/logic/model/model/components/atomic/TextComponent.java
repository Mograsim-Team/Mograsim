package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.NoLogicAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.Preferences;

//TODO clean size calculation mess
public class TextComponent extends GUIComponent
{
	private final String text;

	public TextComponent(ViewModelModifiable model, String text)
	{
		this(model, text, null);
	}

	public TextComponent(ViewModelModifiable model, String text, String name)
	{
		super(model, name);
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

	// serializing

	@Override
	public JsonElement getParamsForSerializing()
	{
		return new JsonPrimitive(text);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new NoLogicAdapter<>(TextComponent.class));
		IndirectGUIComponentCreator.setComponentSupplier(TextComponent.class.getName(),
				(m, p, n) -> new TextComponent(m, p.getAsString(), n));
	}
}
