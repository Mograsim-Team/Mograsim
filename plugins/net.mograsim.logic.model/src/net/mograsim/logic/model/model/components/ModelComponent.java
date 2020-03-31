package net.mograsim.logic.model.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.JSONSerializable;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.DefaultHighLevelStateHandler;

/**
 * The base class for all model components.<br>
 * A <code>ModelComponent</code> has a reference to the LogicModel it belongs to.<br>
 * A <code>ModelComponent</code> has a name. This name is unique in the model the <code>ModelComponent</code> belongs to.<br>
 * A <code>ModelComponent</code> has a position and size. The size can only be modified by subclasses.
 * 
 * @author Daniel Kirschten
 */
public abstract class ModelComponent implements JSONSerializable
{
	/**
	 * The model this component is a part of.
	 */
	protected final LogicModelModifiable model;
	/**
	 * The name of this component. Is unique for all components in its model.<br>
	 * Does never change, but can't be final since it is set in {@link #init()}.
	 */
	private String name;
	private final Rectangle bounds;
	/**
	 * The list of all pins of this component by name.
	 */
	private final Map<String, Pin> pinsByName;
	/**
	 * An unmodifiable view of {@link #pinsByName}.
	 */
	protected final Map<String, Pin> pinsUnmodifiable;

	private final List<Consumer<? super ModelComponent>> componentMovedListeners;
	private final List<Consumer<? super ModelComponent>> componentResizedListeners;
	private final List<Consumer<? super Pin>> pinAddedListeners;
	private final List<Consumer<? super Pin>> pinRemovedListeners;

	private HighLevelStateHandler highLevelStateHandler;

	// creation and destruction

	public ModelComponent(LogicModelModifiable model, String name)
	{
		this(model, name, true);
	}

	/**
	 * Creates a new {@link ModelComponent} and, if <code>callInit</code>, initializes the component (See {@link #init()}).<br>
	 * If <code>callInit==false</code>, make sure to call {@link #init()}!
	 * 
	 * @author Daniel Kirschten
	 */
	protected ModelComponent(LogicModelModifiable model, String name, boolean callInit)
	{
		this.model = model;
		this.name = name;
		this.bounds = new Rectangle(0, 0, 0, 0);
		this.pinsByName = new HashMap<>();
		this.pinsUnmodifiable = Collections.unmodifiableMap(pinsByName);

		this.componentMovedListeners = new ArrayList<>();
		this.componentResizedListeners = new ArrayList<>();
		this.pinAddedListeners = new ArrayList<>();
		this.pinRemovedListeners = new ArrayList<>();

		this.highLevelStateHandler = new DefaultHighLevelStateHandler();

		if (callInit)
			init();
	}

	/**
	 * Initializes this component. This method should be called exactly once in this component's constructor.<br>
	 * <ul>
	 * <li>If <code>{@link #name}==null</code>, sets {@link #name} to {@link LogicModelModifiable#getDefaultComponentName(ModelComponent)}.
	 * <li>Registers this component in the model.
	 * </ul>
	 */
	protected void init()
	{
		if (name == null)
			name = model.getDefaultComponentName(this);
		model.componentCreated(this, this::destroyed);
	}

	/**
	 * Destroys this component. This method is called from {@link LogicModelModifiable#componentDestroyed(ModelComponent)
	 * destroyComponent()} of the model this component is a part of.<br>
	 * When overriding, make sure to also call the original implementation.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void destroyed()
	{
		pinsByName.values().forEach(this::removePinWithoutRedraw);
	}

	// basic getters

	public String getName()
	{
		return name;
	}

	// pins

	/**
	 * Adds the given pin to this component and calls pinAddedListeners and redrawListeners.
	 * 
	 * @throws IllegalArgumentException if the pin doesn't belong to this component
	 * @throws IllegalArgumentException if there already is a pin with the given name
	 * 
	 * @author Daniel Kirschten
	 */
	protected void addPin(Pin pin)
	{
		if (pin.component != this)
			throw new IllegalArgumentException("Can't add a pin not belonging to this component!");
		if (pinsByName.containsKey(pin.name))
			throw new IllegalArgumentException("Duplicate pin name: " + pin.name);
		pinsByName.put(pin.name, pin);
		callPinAddedListeners(pin);
		model.requestRedraw();
	}

	/**
	 * Removes the given pin from this component and calls pinAddedListeners and redrawListeners.
	 * 
	 * @throws NullPointerException if there was no pin with this name
	 * 
	 * @author Daniel Kirschten
	 */
	protected void removePin(String name)
	{
		removePinWithoutRedraw(pinsByName.remove(name));
		model.requestRedraw();
	}

