package net.mograsim.logic.model.model.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.components.GUIComponent;

/**
 * A connection interface between a GUIComponent and the rest of a ViewModel. Pins usually are created by {@link GUIComponent}s themselves.
 * <br>
 * A pin has a name identifying it. Pin names are unique for a {@link GUIComponent}: Every pin of a {@link GUIComponent} has a different
 * name, but different {@link GUIComponent}s can have pins with the same name.
 * 
 * @author Daniel Kirschten
 */
public class Pin
{
	// TODO introduce input/output/tristate hints
	/**
	 * The {@link GUIComponent} this pin belongs to
	 */
	public final GUIComponent component;
	/**
	 * The name identifying this pin. Is unique for a {@link GUIComponent}.
	 */
	public final String name;
	/**
	 * The logical width of this pin. Denotes how many bits this pin consists of.
	 */
	public final int logicWidth;

	/**
	 * The X position of this pin, relative to its component's location.
	 */
	protected double relX;
	/**
	 * The Y position of this pin, relative to its component's location.
	 */
	protected double relY;

	private final List<Consumer<? super Pin>> pinMovedListeners;
	private final List<Runnable> redrawListeners;

	// creation and destruction

	/**
	 * Creates a new pin. Usually it is not needed to call this constructor manually, as {@link GUIComponent}s create their pins themselves.
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin(GUIComponent component, String name, int logicWidth, double relX, double relY)
	{
		this.component = component;
		this.name = name;
		this.logicWidth = logicWidth;
		this.relX = relX;
		this.relY = relY;

		this.pinMovedListeners = new ArrayList<>();
		this.redrawListeners = new ArrayList<>();

		component.addComponentMovedListener(c -> callPinMovedListeners());
	}

	// "graphical" operations

	/**
	 * Returns the X position of this pin relative to the position of its component.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getRelX()
	{
		return relX;
	}

	/**
	 * Returns the Y position of this pin relative to the position of its component.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getRelY()
	{
		return relY;
	}

	/**
	 * Returns the position of this pin relative to the position of its component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Point getRelPos()
	{
		return new Point(relX, relY);
	}

	/**
	 * Returns the absolute position of this pin.
	 * 
	 * @author Daniel Kirschten
	 */
	public Point getPos()
	{
		return new Point(relX + component.getPosX(), relY + component.getPosY());
	}

	/**
	 * Sets the position of this pin relative to the position of its component.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void setRelPos(double relX, double relY)
	{
		this.relX = relX;
		this.relY = relY;
		callPinMovedListeners();
		callRedrawListeners();
	}

	// listeners

	// @formatter:off
	public void addPinMovedListener   (Consumer<? super Pin> listener){pinMovedListeners.add   (listener);}
	public void addRedrawListener     (Runnable              listener){redrawListeners  .add   (listener);}

	public void removePinMovedListener(Consumer<? super Pin> listener){pinMovedListeners.remove(listener);}
	public void removeRedrawListener  (Runnable              listener){redrawListeners  .remove(listener);}

	private void callPinMovedListeners() {pinMovedListeners.forEach(l -> l.accept(this));}
	private void callRedrawListeners  () {redrawListeners  .forEach(l -> l.run   (    ));}
	// @formatter:on

	@Override
	public String toString()
	{
		return "Pin [" + name + ", point=" + getPos() + "]";
	}
}