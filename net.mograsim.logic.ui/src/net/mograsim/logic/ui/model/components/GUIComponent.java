package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.Pin;

/**
 * The base class for all GUI components.<br>
 * A <code>GUIComponent</code> has a position and size. The size can only be modified by subclasses.<br>
 * 
 * @author Daniel Kirschten
 */
public abstract class GUIComponent
{
	/**
	 * The model this component is a part of.
	 */
	protected final ViewModelModifiable model;
	private final Rectangle bounds;
	/**
	 * The list of all pins of this component by name.
	 */
	private final Map<String, Pin> pinsByName;
	/**
	 * An unmodifiable view of {@link #pinsByName}<code>.</code>{@link Map#values() values()}.
	 */
	protected final Collection<Pin> pinsUnmodifiable;

	private final List<Consumer<? super GUIComponent>> componentMovedListeners;
	private final List<Consumer<? super Pin>> pinAddedListeners;
	private final List<Consumer<? super Pin>> pinRemovedListeners;
	private final List<Runnable> redrawListeners;

	private final Runnable redrawListenerForSubcomponents;
	// Defines how the GUIComponent is referenced in SubmodelComponentParams
	protected Supplier<String> identifierDelegate = () -> "class:".concat(getClass().getCanonicalName());

	// creation and destruction

	public GUIComponent(ViewModelModifiable model)
	{
		this.model = model;
		this.bounds = new Rectangle(0, 0, 0, 0);
		this.pinsByName = new HashMap<>();
		this.pinsUnmodifiable = Collections.unmodifiableCollection(pinsByName.values());

		this.componentMovedListeners = new ArrayList<>();
		this.pinAddedListeners = new ArrayList<>();
		this.pinRemovedListeners = new ArrayList<>();
		this.redrawListeners = new ArrayList<>();

		redrawListenerForSubcomponents = this::requestRedraw;

		model.componentCreated(this);
	}

	/**
	 * Destroys this component. This method implicitly calls {@link ViewModelModifiable#componentDestroyed(GUIComponent)
	 * componentDestroyed()} for the model this component is a part of.
	 * 
	 * @author Daniel Kirschten
	 */
	public void destroy()
	{
		pinsByName.values().forEach(p -> pinRemovedListeners.forEach(l -> l.accept(p)));
		model.componentDestroyed(this);
	}

	// pins

	/**
	 * Adds the given pin to this component and calls pinAddedListeners and redrawListeners.
	 */
	protected void addPin(Pin pin)
	{
		if (pin.component != this)
			throw new IllegalArgumentException("Can't add a pin not belonging to this component!");
		if (pinsByName.containsKey(pin.name))
			throw new IllegalArgumentException("Duplicate pin name: " + pin.name);
		pinsByName.put(pin.name, pin);
		callPinAddedListeners(pin);
		pin.addRedrawListener(redrawListenerForSubcomponents);
		requestRedraw();
	}

	/**
	 * Removes the given pin from this component and calls pinAddedListeners and redrawListeners.
	 * 
	 * @throws NullPointerException if there was no pin with this name.
	 */
	protected void removePin(String name)
	{
		Pin pin = pinsByName.remove(name);
		callPinRemovedListeners(pin);
		pin.removeRedrawListener(redrawListenerForSubcomponents);
		requestRedraw();
	}

	/**
	 * Returns a collection of pins of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Collection<Pin> getPins()
	{
		return pinsUnmodifiable;
	}

	/**
	 * Returns the pin with the given name of this component.
	 * 
	 * @throws IllegalArgumentException if there is no pin with the given name
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getPin(String name)
	{
		Pin pin = pinsByName.get(name);
		if (pin == null)
			throw new IllegalArgumentException("No pin with the name " + name);
		return pin;
	}

	// "graphical" operations

	/**
	 * Sets the position of this component and calls componentMovedListeners and redrawListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	public void moveTo(double x, double y)
	{
		bounds.x = x;
		bounds.y = y;
		callComponentMovedListeners();
		requestRedraw();
	}

	/**
	 * Sets the size of this component and calls redrawListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void setSize(double width, double height)
	{
		bounds.width = width;
		bounds.height = height;
		requestRedraw();
	}

	/**
	 * Returns the bounds of this component. Is a bit slower than {@link #getPosX()}, {@link #getPosY()}, {@link #getWidth},
	 * {@link #getHeight}, because new objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * Returns the x coordinate of the position of this component. Is a bit faster than {@link #getBounds()} because no objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getPosX()
	{
		return bounds.x;
	}

	/**
	 * Returns the y coordinate of the position of this component. Is a bit faster than {@link #getBounds()} because no objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getPosY()
	{
		return bounds.y;
	}

	/**
	 * Returns the (graphical) width of this component. Is a bit faster than {@link #getBounds()} because no objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getWidth()
	{
		return bounds.width;
	}

	/**
	 * Returns the height of this component. Is a bit faster than {@link #getBounds()} because no objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public double getHeight()
	{
		return bounds.height;
	}

	/**
	 * Called when this component is clicked. Absolute coordinates of the click are given. Returns true if this component consumed this
	 * click.
	 */
	@SuppressWarnings({ "static-method", "unused" }) // this method is inteded to be overridden
	public boolean clicked(double x, double y)
	{
		return false;
	}

	/**
	 * Render this component to the given gc, in absoulute coordinates.
	 * 
	 * @author Daniel Kirschten
	 */
	public abstract void render(GeneralGC gc, Rectangle visibleRegion);

	// serializing

	/**
	 * @return an identifier used to reference this GUIComponent inside of {@link SubmodelComponentParams}
	 */
	public String getIdentifier()
	{
		return identifierDelegate.get();
	}

	@SuppressWarnings("static-method")
	public Map<String, Object> getInstantiationParameters()
	{
		return new TreeMap<>();
	}

	// listeners

	/**
	 * Calls redraw listeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void requestRedraw()
	{
		callRedrawListeners();
	}

	// @formatter:off
	public void addComponentMovedListener   (Consumer<? super GUIComponent> listener) {componentMovedListeners.add   (listener);}
	public void addPinAddedListener         (Consumer<? super Pin         > listener) {pinAddedListeners      .add   (listener);}
	public void addPinRemovedListener       (Consumer<? super Pin         > listener) {pinRemovedListeners    .add   (listener);}
	public void addRedrawListener           (Runnable                       listener) {redrawListeners        .add   (listener);}

	public void removeComponentMovedListener(Consumer<? super GUIComponent> listener) {componentMovedListeners .remove(listener);}
	public void removePinAddedListener      (Consumer<? super Pin         > listener) {pinAddedListeners       .remove(listener);}
	public void removePinRemovedListener    (Consumer<? super Pin         > listener) {pinRemovedListeners     .remove(listener);}
	public void removeRedrawListener        (Runnable                       listener) {redrawListeners         .remove(listener);}

	private void callComponentMovedListeners(     ) {componentMovedListeners.forEach(l -> l.accept(this));}
	private void callPinAddedListeners      (Pin p) {pinAddedListeners      .forEach(l -> l.accept(p   ));}
	private void callPinRemovedListeners    (Pin p) {pinRemovedListeners    .forEach(l -> l.accept(p   ));}
	private void callRedrawListeners        (     ) {redrawListeners        .forEach(l -> l.run(       ));}
	// @formatter:on
}