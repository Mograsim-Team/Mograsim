package mograsim.logic.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import mograsim.logic.ui.model.components.GUIComponent;
import mograsim.logic.ui.model.wires.GUIWire;

public class ViewModel
{
	private final List<GUIComponent> components;
	private final List<GUIComponent> componentsUnmodifiable;
	private final List<GUIWire> wires;
	private final List<GUIWire> wiresUnmodifiable;

	private final List<Consumer<? super GUIComponent>> componentAddedListeners;
	private final List<Consumer<? super GUIComponent>> componentRemovedListeners;
	private final List<Consumer<? super GUIWire>> wireAddedListeners;
	private final List<Consumer<? super GUIWire>> wireRemovedListeners;

	public ViewModel()
	{
		components = new ArrayList<>();
		componentsUnmodifiable = Collections.unmodifiableList(components);
		wires = new ArrayList<>();
		wiresUnmodifiable = Collections.unmodifiableList(wires);

		componentAddedListeners = new ArrayList<>();
		componentRemovedListeners = new ArrayList<>();
		wireAddedListeners = new ArrayList<>();
		wireRemovedListeners = new ArrayList<>();
	}

	/**
	 * Adds the given component to the list of components and calls all componentAddedListeners. Don't call this method from application
	 * code as it is automatically called in GUIComponent::new.
	 */
	public void componentCreated(GUIComponent component)
	{
		if (components.contains(component))
			throw new IllegalStateException("Don't add the same component twice!");
		components.add(component);
		callComponentAddedListeners(component);
	}

	/**
	 * Removes the given component from the list of components and calls all componentRemovedListeners. Don't call this method from
	 * application code as it is automatically called in GUIComponent::destroy.
	 */
	public void componentDestroyed(GUIComponent component)
	{
		if (!components.contains(component))
			throw new IllegalStateException("Don't remove the same component twice!");
		components.remove(component);
		callComponentRemovedListeners(component);
	}

	/**
	 * Adds the given component to the list of components and calls all componentAddedListeners. Don't call this method from application
	 * code as it is automatically called in GUIComponent::new.
	 */
	public void wireCreated(GUIWire wire)
	{
		if (wires.contains(wire))
			throw new IllegalStateException("Don't add the same wire twice!");
		wires.add(wire);
		callWireAddedListeners(wire);
	}

	/**
	 * Removes the given component from the list of components and calls all componentRemovedListeners. Don't call this method from
	 * application code as it is automatically called in GUIComponent::destroy.
	 */
	public void wireDestroyed(GUIWire wire)
	{
		if (!wires.contains(wire))
			throw new IllegalStateException("Don't remove the same wire twice!");
		wires.remove(wire);
		callWireRemovedListeners(wire);
	}

	public List<GUIComponent> getComponents()
	{
		return componentsUnmodifiable;
	}

	public List<GUIWire> getWires()
	{
		return wiresUnmodifiable;
	}

	// @formatter:off
	public void addComponentAddedListener     (Consumer<? super GUIComponent> listener){componentAddedListeners  .add   (listener);}
	public void addComponentRemovedListener   (Consumer<? super GUIComponent> listener){componentRemovedListeners.add   (listener);}
	public void addWireAddedListener          (Consumer<? super GUIWire     > listener){wireAddedListeners       .add   (listener);}
	public void addWireRemovedListener        (Consumer<? super GUIWire     > listener){wireRemovedListeners     .add   (listener);}

	public void removeComponentAddedListener  (Consumer<? super GUIComponent> listener){componentAddedListeners  .remove(listener);}
	public void removeComponentRemovedListener(Consumer<? super GUIComponent> listener){componentRemovedListeners.remove(listener);}
	public void removeWireAddedListener       (Consumer<? super GUIWire     > listener){wireAddedListeners       .remove(listener);}
	public void removeWireRemovedListener     (Consumer<? super GUIWire     > listener){wireRemovedListeners     .remove(listener);}

	private void callComponentAddedListeners  (GUIComponent c) {componentAddedListeners  .forEach(l -> l.accept(c));}
	private void callComponentRemovedListeners(GUIComponent c) {componentRemovedListeners.forEach(l -> l.accept(c));}
	private void callWireAddedListeners       (GUIWire w     ) {wireAddedListeners       .forEach(l -> l.accept(w));}
	private void callWireRemovedListeners     (GUIWire w     ) {wireRemovedListeners     .forEach(l -> l.accept(w));}
	// @formatter:on
}