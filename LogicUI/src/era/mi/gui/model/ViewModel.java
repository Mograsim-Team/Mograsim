package era.mi.gui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import era.mi.gui.model.components.GUIComponent;
import era.mi.gui.model.wires.GUIWire;

public class ViewModel
{
	private final List<GUIComponent> components;
	private final List<GUIWire> wires;

	private final List<Consumer<GUIComponent>> componentAddedListeners;
	private final List<Consumer<GUIComponent>> componentRemovedListeners;
	private final List<Consumer<GUIWire>> wireAddedListeners;
	private final List<Consumer<GUIWire>> wireRemovedListeners;

	public ViewModel()
	{
		components = new ArrayList<>();
		wires = new ArrayList<>();

		componentAddedListeners = new ArrayList<>();
		componentRemovedListeners = new ArrayList<>();
		wireAddedListeners = new ArrayList<>();
		wireRemovedListeners = new ArrayList<>();
	}

	/**
	 * Adds the given component to the list of components and calls all componentAddedListeners. Don't call this method from application
	 * code as it is automatically called in GUIComponent::new.
	 */
	public void addComponent(GUIComponent component)
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
	public void removeComponent(GUIComponent component)
	{
		if (!components.contains(component))
			throw new IllegalStateException("Don't remove the same component twice!");
		components.remove(component);
		callComponentRemovedListeners(component);
	}

	// @formatter:off
	public void addComponentAddedListener     (Consumer<GUIComponent> listener){componentAddedListeners  .add   (listener);}
	public void addComponentRemovedListener   (Consumer<GUIComponent> listener){componentRemovedListeners.add   (listener);}
	public void addWireAddedListener          (Consumer<GUIWire     > listener){wireAddedListeners       .add   (listener);}
	public void addWireRemovedListener        (Consumer<GUIWire     > listener){wireRemovedListeners     .add   (listener);}

	public void removeComponentAddedListener  (Consumer<GUIComponent> listener){componentAddedListeners  .remove(listener);}
	public void removeComponentRemovedListener(Consumer<GUIComponent> listener){componentRemovedListeners.remove(listener);}
	public void removeWireAddedListener       (Consumer<GUIWire     > listener){wireAddedListeners       .remove(listener);}
	public void removeWireRemovedListener     (Consumer<GUIWire     > listener){wireRemovedListeners     .remove(listener);}

	private void callComponentAddedListeners  (GUIComponent c) {componentAddedListeners  .forEach(l -> l.accept(c));}
	private void callComponentRemovedListeners(GUIComponent c) {componentRemovedListeners.forEach(l -> l.accept(c));}
	private void callWireAddedListeners       (GUIWire w     ) {wireAddedListeners       .forEach(l -> l.accept(w));}
	private void callWireRemovedListeners     (GUIWire w     ) {wireRemovedListeners     .forEach(l -> l.accept(w));}
	// @formatter:on
}