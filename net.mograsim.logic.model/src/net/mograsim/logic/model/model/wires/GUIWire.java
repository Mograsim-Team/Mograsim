package net.mograsim.logic.model.model.wires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

/**
 * A wire connecting exactly two {@link Pin}s.
 * 
 * @author Daniel Kirschten
 */
public class GUIWire
{
	/**
	 * The model this wire is a part of.
	 */
	private final ViewModelModifiable model;
	/**
	 * The name of this wire. Is unique for all wires in its model.
	 */
	public final String name;
	/**
	 * The logical width of this wire. Is equal to the logical with of {@link #pin1} and {@link #pin2}.
	 */
	public final int logicWidth;
	/**
	 * The {@link Pin} on one side of this wire, usually the signal source.
	 */
	private Pin pin1;
	/**
	 * The {@link Pin} on one side of this wire, usually the signal target.
	 */
	private Pin pin2;
	/**
	 * The user-defined path between {@link #pin1} and {@link #pin2}.<br>
	 * Special cases: <code>null</code> means "choose an interpolation as fits", and an empty array means "direct connection without any
	 * interpolation".
	 */
	private Point[] path;
	/**
	 * The bounds of this wire, excluding line width (and line joins, if the line join is {@link SWT#JOIN_MITER})
	 */
	private final Rectangle bounds;
	/**
	 * The effective path of this wire, including automatic interpolation and the position of both {@link Pin}s. Is never null.
	 */
	private double[] effectivePath;

	private final List<Runnable> redrawListeners;
	private final List<Consumer<GUIWire>> pathChangedListeners;

	/**
	 * A LogicObserver calling redrawListeners. Used for logic model bindings.
	 */
	private final LogicObserver logicObs;
	/**
	 * A ReadEnd of the logic wire this GUI wire currently is bound to.
	 */
	private ReadEnd end;

