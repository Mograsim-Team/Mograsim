package net.mograsim.logic.model.model.components.atomic;

import static net.mograsim.logic.model.preferences.RenderPreferences.TEXT_COLOR;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.NoLogicAdapter;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

//TODO clean size calculation mess
public class ModelTextComponent extends ModelComponent
{
	private final String text;
	private boolean calculatedSize;

	public ModelTextComponent(LogicModelModifiable model, String text)
	{
		this(model, text, null);
	}

	public ModelTextComponent(LogicModelModifiable model, String text, String name)
	{
		super(model, name, false);
		this.text = text;
		// If size is unset, it defaults to 0, which could prohibit this component from being rendered, which would prohibit the size being
		// set to a better guess
		setSize(1, 1);

		init();
	}

	@Override
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		if (!calculatedSize)
		{
			calculatedSize = true;
			Point textExtent = gc.textExtent(text);
			setSize(textExtent.x, textExtent.y);
		}

		Color textColor = renderPrefs.getColor(TEXT_COLOR);
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(text, getPosX(), getPosY(), true);
	}

	// serializing

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "TextComponent";
	}

	@Override
	public String getParamsForSerializing(IdentifyParams idParams)
	{
		return text;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new NoLogicAdapter<>(ModelTextComponent.class));
		IndirectModelComponentCreator.setComponentSupplier(ModelTextComponent.class.getName(),
				(m, p, n) -> new ModelTextComponent(m, p.getAsString(), n));
	}
}
