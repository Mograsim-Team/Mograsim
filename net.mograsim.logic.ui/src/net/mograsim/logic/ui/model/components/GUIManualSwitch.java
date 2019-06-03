package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.wires.Pin;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public class GUIManualSwitch extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 15;
	private static final double fontHeight = 5;

	private final Pin outputPin;

	private final LogicObserver logicObs;
	private ManualSwitch logicSwitch;
	private ReadEnd end;

	public GUIManualSwitch(ViewModel model)
	{
		super(model);
		logicObs = (i) -> requestRedraw();

		setSize(width, height);
		addPin(this.outputPin = new Pin(this, 1, width, height / 2));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		// TODO maybe draw switch state too?
		gc.drawRectangle(posX, posY, width, height);
		String label = BitVectorFormatter.formatValueAsString(end);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (width - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	public void setLogicModelBinding(ManualSwitch logicSwitch, ReadEnd end)
	{
		deregisterLogicObs(this.end);
		deregisterLogicObs(this.logicSwitch);
		this.logicSwitch = logicSwitch;
		this.end = end;
		registerLogicObs(end);
		registerLogicObs(logicSwitch);
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

	@Override
	public boolean clicked(double x, double y)
	{
		if (logicSwitch != null)
			logicSwitch.toggle();
		return true;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}
}