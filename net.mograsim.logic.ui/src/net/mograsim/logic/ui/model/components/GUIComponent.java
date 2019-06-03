package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.wires.Pin;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public abstract class GUIComponent
{
	protected final ViewModel model;
	private final Rectangle bounds;
	private final List<Pin> pins;
	protected final List<Pin> pinsUnmodifiable;

	private final List<Consumer<? super GUIComponent>> componentMovedListeners;
	private final List<Consumer<? super Pin>> pinAddedListeners;
	private final List<Consumer<? super Pin>> pinRemovedListeners;
	private final List<Runnable> redrawListeners;

	private final Runnable redrawListenerForSubcomponents;

	public GUIComponent(ViewModel model)
	{
		this.model = model;
		this.bounds = new Rectangle(0, 0, 0, 0);
		this.pins = new ArrayList<>();
		this.pinsUnmodifiable = Collections.unmodifiableList(pins);

		this.componentMovedListeners = new ArrayList<>();
		this.pinAddedListeners = new ArrayList<>();
		this.pinRemovedListeners = new ArrayList<>();
		this.redrawListeners = new ArrayList<>();

		redrawListenerForSubcomponents = this::callRedrawListeners;

		model.componentCreated(this);
	}

	public void destroy()
	{
		pins.forEach(p -> pinRemovedListeners.forEach(l -> l.accept(p)));
		model.componentDestroyed(this);
	}

	public void moveTo(double x, double y)
	{
		bounds.x = x;
		bounds.y = y;
		callComponentMovedListeners();
	}

	/**
	 * Returns the bounds of this component. Used for calculating which component is clicked.
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
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
	 * Returns a list of pins of this component.
	 */
	public List<Pin> getPins()
	{
		return pinsUnmodifiable;
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

	/**
	 * Render this component to the given gc.
	 */
	public abstract void render(GeneralGC gc, Rectangle visibleRegion);

	protected void requestRedraw()
	{
		callRedrawListeners();
	}

	protected void setSize(double width, double height)
	{
		bounds.width = width;
		bounds.height = height;
		callRedrawListeners();
	}

	protected void addPin(Pin pin)
	{
		pins.add(pin);
		callPinAddedListeners(pin);
		pin.addRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}

	protected void removePin(Pin pin)
	{
		pins.remove(pin);
		callPinRemovedListeners(pin);
		pin.removeRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}
}