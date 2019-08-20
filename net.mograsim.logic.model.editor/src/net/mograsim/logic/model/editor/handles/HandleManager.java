package net.mograsim.logic.model.editor.handles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.states.EditorState;
import net.mograsim.logic.model.editor.util.PrioritySet;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;

public class HandleManager
{
	private final Map<Pin, StaticPinHandle> handlePerPin;
	private final Map<Pin, InterfacePinHandle> handlePerInterfacePin;
	private final Map<GUIWire, List<WirePointHandle>> pointHandlesPerWire;
	private final Map<GUIWire, WireHandle> handlePerWire;
	private final Set<Handle> handles;
	private final Set<WirePointHandle> wirePointHandles;
	private final Map<GUIComponent, ComponentHandle> handlePerComp;

	private final Collection<Consumer<Handle>> handleAddedListeners;
	private final Collection<Consumer<Handle>> handleRemovedListeners;
	private final Editor editor;
	private boolean initialized = false;

	public HandleManager(Editor editor)
	{
		this.editor = editor;
		handlePerPin = new HashMap<>();
		handlePerInterfacePin = new HashMap<>();
		pointHandlesPerWire = new HashMap<>();
		handlePerComp = new HashMap<>();
		handles = new PrioritySet<>((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
		wirePointHandles = new HashSet<>();
		handlePerWire = new HashMap<>();

		handleAddedListeners = new ArrayList<>();
		handleRemovedListeners = new ArrayList<>();

		ViewModelModifiable model = editor.getSubmodel();

		model.addComponentAddedListener(c -> registerComponent(c));

		model.addComponentRemovedListener(c ->
		{
			removeComponentHandle(c);
		});

		model.addWireAddedListener(w ->
		{
			registerWire(w);
		});

		model.addWireRemovedListener(w ->
		{
			removeWireHandle(w);
			removeWirePointHandles(w);
		});
	}

	////////////////////////////////////////
	// -- Setting up initial handles -- ///
	//////////////////////////////////////

	public void init()
	{
		if (initialized)
			System.err.println("Warning! HandleManager was already initialized.");
		else
		{
			ViewModelModifiable model = editor.getSubmodel();
			Map<String, GUIComponent> compsByName = model.getComponentsByName();
			Set<GUIComponent> comps = new HashSet<>(compsByName.values());
			GUIComponent interfaceComp = compsByName.get(SubmodelComponent.SUBMODEL_INTERFACE_NAME);
			comps.remove(interfaceComp);
			registerInterfaceComponent(interfaceComp);
			comps.forEach(c -> registerComponent(c));

			model.getWiresByName().values().forEach(w -> registerWire(w));
			addHandle(new CornerHandle(editor.toBeEdited));
		}
	}

	private void registerInterfaceComponent(GUIComponent c)
	{
		c.getPins().values().forEach(p -> addInterfacePinHandle(p));
		c.addPinAddedListener(p -> addInterfacePinHandle(p));
		c.addPinRemovedListener(p -> removeInterfacePinHandle(p));
	}

	private void registerComponent(GUIComponent c)
	{
		addComponentHandle(c);

		c.getPins().values().forEach(p -> addPinHandle(p));

		c.addPinAddedListener(p -> addPinHandle(p));
		c.addPinRemovedListener(p -> removePinHandle(p));
	}

	private void registerWire(GUIWire wire)
	{
		Point[] path = wire.getPath();
		AtomicInteger oldLength = new AtomicInteger(path == null ? 0 : path.length);
		wire.addPathChangedListener(w ->
		{
			Point[] newPath = w.getPath();
			int newLength = newPath == null ? 0 : newPath.length;
			int diff = newLength - oldLength.getAndSet(newLength);
			if (diff != 0)
			{
				if (diff > 0)
				{
					for (int i = 0; i < diff; i++)
						addWirePointHandle(w);
				}

				List<WirePointHandle> wpHandles = pointHandlesPerWire.get(w);
				int size = wpHandles.size();
				for (int i = 0; i < size; i++)
				{
					wpHandles.get(i).setIndex(i);
				}
			}
			pointHandlesPerWire.get(w).forEach(h -> h.updatePos());
		});
		addWireHandle(wire);
		if (path == null)
			return;
		for (int i = 0; i < path.length; i++)
		{
			addWirePointHandle(wire);
		}
	}

	/////////////////////////////////////
	// -- Adding/Removing handles -- ///
	///////////////////////////////////

	private void addComponentHandle(GUIComponent c)
	{
		ComponentHandle h = new ComponentHandle(c);
		handlePerComp.put(c, h);
		addHandle(h);
	}

	private void removeComponentHandle(GUIComponent c)
	{
		ComponentHandle h = handlePerComp.get(c);
		handlePerComp.remove(c);
		removeHandle(h);
	}

	private void addPinHandle(Pin owner)
	{
		StaticPinHandle h = new StaticPinHandle(owner);
		handlePerPin.put(owner, h);
		addHandle(h);
	}

	private void removePinHandle(Pin owner)
	{
		StaticPinHandle h = handlePerPin.get(owner);
		handlePerPin.remove(owner);
		removeHandle(h);
	}

	private void addInterfacePinHandle(Pin p)
	{
		// The following is not an alternative to the cast, because the new pin is not yet in the map, when the listener is called
		// editor.toBeEdited.getSubmodelMovablePins().get(p.name);
		MovablePin pM = (MovablePin) p;
		InterfacePinHandle h = new InterfacePinHandle(pM, editor.toBeEdited);
		handlePerInterfacePin.put(pM, h);
		addHandle(h);
	}

	private void removeInterfacePinHandle(Pin p)
	{
		InterfacePinHandle h = handlePerInterfacePin.get(p);
		handlePerInterfacePin.remove(p);
		removeHandle(h);
	}

	private void addWirePointHandle(GUIWire w)
	{
		List<WirePointHandle> wireHandles = pointHandlesPerWire.get(w);
		WirePointHandle h;
		if (wireHandles != null)
			wireHandles.add(h = new WirePointHandle(this, w, wireHandles.size()));
		else
		{
			wireHandles = new ArrayList<>();
			h = new WirePointHandle(this, w, 0);
			wireHandles.add(h);
			pointHandlesPerWire.put(h.parent, wireHandles);
		}
		this.wirePointHandles.add(h);
		addHandle(h);
	}

	void destroyWirePointHandle(GUIWire owner, WirePointHandle h)
	{
		if (pointHandlesPerWire.containsKey(owner))
		{
			List<WirePointHandle> handles = pointHandlesPerWire.get(owner);
			int pointIndex = handles.indexOf(h);
			handles.remove(pointIndex);
			removeHandle(h);
			owner.removePathPoint(pointIndex);
		}
	}

	private void removeWirePointHandles(GUIWire owner)
	{
		if (!pointHandlesPerWire.containsKey(owner))
			return;
		pointHandlesPerWire.get(owner).forEach(h ->
		{
			wirePointHandles.remove(h);
			removeHandle(h);
		});
		pointHandlesPerWire.remove(owner);
	}

	private void addWireHandle(GUIWire w)
	{
		WireHandle h = new WireHandle(w);
		handlePerWire.put(w, h);
		addHandle(h);
	}

	private void removeWireHandle(GUIWire w)
	{
		WireHandle h = handlePerWire.get(w);
		handlePerWire.remove(w);
		removeHandle(h);
	}

	private void addHandle(Handle h)
	{
		handles.add(h);
		callHandleAddedListeners(h);
	}

	private void removeHandle(Handle h)
	{
		handles.remove(h);
		callHandleRemovedListeners(h);
		h.destroy();
	}

	public StaticPinHandle getHandle(Pin parent)
	{
		return handlePerPin.get(parent);
	}

	public ComponentHandle getHandle(GUIComponent parent)
	{
		return handlePerComp.get(parent);
	}

	public WireHandle getHandle(GUIWire parent)
	{
		return handlePerWire.get(parent);
	}

	public Handle getInterfacePinHandle(Pin p)
	{
		return handlePerInterfacePin.get(p);
	}

	/**
	 * @return A Collection of the registered {@link WirePointHandle}s of the specified wire
	 */
	public Collection<WirePointHandle> getWirePointHandles(GUIWire parent)
	{
		return pointHandlesPerWire.get(parent).stream().collect(Collectors.toSet());
	}

	/**
	 * @return An unmodifiable view of all registered {@link Handle}s
	 */
	public Collection<Handle> getHandles()
	{
		return Collections.unmodifiableCollection(handles);
	}

	/**
	 * @return An unmodifiable view of all registered {@link StaticPinHandle}s
	 */
	public Collection<StaticPinHandle> getPinHandles()
	{
		return Collections.unmodifiableCollection(handlePerPin.values());
	}

	/**
	 * @return An unmodifiable view of all registered {@link InterfacePinHandle}s
	 */
	public Collection<InterfacePinHandle> getInterfacePinHandles()
	{
		return Collections.unmodifiableCollection(handlePerInterfacePin.values());
	}

	/**
	 * @return An unmodifiable view of all registered {@link ComponentHandle}s
	 */
	public Collection<ComponentHandle> getComponentHandles()
	{
		return Collections.unmodifiableCollection(handlePerComp.values());
	}

	/**
	 * @return An unmodifiable view of all registered {@link WireHandle}s
	 */
	public Collection<WireHandle> getWireHandles()
	{
		return Collections.unmodifiableCollection(handlePerWire.values());
	}

	/**
	 * @return An unmodifiable view of all registered {@link WirePointHandle}s
	 */
	public Collection<WirePointHandle> getWirePointHandles()
	{
		return Collections.unmodifiableSet(wirePointHandles);
	}

	public void click(Point clicked, int stateMask)
	{
		EditorState entryState = editor.stateManager.getState();
		// TODO: As soon as wires connected to a component being removed also are removed, change priority)
		if (!click(handles, clicked, entryState, stateMask))
			entryState.clickedEmpty(clicked, stateMask);
		entryState.clicked(clicked, stateMask);
	}

	private static boolean click(Collection<? extends Handle> handles, Point clicked, EditorState state, int stateMask)
	{
		for (Handle h : handles)
			if (h.click(clicked.x, clicked.y, stateMask, state))
				return true;
		return false;
	}

	public void addHandleAddedListener(Consumer<Handle> c)
	{
		handleAddedListeners.add(c);
	}

	private void callHandleAddedListeners(Handle added)
	{
		handleAddedListeners.forEach(l -> l.accept(added));
	}

	public void removeHandleAddedListener(Consumer<Handle> c)
	{
		handleAddedListeners.remove(c);
	}

	public void addHandleRemovedListener(Consumer<Handle> c)
	{
		handleRemovedListeners.add(c);
	}

	private void callHandleRemovedListeners(Handle removed)
	{
		handleRemovedListeners.forEach(l -> l.accept(removed));
	}

	public void removeHandleRemovedListener(Consumer<Handle> c)
	{
		handleRemovedListeners.remove(c);
	}
}
