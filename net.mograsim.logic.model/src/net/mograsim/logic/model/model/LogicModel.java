package net.mograsim.logic.model.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;

public class LogicModel
{
	private final Map<String, ModelComponent> components;
	private final Map<String, Runnable> componentDestroyFunctions;
	private final Map<String, ModelComponent> componentsUnmodifiable;
	private final Map<String, ModelWire> wires;
	private final Map<String, Runnable> wireDestroyFunctions;
	private final Map<String, ModelWire> wiresUnmodifiable;

	private final List<Consumer<? super ModelComponent>> componentAddedListeners;
	private final List<Consumer<? super ModelComponent>> componentRemovedListeners;
	private final List<Consumer<? super ModelWire>> wireAddedListeners;
	private final List<Consumer<? super ModelWire>> wireRemovedListeners;
	private final List<Consumer<? super Runnable>> redrawHandlerChangedListeners;

	private Runnable redrawHandler;

	protected LogicModel()
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
	 * code as it is automatically called in {@link ModelComponent}'s constructor.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void componentCreated(ModelComponent component, Runnable destroyed)
	{
		if (components.containsKey(component.getName()))
			throw new IllegalStateException("Don't add the same component twice!");
		components.put(component.getName(), component);
		componentDestroyFunctions.put(component.getName(), destroyed);
		callComponentAddedListeners(component);
		requestRedraw();
	}

	/**
	 * Destroyes the given component, removes it from the list of components and calls all componentRemovedListeners.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void destroyComponent(ModelComponent component)
	{
		componentDestroyFunctions.get(component.getName()).run();
		if (!components.containsKey(component.getName()))
			throw new IllegalStateException("Don't remove the same component twice!");
		components.remove(component.getName());
		callComponentRemovedListeners(component);
		requestRedraw();
	}

	/**
	 * Adds the given wire to the list of wires and calls all wireAddedListeners. Don't call this method from application code as it is
	 * automatically called in {@link ModelWire}'s constructor.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void wireCreated(ModelWire wire, Runnable destroyed)
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
	protected void destroyWire(ModelWire wire)
	{
		wireDestroyFunctions.get(wire.name).run();
		if (!wires.containsKey(wire.name))
			throw new IllegalStateException("Don't remove the same wire twice!");
		wires.remove(wire.name);
		callWireRemovedListeners(wire);
		requestRedraw();
	}

	public Map<String, ModelComponent> getComponentsByName()
	{
		return componentsUnmodifiable;
	}

	public Map<String, ModelWire> getWiresByName()
	{
		return wiresUnmodifiable;
	}

	public <T extends ModelComponent> T getComponentByName(String name, Class<T> expectedComponentClass)
	{
		return getByName(name, expectedComponentClass, components);
	}

	public ModelWire getWireByName(String name)
	{
		return getByName(name, ModelWire.class, wires);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getByName(String name, Class<T> expectedClass, Map<String, ? super T> map)
	{
		Object comp = map.get(name);
		Objects.requireNonNull(comp, "Invaild path, component " + name + " not found");
		if (expectedClass.isInstance(comp))
			return (T) comp;
		throw new IllegalArgumentException("The component " + name + " is not an instance of " + expectedClass);
	}

	public <T extends ModelComponent> T getComponentBySubmodelPath(String path, Class<T> modelClass)
	{
		int firstDot = path.indexOf('.');
		if (firstDot == -1)
			return getComponentByName(path, modelClass);
		String first = path.substring(0, firstDot);
		String rest = path.substring(firstDot + 1);
		return getComponentByName(first, SubmodelComponent.class).submodel.getComponentBySubmodelPath(rest, modelClass);
	}

	public ModelWire getWireBySubmodelPath(String path)
	{
		int firstDot = path.indexOf('.');
		if (firstDot == -1)
			return getWireByName(path);
		String first = path.substring(0, firstDot);
		String rest = path.substring(firstDot + 1);
		return getComponentByName(first, SubmodelComponent.class).submodel.getWireBySubmodelPath(rest);
	}

	// @formatter:off
	public void addComponentAddedListener         (Consumer<? super ModelComponent> listener) {componentAddedListeners      .add   (listener);}
	public void addComponentRemovedListener       (Consumer<? super ModelComponent> listener) {componentRemovedListeners    .add   (listener);}
	public void addWireAddedListener              (Consumer<? super ModelWire     > listener) {wireAddedListeners           .add   (listener);}
	public void addWireRemovedListener            (Consumer<? super ModelWire     > listener) {wireRemovedListeners         .add   (listener);}
	public void addRedrawHandlerChangedListener   (Consumer<? super Runnable      > listener) {redrawHandlerChangedListeners.add   (listener);}

	public void removeComponentAddedListener      (Consumer<? super ModelComponent> listener) {componentAddedListeners      .remove(listener);}
	public void removeComponentRemovedListener    (Consumer<? super ModelComponent> listener) {componentRemovedListeners    .remove(listener);}
	public void removeWireAddedListener           (Consumer<? super ModelWire     > listener) {wireAddedListeners           .remove(listener);}
	public void removeWireRemovedListener         (Consumer<? super ModelWire     > listener) {wireRemovedListeners         .remove(listener);}
	public void removeRedrawHandlerChangedListener(Consumer<? super Runnable      > listener) {redrawHandlerChangedListeners.remove(listener);}

	private void callComponentAddedListeners     (ModelComponent c) {componentAddedListeners      .forEach(l -> l.accept(c));}
	private void callComponentRemovedListeners   (ModelComponent c) {componentRemovedListeners    .forEach(l -> l.accept(c));}
	private void callWireAddedListeners          (ModelWire      w) {wireAddedListeners           .forEach(l -> l.accept(w));}
	private void callWireRemovedListeners        (ModelWire      w) {wireRemovedListeners         .forEach(l -> l.accept(w));}
	private void callRedrawHandlerChangedListener(Runnable       r) {redrawHandlerChangedListeners.forEach(l -> l.accept(r));}
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