	private void removePinWithoutRedraw(Pin pin)
	{
		pin.destroyed();
		callPinRemovedListeners(pin);
	}

	/**
	 * Returns a collection of pins of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Map<String, Pin> getPins()
	{
		return pinsUnmodifiable;
	}

	/**
	 * Returns the pin with the given name of this component.
	 * 
	 * @throws IllegalArgumentException if there is no pin with the given name
	 * @see #getPinOrNull(String)
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getPin(String name)
	{
		Pin pin = getPinOrNull(name);
		if (pin == null)
			throw new IllegalArgumentException("No pin with the name " + name);
		return pin;
	}

	/**
	 * Returns the pin with the given name of this component, or <code>null</code> if there is no such pin.
	 * 
	 * @see #getPin(String)
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getPinOrNull(String name)
	{
		return pinsByName.get(name);
	}

	// high-level access

	/**
	 * @author Daniel Kirschten
	 */
	protected void setHighLevelStateHandler(HighLevelStateHandler highLevelStateHandler)
	{
		this.highLevelStateHandler = highLevelStateHandler;
	}

	public HighLevelStateHandler getHighLevelStateHandler()
	{
		return highLevelStateHandler;
	}

	/**
	 * Gets the current value of the given high-level state. <br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @see #setHighLevelState(String, Object)
	 * @see HighLevelStateHandler#get(String)
	 * 
	 * @author Daniel Kirschten
	 */
	public final Object getHighLevelState(String stateID)
	{
		return highLevelStateHandler.get(stateID);
	}

	/**
	 * Sets the given high-level state to the given value. <br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @see #getHighLevelState(String)
	 * @see HighLevelStateHandler#set(String, Object)
	 * 
	 * @author Daniel Kirschten
	 */
	public final void setHighLevelState(String stateID, Object newState)
	{
		highLevelStateHandler.set(stateID, newState);
	}

	public final void addHighLevelStateListener(String stateID, Consumer<Object> stateChanged)
	{
		highLevelStateHandler.addListener(stateID, stateChanged);
	}

	public final void removeHighLevelStateListener(String stateID, Consumer<Object> stateChanged)
	{
		highLevelStateHandler.removeListener(stateID, stateChanged);
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
		model.requestRedraw();
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
		callComponentResizedListener();
		model.requestRedraw();
	}

	/**
	 * Returns the bounds of this component. Is a bit slower than {@link #getPosX()}, {@link #getPosY()}, {@link #getWidth},
	 * {@link #getHeight}, because new objects are created.
	 * 
	 * @author Daniel Kirschten
	 */
	public final Rectangle getBounds()
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
	 * 
	 * @author Daniel Kirschten
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
	public abstract void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion);

	// serializing

	@Override
	public Object getParamsForSerializing(IdentifyParams idParams)
	{
		return null;
	}

	// listeners

	// @formatter:off
	public void addComponentMovedListener      (Consumer<? super ModelComponent> listener) {componentMovedListeners  .add   (listener);}
	public void addComponentResizedListener    (Consumer<? super ModelComponent> listener) {componentResizedListeners.add   (listener);}
	public void addPinAddedListener            (Consumer<? super Pin         > listener) {pinAddedListeners        .add   (listener);}
	public void addPinRemovedListener          (Consumer<? super Pin         > listener) {pinRemovedListeners      .add   (listener);}

	public void removeComponentMovedListener   (Consumer<? super ModelComponent> listener) {componentMovedListeners  .remove(listener);}
	public void removeComponentResizedListener (Consumer<? super ModelComponent> listener) {componentResizedListeners.remove(listener);}
	public void removePinAddedListener         (Consumer<? super Pin         > listener) {pinAddedListeners        .remove(listener);}
	public void removePinRemovedListener       (Consumer<? super Pin         > listener) {pinRemovedListeners      .remove(listener);}

	private void callComponentMovedListeners (     ) {componentMovedListeners  .forEach(l -> l.accept(this));}
	private void callComponentResizedListener(     ) {componentResizedListeners.forEach(l -> l.accept(this));}
	private void callPinAddedListeners       (Pin p) {pinAddedListeners        .forEach(l -> l.accept(p   ));}
	private void callPinRemovedListeners     (Pin p) {pinRemovedListeners      .forEach(l -> l.accept(p   ));}
	// @formatter:on
}