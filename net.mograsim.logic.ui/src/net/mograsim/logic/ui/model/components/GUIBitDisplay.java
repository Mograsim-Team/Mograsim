package net.mograsim.logic.ui.model.components;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.BitDisplayAdapter;
import net.mograsim.preferences.Preferences;

public class GUIBitDisplay extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 15;
	private static final double fontHeight = 5;

	private final Pin inputPin;

	private final LogicObserver logicObs;
	private BitDisplay bitDisplay;

	public GUIBitDisplay(ViewModelModifiable model)
	{
		super(model);
		logicObs = (i) -> requestRedraw();

		setSize(width, height);
		addPin(this.inputPin = new Pin(this, "", 1, 0, height / 2));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.ui.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = bitDisplay == null ? BitVectorFormatter.formatAsString(null)
				: BitVectorFormatter.formatAsString(bitDisplay.getDisplayedValue());
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.ui.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (width - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	public void setLogicModelBinding(BitDisplay bitDisplay)
	{
		if (this.bitDisplay != null)
			this.bitDisplay.deregisterObserver(logicObs);
		this.bitDisplay = bitDisplay;
		if (bitDisplay != null)
			bitDisplay.registerObserver(logicObs);
	}

	public boolean hasLogicModelBinding()
	{
		return bitDisplay != null;
	}

	public BitDisplay getBitDisplay()
	{
		return bitDisplay;
	}

	public Pin getInputPin()
	{
		return inputPin;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new BitDisplayAdapter());
	}
}