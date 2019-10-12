package net.mograsim.logic.model.model.components.atomic;

import java.util.Objects;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.FixedOutputAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.preferences.Preferences;

public class ModelFixedOutput extends ModelComponent
{
	private static final double width = 20;
	private static final double height = 20;
	private static final double fontHeight = 5;

	public final BitVector bits;

	public ModelFixedOutput(LogicModelModifiable model, BitVector bits, String name)
	{
		super(model, name, false);
		this.bits = bits;
		setSize(width, height);
		addPin(new Pin(model, this, "out", bits.length(), PinUsage.OUTPUT, width, height / 2));

		init();
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "FixedOutput";
	}

	@Override
	public Object getParamsForSerializing(IdentifyParams idParams)
	{
		return bits;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = BitVectorFormatter.formatAsString(bits, false);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (width - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new FixedOutputAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelFixedOutput.class.getCanonicalName(),
				(m, p, n) -> new ModelFixedOutput(m, Objects.requireNonNull(JsonHandler.fromJsonTree(p, BitVector.class)), n));
	}
}