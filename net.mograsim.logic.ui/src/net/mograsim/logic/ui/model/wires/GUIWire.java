package net.mograsim.logic.ui.model.wires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.ui.ColorHelper;
import net.mograsim.logic.ui.model.ViewModelModifiable;

public class GUIWire
{
	private final ViewModelModifiable model;
	public final int logicWidth;
	private Pin pin1;
	private Pin pin2;
	private Point[] path;
	private final Rectangle bounds;
	private double[] effectivePath;

	private final List<Runnable> redrawListeners;

	private final LogicObserver logicObs;
	private ReadEnd end;

	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, WireCrossPoint pin2)
	{
		this(model, pin1, pin2, (Point[]) null);
	}

	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, Pin pin2)
	{
		this(model, pin1, pin2, (Point[]) null);
	}

	public GUIWire(ViewModelModifiable model, Pin pin1, WireCrossPoint pin2)
	{
		this(model, pin1, pin2, (Point[]) null);
	}

	public GUIWire(ViewModelModifiable model, Pin pin1, Pin pin2)
	{
		this(model, pin1, pin2, (Point[]) null);
	}

	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, pin1.getPin(), pin2.getPin(), path);
	}

	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, Pin pin2, Point... path)
	{
		this(model, pin1.getPin(), pin2, path);
	}

	public GUIWire(ViewModelModifiable model, Pin pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, pin1, pin2.getPin(), path);
	}

	public GUIWire(ViewModelModifiable model, Pin pin1, Pin pin2, Point... path)
	{
		logicObs = (i) -> callRedrawListeners();
		this.model = model;
		this.logicWidth = pin1.logicWidth;
		if (pin2.logicWidth != pin1.logicWidth)
			throw new IllegalArgumentException("Can't connect pins of different logic width");

		this.pin1 = pin1;
		this.pin2 = pin2;

		this.path = path == null ? null : Arrays.copyOf(path, path.length);
		this.bounds = new Rectangle(0, 0, -1, -1);

		redrawListeners = new ArrayList<>();

		pin1.addPinMovedListener(p -> pin1Moved());
		pin2.addPinMovedListener(p -> pin2Moved());

		recalculateEffectivePath();

		model.wireCreated(this);
	}

	private void recalculateEffectivePath()
	{
		Point pos1 = pin1.getPos(), pos2 = pin2.getPos();

		double boundsX1 = Math.min(pos1.x, pos2.x);
		double boundsY1 = Math.min(pos1.y, pos2.y);
		double boundsX2 = Math.max(pos1.x, pos2.x);
		double boundsY2 = Math.max(pos1.y, pos2.y);

		if (path == null)
			effectivePath = new double[] { pos1.x, pos1.y, (pos1.x + pos2.x) / 2, pos1.y, (pos1.x + pos2.x) / 2, pos2.y, pos2.x, pos2.y };
		else
		{
			effectivePath = new double[path.length * 2 + 4];
			effectivePath[0] = pos1.x;
			effectivePath[1] = pos1.y;
			for (int srcI = 0, dstI = 2; srcI < path.length; srcI++, dstI += 2)
			{
				double pathX = path[srcI].x;
				double pathY = path[srcI].y;
				effectivePath[dstI + 0] = pathX;
				effectivePath[dstI + 1] = pathY;
				if (pathX < boundsX1)
					boundsX1 = pathX;
				if (pathX > boundsX2)
					boundsX2 = pathX;
				if (pathY < boundsY1)
					boundsY1 = pathY;
				if (pathY > boundsY2)
					boundsY2 = pathY;
			}
			effectivePath[effectivePath.length - 2] = pos2.x;
			effectivePath[effectivePath.length - 1] = pos2.y;
		}

		bounds.x = boundsX1;
		bounds.y = boundsY1;
		bounds.width = boundsX2 - boundsX1;
		bounds.height = boundsY2 - boundsY1;
	}

	private void pin1Moved()
	{
		recalculateEffectivePath();
		callRedrawListeners();
	}

	private void pin2Moved()
	{
		recalculateEffectivePath();
		callRedrawListeners();
	}

	public void destroy()
	{
		model.wireDestroyed(this);
	}

	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void render(GeneralGC gc)
	{
		ColorHelper.executeWithDifferentForeground(gc, BitVectorFormatter.formatAsColor(end), () -> gc.drawPolyline(effectivePath));
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

	public Pin getPin1()
	{
		return pin1;
	}

	public Pin getPin2()
	{
		return pin2;
	}

	public Point[] getPath()
	{
		return path == null ? null : path.clone();
	}

	// @formatter:off
	public void addRedrawListener   (Runnable listener) {redrawListeners         .add   (listener);}

	public void removeRedrawListener(Runnable listener) {redrawListeners         .remove(listener);}

	private void callRedrawListeners() {redrawListeners.forEach(l -> l.run());}
	// @formatter:on

}