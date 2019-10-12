package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.BitDisplayAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.TextRenderingHelper;
import net.mograsim.preferences.Preferences;

public class ModelBitDisplay extends ModelComponent
{
	private static final double width = 20;
	private static final double height = 10;
	private static final double fontHeight = 5;
	private static final double textMargin = 0.5;

	public final int logicWidth;
	private final Pin inputPin;

	private final LogicObserver logicObs;
	private CoreBitDisplay bitDisplay;

	public ModelBitDisplay(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelBitDisplay(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, name, false);
		this.logicWidth = logicWidth;
		logicObs = (i) -> model.requestRedraw();

		setSize(width, height);
		addPin(this.inputPin = new Pin(model, this, "", logicWidth, PinUsage.INPUT, 0, height / 2));

		init();
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = BitVectorFormatter.formatAsString(bitDisplay == null ? null : bitDisplay.getDisplayedValue(), true);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		TextRenderingHelper.drawTextFitting(gc, label, getBounds(), textMargin, true);
		gc.setFont(oldFont);
	}

	public void setCoreModelBinding(CoreBitDisplay bitDisplay)
	{
		if (this.bitDisplay != null)
			this.bitDisplay.deregisterObserver(logicObs);
		this.bitDisplay = bitDisplay;
		if (bitDisplay != null)
			bitDisplay.registerObserver(logicObs);
	}

	public boolean hasCoreModelBinding()
	{
		return bitDisplay != null;
	}

	public CoreBitDisplay getBitDisplay()
	{
		return bitDisplay;
	}

	public Pin getInputPin()
	{
		return inputPin;
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "BitDisplay";
	}

	@Override
	public Integer getParamsForSerializing(IdentifyParams idParams)
	{
		return logicWidth;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new BitDisplayAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelBitDisplay.class.getCanonicalName(),
				(m, p, n) -> new ModelBitDisplay(m, p.getAsInt(), n));
	}
}