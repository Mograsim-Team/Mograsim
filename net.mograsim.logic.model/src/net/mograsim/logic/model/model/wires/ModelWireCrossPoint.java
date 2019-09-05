package net.mograsim.logic.model.model.wires;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

/**
 * A {@link ModelComponent} with only one pin. Is used to create wires connecting more than two pins. <br>
 * Example: There are three pins <code>P1</code>, <code>P2</code>, <code>P3</code> that need to be connected. Solution: Create a
 * ModelWireCrossPoint (<code>WCP</code>) and create the ModelWires <code>P1</code>-<code>WCP</code>, <code>P2</code>-<code>WCP</code>,
 * <code>P3</code>-<code>WCP</code>.<br>
 * Cross points are drawn as circles. The pin of cross points is in the center of this circle.
 * 
 * @author Daniel Kirschten
 */
public class ModelWireCrossPoint extends ModelComponent
{
	private static final int CIRCLE_RADIUS = 1;
	private static final int CIRCLE_DIAM = CIRCLE_RADIUS * 2;

	/**
	 * The logical width of this cross point.
	 */
	public final int logicWidth;
	/**
	 * The (single) pin of this cross point.
	 */
	private final Pin pin;

	/**
	 * A {@link LogicObserver} calling {@link #requestRedraw()}.
	 */
	private final LogicObserver logicObs;
	/**
	 * The {@link ReadEnd} currently bound to this cross point.
	 */
	private ReadEnd end;

	// creation and destruction

	public ModelWireCrossPoint(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelWireCrossPoint(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, name);
		this.logicWidth = logicWidth;
		logicObs = (i) -> model.requestRedraw();

		setSize(CIRCLE_DIAM, CIRCLE_DIAM);
		addPin(this.pin = new Pin(this, "", logicWidth, PinUsage.TRISTATE, CIRCLE_RADIUS, CIRCLE_RADIUS));
	}

	// pins

	public Pin getPin()
	{
		return pin;
	}

	// "graphical" operations

	/**
	 * Moves the center (and therefore the pin) of this {@link ModelWireCrossPoint} to the given location.
	 * 
	 * @author Daniel Kirschten
	 */
	public void moveCenterTo(double x, double y)
	{
		moveTo(x - CIRCLE_RADIUS, y - CIRCLE_RADIUS);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		ColorDefinition wireColor = BitVectorFormatter.formatAsColor(end);
		if (wireColor != null)
			gc.setBackground(ColorManager.current().toColor(wireColor));
		gc.fillOval(getPosX(), getPosY(), CIRCLE_DIAM, CIRCLE_DIAM);
	}

	// core model binding

	/**
	 * Binds this {@link ModelWireCrossPoint} to the given {@link ReadEnd}: The color of this {@link ModelWireCrossPoint} will now depend on
	 * the state of the given {@link ReadEnd}, and further changes of the given {@link ReadEnd} will result in readrawListeners being
	 * called.<br>
	 * The argument can be null, in which case the old binding is stopped.
	 * 
	 * @author Daniel Kirschten
	 */
	public void setCoreModelBinding(ReadEnd end)
	{
		if (this.end != null)
			this.end.deregisterObserver(logicObs);
		this.end = end;
		if (end != null)
			end.registerObserver(logicObs);
	}

	/**
	 * Returns whether this {@link ModelWireCrossPoint} has a core model binding or not.
	 */
	public boolean hasCoreModelBinding()
	{
		return end != null;
	}

	// serializing

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "WireCrossPoint";
	}

	@Override
	public Integer getParamsForSerializing(IdentifyParams idParams)
	{
		return logicWidth;
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelWireCrossPoint.class.getCanonicalName(),
				(m, p, n) -> new ModelWireCrossPoint(m, p.getAsInt(), n));
	}
}