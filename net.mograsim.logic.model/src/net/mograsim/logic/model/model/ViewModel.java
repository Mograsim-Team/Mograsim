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
	private final Map<String, GUIComponent> componentsUnmodifiable;
	private final List<GUIWire> wires;
	private final List<GUIWire> wiresUnmodifiable;

	private final List<Consumer<? super GUIComponent>> componentAddedListeners;
	private final List<Consumer<? super GUIComponent>> componentRemovedListeners;
	private final List<Consumer<? super GUIWire>> wireAddedListeners;
	private final List<Consumer<? super GUIWire>> wireRemovedListeners;
	private final List<Runnable> redrawListeners;

	private final Runnable redrawListenerForSubcomponents;

	protected ViewModel()
	{
		components = new HashMap<>();
		componentsUnmodifiable = Collections.unmodifiableMap(components);
		wires = new ArrayList<>();
		wiresUnmodifiable = Collections.unmodifiableList(wires);

		componentAddedListeners = new ArrayList<>();
		componentRemovedListeners = new ArrayList<>();
		wireAddedListeners = new ArrayList<>();
		wireRemovedListeners = new ArrayList<>();
		redrawListeners = new ArrayList<>();

		redrawListenerForSubcomponents = this::callRedrawListeners;
	}

	/**
	 * Adds the given component to the list of components and calls all componentAddedListeners. Don't call this method from application
	 * code as it is automatically called in {@link GUIComponent}'s constructor.
	 */
	protected void componentCreated(GUIComponent component)
	{
		if (components.containsKey(component.name))
			throw new IllegalStateException("Don't add the same component twice!");
		components.put(component.name, component);
		callComponentAddedListeners(component);
		component.addRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}

	/**
	 * Removes the given component from the list of components and calls all componentRemovedListeners. Don't call this method from
	 * application code as it is automatically called in {@link GUIComponent#destroy()}.
	 */
	protected void componentDestroyed(GUIComponent component)
	{
		if (!components.containsKey(component.name))
			throw new IllegalStateException("Don't remove the same component twice!");
		components.remove(component.name);
		callComponentRemovedListeners(component);
		component.removeRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}

	/**
	 * Adds the given wire to the list of wires and calls all wireAddedListeners. Don't call this method from application code as it is
	 * automatically called in {@link GUIWire}'s constructor(s).
	 */
	protected void wireCreated(GUIWire wire)
	{
		if (wires.contains(wire))
			throw new IllegalStateException("Don't add the same wire twice!");
		wires.add(wire);
		callWireAddedListeners(wire);
		wire.addRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}

	/**
	 * Removes the given wire from the list of wires and calls all wireRemovedListeners. Don't call this method from application code as it
	 * is automatically called in {@link GUIWire#destroy()}.
	 */
	protected void wireDestroyed(GUIWire wire)
	{
		if (!wires.contains(wire))
			throw new IllegalStateException("Don't remove the same wire twice!");
		wires.remove(wire);
		callWireRemovedListeners(wire);
		wire.removeRedrawListener(redrawListenerForSubcomponents);
		callRedrawListeners();
	}

	public Map<String, GUIComponent> getComponentsByName()
	{
		return componentsUnmodifiable;
	}

	public List<GUIWire> getWires()
	{
		return wiresUnmodifiable;
	}

	// @formatter:off
	public void addComponentAddedListener     (Consumer<? super GUIComponent> listener) {componentAddedListeners  .add   (listener);}
	public void addComponentRemovedListener   (Consumer<? super GUIComponent> listener) {componentRemovedListeners.add   (listener);}
	public void addWireAddedListener          (Consumer<? super GUIWire     > listener) {wireAddedListeners       .add   (listener);}
	public void addWireRemovedListener        (Consumer<? super GUIWire     > listener) {wireRemovedListeners     .add   (listener);}
	public void addRedrawListener             (Runnable                       listener) {redrawListeners          .add   (listener);}

	public void removeComponentAddedListener  (Consumer<? super GUIComponent> listener) {componentAddedListeners  .remove(listener);}
	public void removeComponentRemovedListener(Consumer<? super GUIComponent> listener) {componentRemovedListeners.remove(listener);}
	public void removeWireAddedListener       (Consumer<? super GUIWire     > listener) {wireAddedListeners       .remove(listener);}
	public void removeWireRemovedListener     (Consumer<? super GUIWire     > listener) {wireRemovedListeners     .remove(listener);}
	public void removeRedrawListener          (Runnable                       listener) {redrawListeners          .remove(listener);}

	private void callComponentAddedListeners  (GUIComponent c) {componentAddedListeners  .forEach(l -> l.accept(c));}
	private void callComponentRemovedListeners(GUIComponent c) {componentRemovedListeners.forEach(l -> l.accept(c));}
	private void callWireAddedListeners       (GUIWire w     ) {wireAddedListeners       .forEach(l -> l.accept(w));}
	private void callWireRemovedListeners     (GUIWire w     ) {wireRemovedListeners     .forEach(l -> l.accept(w));}
	private void callRedrawListeners          (              ) {redrawListeners          .forEach(l -> l.run(    ));}
	// @formatter:on
}