	// creation and destruction

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, WireCrossPoint pin2)
	{
		this(model, null, pin1, pin2);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, Pin pin2)
	{
		this(model, null, pin1, pin2);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, Pin pin1, WireCrossPoint pin2)
	{
		this(model, null, pin1, pin2);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, Pin pin1, Pin pin2)
	{
		this(model, null, pin1, pin2);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, null, pin1, pin2, path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, WireCrossPoint pin1, Pin pin2, Point... path)
	{
		this(model, null, pin1, pin2, path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, Pin pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, null, pin1, pin2, path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, Pin pin1, Pin pin2, Point... path)
	{
		this(model, null, pin1, pin2, path);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, WireCrossPoint pin1, WireCrossPoint pin2)
	{
		this(model, name, pin1, pin2, (Point[]) null);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, WireCrossPoint pin1, Pin pin2)
	{
		this(model, name, pin1, pin2, (Point[]) null);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, Pin pin1, WireCrossPoint pin2)
	{
		this(model, name, pin1, pin2, (Point[]) null);
	}

	/**
	 * Creates a new {@link GUIWire} with automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, Pin pin1, Pin pin2)
	{
		this(model, name, pin1, pin2, (Point[]) null);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, WireCrossPoint pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, name, pin1.getPin(), pin2.getPin(), path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, WireCrossPoint pin1, Pin pin2, Point... path)
	{
		this(model, name, pin1.getPin(), pin2, path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, Pin pin1, WireCrossPoint pin2, Point... path)
	{
		this(model, name, pin1, pin2.getPin(), path);
	}

	/**
	 * Creates a new {@link GUIWire} without automatic interpolation.
	 * 
	 * @author Daniel Kirschten
	 */
	public GUIWire(ViewModelModifiable model, String name, Pin pin1, Pin pin2, Point... path)
	{
		this.model = model;
		this.name = name == null ? model.getDefaultWireName() : name;
		this.logicWidth = pin1.logicWidth;
		if (pin2.logicWidth != pin1.logicWidth)
			throw new IllegalArgumentException("Can't connect pins of different logic width");

		this.pin1 = pin1;
		this.pin2 = pin2;

		this.path = path == null ? null : Arrays.copyOf(path, path.length);
		this.bounds = new Rectangle(0, 0, -1, -1);

		redrawListeners = new ArrayList<>();
		pathChangedListeners = new ArrayList<>();

		logicObs = (i) -> callRedrawListeners();

		pin1.addPinMovedListener(p -> pinMoved());
		pin2.addPinMovedListener(p -> pinMoved());

		recalculateEffectivePath();

		model.wireCreated(this);
	}

	/**
	 * Destroys this wire. This method implicitly calls {@link ViewModelModifiable#wireDestroyed(GUIWire) wireDestroyed()} for the model
	 * this component is a part of.
	 * 
	 * @author Daniel Kirschten
	 */
	public void destroy()
	{
		model.wireDestroyed(this);
	}

	// pins

	/**
	 * Returns the {@link Pin} on one side of this wire, usually the signal source.
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getPin1()
	{
		return pin1;
	}

	/**
	 * Returns the {@link Pin} on one side of this wire, usually the signal target.
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getPin2()
	{
		return pin2;
	}

	/**
	 * Called when {@link #pin1} or {@link #pin2} were moved.
	 * 
	 * @author Daniel Kirschten
	 */
	private void pinMoved()
	{
		recalculateEffectivePath();
		callRedrawListeners();
	}

	// "graphical" operations

	/**
	 * Recalculates {@link #effectivePath} "from scratch". Also updates {@link #bounds}.
	 * 
	 * @author Daniel Kirschten
	 */
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

	/**
	 * Returns the bounds of this wire, excluding line width (and line joins, if the line join is {@link SWT#JOIN_MITER})
	 * 
	 * @author Daniel Kirschten
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * Render this wire to the given gc, in absoulute coordinates.
	 * 
	 * @author Daniel Kirschten
	 */
	public void render(GeneralGC gc)
	{
		ColorDefinition wireColor = BitVectorFormatter.formatAsColor(end);
		if (wireColor != null)
			gc.setForeground(ColorManager.current().toColor(wireColor));
		gc.drawPolyline(effectivePath);
	}

	// operations concerning the path

	/**
	 * The user-defined path between {@link #pin1} and {@link #pin2}. Note that this is not neccessarily equal to the effective path drawn
	 * in {@link #render(GeneralGC)}.<br>
	 * Special cases: <code>null</code> means "choose an interpolation as fits", and an empty array means "direct connection without any
	 * interpolation".
	 * 
	 * @author Daniel Kirschten
	 */
	public Point[] getPath()
	{
		return deepPathCopy(path);
	}

	public void setPath(Point... path)
	{
		this.path = deepPathCopy(path);
		recalculateEffectivePath();
		callPathChangedListeners();
		callRedrawListeners();
	}

	public Point getPathPoint(int index)
	{
		return pointCopy(path[index]);
	}

	public void setPathPoint(Point p, int index)
	{
		path[index] = pointCopy(p);
		recalculateEffectivePath();
		callPathChangedListeners();
		callRedrawListeners();
	}

	public void insertPathPoint(Point p, int index)
	{
		if (path == null)
			path = new Point[] { pointCopy(p) };
		else
		{
			Point[] oldPath = path;
			path = new Point[oldPath.length + 1];
			System.arraycopy(oldPath, 0, path, 0, index);
			if (index < oldPath.length)
				System.arraycopy(oldPath, index, path, index + 1, oldPath.length - index);
			path[index] = pointCopy(p);
		}
	}

	public void removePathPoint(int index)
	{
		if (path.length == 0)
			path = null;
		else
		{
			Point[] oldPath = path;
			path = new Point[oldPath.length - 1];
			System.arraycopy(oldPath, 0, path, 0, index);
			if (index < oldPath.length - 1)
				System.arraycopy(oldPath, index + 1, path, index, oldPath.length - index - 1);
		}
	}

	public double[] getEffectivePath()
	{
		return Arrays.copyOf(effectivePath, effectivePath.length);
	}

	private static Point[] deepPathCopy(Point[] path)
	{
		if (path == null)
			return null;
		Point[] copy = new Point[path.length];
		for (int i = 0; i < path.length; i++)
			copy[i] = pointCopy(path[i]);
		return copy;
	}

	private static Point pointCopy(Point p)
	{
		return new Point(p.x, p.y);
	}

	// logic model binding

	/**
	 * Binds this {@link GUIWire} to the given {@link ReadEnd}: The color of this {@link GUIWire} will now depend on the state of the given
	 * {@link ReadEnd}, and further changes of the given {@link ReadEnd} will result in readrawListeners being called.<br>
	 * The argument can be null, in which case the old binding is stopped.
	 * 
	 * @author Daniel Kirschten
	 */
	public void setLogicModelBinding(ReadEnd end)
	{
		if (this.end != null)
			this.end.deregisterObserver(logicObs);
		this.end = end;
		if (end != null)
			end.registerObserver(logicObs);
	}

	/**
	 * Returns whether this {@link GUIWire} has a logic model binding or not.
	 * 
	 * @author Daniel Kirschten
	 */
	public boolean hasLogicModelBinding()
	{
		return end != null;
	}

	/**
	 * If this {@link GUIWire} has a logic model binding, delegates to {@link Wire#forceValues(BitVector)} for the {@link Wire}
	 * corresponding to this {@link GUIWire}.
	 * 
	 * @author Daniel Kirschten
	 */
	public void forceWireValues(BitVector values)
	{
		end.getWire().forceValues(values);
	}

	/**
	 * If this {@link GUIWire} has a logic model binding, delegates to {@link ReadEnd#getValues()} for the {@link ReadEnd} corresponding to
	 * this {@link GUIWire}.
	 * 
	 * @author Daniel Kirschten
	 */
	public BitVector getWireValues()
	{
		return end.getValues();
	}

	// listeners

	// @formatter:off
	public void addRedrawListener        (Runnable          listener) {redrawListeners     .add    (listener);}
	public void addPathChangedListener   (Consumer<GUIWire> listener) {pathChangedListeners.add    (listener);}

	public void removeRedrawListener     (Runnable          listener) {redrawListeners     .remove(listener);}
	public void removePathChangedListener(Consumer<GUIWire> listener) {pathChangedListeners.remove(listener);}

	private void callRedrawListeners     () {redrawListeners     .forEach(l -> l.run   (    ));}
	private void callPathChangedListeners() {pathChangedListeners.forEach(l -> l.accept(this));}
	// @formatter:on

	@Override
	public String toString()
	{
		return "GUIWire [" + pin1 + "---" + pin2 + ", value=" + (end == null ? "null" : end.getValues()) + "]";
	}
}