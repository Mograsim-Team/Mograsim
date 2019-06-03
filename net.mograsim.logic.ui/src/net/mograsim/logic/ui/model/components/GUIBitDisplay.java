package net.mograsim.logic.ui.model.components;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.wires.Pin;

public class GUIBitDisplay extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 15;
	private static final double fontHeight = 5;

	private final Pin inputPin;

	private final LogicObserver logicObs;
	private BitDisplay bitDisplay;

	public GUIBitDisplay(ViewModel model)
	{
		super(model);
		logicObs = (i) -> callComponentLookChangedListeners();

		setSize(width, height);
		addPin(this.inputPin = new Pin(this, 1, 0, height / 2));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		// TODO maybe draw switch state too?
		gc.drawRectangle(posX, posY, width, height);
		String label = bitDisplay == null ? BitVectorFormatter.formatAsString(null)
				: BitVectorFormatter.formatAsString(bitDisplay.getDisplayedValue());
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (width - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	public void setLogicModelBinding(BitDisplay bitDisplay)
	{
		deregisterLogicObs(this.bitDisplay);
		this.bitDisplay = bitDisplay;
		registerLogicObs(bitDisplay);
	}

	private void registerLogicObs(LogicObservable observable)
	{
		if (observable != null)
			observable.registerObserver(logicObs);
	}

	private void deregisterLogicObs(LogicObservable observable)
	{
		if (observable != null)
			observable.deregisterObserver(logicObs);
	}

	public Pin getInputPin()
	{
		return inputPin;
	}
}