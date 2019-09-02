package net.mograsim.logic.model.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.GUIWire;

public class ViewModel
{
	private final Map<String, GUIComponent> components;
	private final Map<String, Runnable> componentDestroyFunctions;
	private final Map<String, GUIComponent> componentsUnmodifiable;
	private final Map<String, GUIWire> wires;
	private final Map<String, Runnable> wireDestroyFunctions;
	private final Map<String, GUIWire> wiresUnmodifiable;

	private final List<Consumer<? super GUIComponent>> componentAddedListeners;
	private final List<Consumer<? super GUIComponent>> componentRemovedListeners;
	private final List<Consumer<? super GUIWire>> wireAddedListeners;
	private final List<Consumer<? super GUIWire>> wireRemovedListeners;
	private final List<Consumer<? super Runnable>> redrawHandlerChangedListeners;

	private Runnable redrawHandler;

	protected ViewModel()
	{
		components = new HashMap<>();
		componentDestroyFunctions = new HashMap<>();
		componentsUnmodifiable = Collections.unmodifiableMap(components);
		wires = new HashMap<>();
		wireDestroyFunctions = new HashMap<>();
		wiresUnmodifiable = Collections.unmodifiableMap(wires);

		componentAddedListeners = new ArrayList<>();
		componentRemovedListeners = new ArrayList<>();
		wireAddedListeners = new ArrayList<>();
		wireRemovedListeners = new ArrayList<>();
		redrawHandlerChangedListeners = new ArrayList<>();
	}

	/**
	 * Adds the given component to the list of components and calls all componentAddedListeners. Don't call this method from application
	 * code as it is automatically called in {@link GUIComponent}'s constructor.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void componentCreated(GUIComponent component, Runnable destroyed)
	{
		if (components.containsKey(component.name))
			throw new IllegalStateException("Don't add the same component twice!");
		components.put(component.name, component);
		componentDestroyFunctions.put(component.name, destroyed);
		callComponentAddedListeners(component);
		requestRedraw();
	}

	/**
	 * Destroyes the given component, removes it from the list of components and calls all componentRemovedListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void destroyComponent(GUIComponent component)
	{
		componentDestroyFunctions.get(component.name).run();
		if (!components.containsKey(component.name))
			throw new IllegalStateException("Don't remove the same component twice!");
		components.remove(component.name);
		callComponentRemovedListeners(component);
		requestRedraw();
	}

	/**
	 * Adds the given wire to the list of wires and calls all wireAddedListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void wireCreated(GUIWire wire, Runnable destroyed)
	{
		if (wires.containsKey(wire.name))
			throw new IllegalStateException("Don't add the same wire twice!");
		wires.put(wire.name, wire);
		wireDestroyFunctions.put(wire.name, destroyed);
		callWireAddedListeners(wire);
		requestRedraw();
	}

	/**
	 * Destroys the given wire, removes it from the list of wires and calls all wireRemovedListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void destroyWire(GUIWire wire)
	{
		wireDestroyFunctions.get(wire.name).run();
		if (!wires.containsKey(wire.name))
			throw new IllegalStateException("Don't remove the same wire twice!");
		wires.remove(wire.name);
		callWireRemovedListeners(wire);
		requestRedraw();
	}

	public Map<String, GUIComponent> getComponentsByName()
	{
		return componentsUnmodifiable;
	}

	public Map<String, GUIWire> getWiresByName()
	{
		return wiresUnmodifiable;
	}

	// @formatter:off
	public void addComponentAddedListener         (Consumer<? super GUIComponent> listener) {componentAddedListeners      .add   (listener);}
	public void addComponentRemovedListener       (Consumer<? super GUIComponent> listener) {componentRemovedListeners    .add   (listener);}
	public void addWireAddedListener              (Consumer<? super GUIWire     > listener) {wireAddedListeners           .add   (listener);}
	public void addWireRemovedListener            (Consumer<? super GUIWire     > listener) {wireRemovedListeners         .add   (listener);}
	public void addRedrawHandlerChangedListener   (Consumer<? super Runnable    > listener) {redrawHandlerChangedListeners.add   (listener);}

	public void removeComponentAddedListener      (Consumer<? super GUIComponent> listener) {componentAddedListeners      .remove(listener);}
	public void removeComponentRemovedListener    (Consumer<? super GUIComponent> listener) {componentRemovedListeners    .remove(listener);}
	public void removeWireAddedListener           (Consumer<? super GUIWire     > listener) {wireAddedListeners           .remove(listener);}
	public void removeWireRemovedListener         (Consumer<? super GUIWire     > listener) {wireRemovedListeners         .remove(listener);}
	public void removeRedrawHandlerChangedListener(Consumer<? super Runnable    > listener) {redrawHandlerChangedListeners.remove(listener);}

	private void callComponentAddedListeners     (GUIComponent c) {componentAddedListeners      .forEach(l -> l.accept(c));}
	private void callComponentRemovedListeners   (GUIComponent c) {componentRemovedListeners    .forEach(l -> l.accept(c));}
	private void callWireAddedListeners          (GUIWire      w) {wireAddedListeners           .forEach(l -> l.accept(w));}
	private void callWireRemovedListeners        (GUIWire      w) {wireRemovedListeners         .forEach(l -> l.accept(w));}
	private void callRedrawHandlerChangedListener(Runnable     r) {redrawHandlerChangedListeners.forEach(l -> l.accept(r));}
	// @formatter:on

	public void setRedrawHandler(Runnable handler)
	{
		this.redrawHandler = handler;
		callRedrawHandlerChangedListener(handler);
	}

	public Runnable getRedrawHandler()
	{
		return redrawHandler;
	}

	public void requestRedraw()
	{
		if (redrawHandler != null)
			redrawHandler.run();
	}
}