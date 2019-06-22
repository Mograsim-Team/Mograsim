package net.mograsim.logic.ui.model.wires;

import java.util.Map;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.ui.ColorHelper;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.SimpleRectangularGUIGate;

public class WireCrossPoint extends GUIComponent
{
	private static final int CIRCLE_RADIUS = 1;
	private static final int CIRCLE_DIAM = CIRCLE_RADIUS * 2;

	private final Pin pin;
	private final int logicWidth;

	private final LogicObserver logicObs;
	private ReadEnd end;

	public WireCrossPoint(ViewModelModifiable model, int logicWidth)
	{
		super(model);
		logicObs = (i) -> requestRedraw();

		this.logicWidth = logicWidth;
		setSize(CIRCLE_DIAM, CIRCLE_DIAM);
		addPin(this.pin = new Pin(this, "", logicWidth, CIRCLE_RADIUS, CIRCLE_RADIUS));
	}

	public void moveCenterTo(double x, double y)
	{
		moveTo(x - CIRCLE_RADIUS, y - CIRCLE_RADIUS);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		ColorHelper.executeWithDifferentBackground(gc, BitVectorFormatter.formatAsColor(end),
				() -> gc.fillOval(getPosX(), getPosY(), CIRCLE_DIAM, CIRCLE_DIAM));
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

	@Override
	public Map<String, Object> getInstantiationParameters()
	{
		Map<String, Object> m = super.getInstantiationParameters();
		m.put(SimpleRectangularGUIGate.kLogicWidth, logicWidth);
		return m;
	}

}