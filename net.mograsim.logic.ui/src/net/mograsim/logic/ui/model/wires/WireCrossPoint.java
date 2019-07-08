package net.mograsim.logic.ui.model.wires;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;

/**
 * A {@link GUIComponent} with only one pin. Is used to create wires connecting more than two pins. <br>
 * Example: There are three pins <code>P1</code>, <code>P2</code>, <code>P3</code> that need to be connected. Solution: Create a
 * WireCrossPoint (<code>WCP</code>) and create the GUIWires <code>P1</code>-<code>WCP</code>, <code>P2</code>-<code>WCP</code>,
 * <code>P3</code>-<code>WCP</code>.<br>
 * Cross points are drawn as circles. The pin of cross points is in the center of this circle.
 * 
 * @author Daniel Kirschten
 */
public class WireCrossPoint extends GUIComponent
{
	private static final int CIRCLE_RADIUS = 1;
	private static final int CIRCLE_DIAM = CIRCLE_RADIUS * 2;

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

	public WireCrossPoint(ViewModelModifiable model, int logicWidth)
	{
		super(model);
		logicObs = (i) -> requestRedraw();

		setSize(CIRCLE_DIAM, CIRCLE_DIAM);
		addPin(this.pin = new Pin(this, "", logicWidth, CIRCLE_RADIUS, CIRCLE_RADIUS));
	}

	// pins

	public Pin getPin()
	{
		return pin;
	}

	// "graphical" operations

	/**
	 * Moves the center (and therefore the pin) of this {@link WireCrossPoint} to the given location.
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

	// logic model binding

	/**
	 * Binds this {@link WireCrossPoint} to the given {@link ReadEnd}: The color of this {@link WireCrossPoint} will now depend on the state
	 * of the given {@link ReadEnd}, and further changes of the given {@link ReadEnd} will result in readrawListeners being called.<br>
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
	 * Returns whether this {@link WireCrossPoint} has a logic model binding or not.
	 */
	public boolean hasLogicModelBinding()
	{
		return end != null;
	}

	// serializing

	@Override
	public JsonElement getParams()
	{
		return new JsonPrimitive(pin.logicWidth);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(WireCrossPoint.class.getCanonicalName(),
				(m, p) -> new WireCrossPoint(m, p.getAsInt()));
	}
}