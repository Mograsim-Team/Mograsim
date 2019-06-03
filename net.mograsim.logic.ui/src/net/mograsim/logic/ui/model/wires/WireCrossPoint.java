package net.mograsim.logic.ui.model.wires;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.ui.ColorHelper;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;

public class WireCrossPoint extends GUIComponent
{
	private final Pin pin;
	private final int logicWidth;

	private final LogicObserver logicObs;
	private ReadEnd end;

	public WireCrossPoint(ViewModelModifiable model, int logicWidth)
	{
		super(model);
		logicObs = (i) -> requestRedraw();

		this.logicWidth = logicWidth;
		setSize(0, 0);
		addPin(this.pin = new Pin(this, logicWidth, 0, 0));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Rectangle bounds = getBounds();
		ColorHelper.executeWithDifferentBackground(gc, BitVectorFormatter.formatAsColor(end),
				() -> gc.fillOval(bounds.x - 1, bounds.y - 1, 2, 2));
	}

	public void setLogicModelBinding(ReadEnd end)
	{
		deregisterLogicObs(this.end);
		this.end = end;
		registerLogicObs(end);
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

	public int getLogicWidth()
	{
		return logicWidth;
	}

	public Pin getPin()
	{
		return pin;
	}
}