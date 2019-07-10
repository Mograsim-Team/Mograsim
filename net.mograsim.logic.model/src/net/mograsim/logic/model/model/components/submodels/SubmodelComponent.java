package net.mograsim.logic.model.model.components.submodels;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import net.haspamelodica.swt.helper.gcs.GCConfig;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.LogicUIRenderer;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.SubmodelComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams.InnerPinParams;

/**
 * A {@link GUIComponent} consisting of another model. A <code>SubmodelComponent</code> can have so-called "interface pins" connecting the
 * inner and outer models.
 */
public abstract class SubmodelComponent extends GUIComponent
{
	private static final String SUBMODEL_INTERFACE_NAME = "_submodelinterface";
	/**
	 * A modifiable view of {@link #submodel}.
	 */
	protected final ViewModelModifiable submodelModifiable;
	/**
	 * The model this {@link SubmodelComponent} consists of.
	 */
	public final ViewModel submodel;
	/**
	 * The list of all submodel interface pins of this {@link SubmodelComponent} on the submodel side.
	 */
	private final Map<String, MovablePin> submodelPins;
	/**
	 * An unmodifiable view of {@link #submodelPins}.
	 */
	private final Map<String, MovablePin> submodelMovablePinsUnmodifiable;
	/**
	 * An unmodifiable view of {@link #submodelPins} where pins are not movable.
	 */
	private final Map<String, Pin> submodelUnmovablePinsUnmodifiable;
	/**
	 * The list of all submodel interface pins of this {@link SubmodelComponent} on the supermodel side.
	 */
	private final Map<String, MovablePin> supermodelPins;
	/**
	 * An unmodifiable view of {@link #supermodelPins}.
	 */
	private final Map<String, MovablePin> supermodelMovablePinsUnmodifiable;
	/**
	 * An unmodifiable view of {@link #supermodelPins} where pins are not movable.
	 */
	private final Map<String, Pin> supermodelUnmovablePinsUnmodifiable;
	/**
	 * A pseudo-component containing all submodel interface pins on the submodel side.
	 */
	private final SubmodelInterface submodelInterface;

	/**
	 * The list of all high level state IDs this component supports without delegating to subcomponents.
	 */
	private final Set<String> highLevelAtomicStates;
	/**
	 * A map of high level state subcomponent IDs to the {@link GUIComponent} high level state access requests are delegated to.
	 */
	private final Map<String, GUIComponent> subcomponentsByHighLevelStateSubcomponentID;

	/**
	 * The factor by which the submodel is scaled when rendering.
	 */
	private double submodelScale;
	/**
	 * If this {@link SubmodelComponent} fills at least this amount of the visible region vertically or horizontally, the submodel starts to
	 * be visible.
	 */
	private double maxVisibleRegionFillRatioForAlpha0;
	/**
	 * If this {@link SubmodelComponent} fills at least this amount of the visible region vertically or horizontally, the submodel is fully
	 * visible.
	 */
	private double minVisibleRegionFillRatioForAlpha1;
	/**
	 * The renderer used for rendering the submodel.
	 */
	private final LogicUIRenderer renderer;

	// creation and destruction

	public SubmodelComponent(ViewModelModifiable model, String name)
	{
		super(model, name);
		this.submodelModifiable = new ViewModelModifiable();
		this.submodel = submodelModifiable;
		this.submodelPins = new HashMap<>();
		this.submodelMovablePinsUnmodifiable = Collections.unmodifiableMap(submodelPins);
		this.submodelUnmovablePinsUnmodifiable = Collections.unmodifiableMap(submodelPins);
		this.supermodelPins = new HashMap<>();
		this.supermodelMovablePinsUnmodifiable = Collections.unmodifiableMap(supermodelPins);
		this.supermodelUnmovablePinsUnmodifiable = Collections.unmodifiableMap(supermodelPins);
		this.submodelInterface = new SubmodelInterface(submodelModifiable, SUBMODEL_INTERFACE_NAME);

		this.highLevelAtomicStates = new HashSet<>();
		this.subcomponentsByHighLevelStateSubcomponentID = new HashMap<>();

		this.submodelScale = 1;
		this.maxVisibleRegionFillRatioForAlpha0 = 0.4;
		this.minVisibleRegionFillRatioForAlpha1 = 0.8;
		this.renderer = new LogicUIRenderer(submodelModifiable);

		submodelModifiable.addRedrawListener(this::requestRedraw);
	}

	// pins

	/**
	 * Adds a new submodel interface pin.
	 * 
	 * @param supermodelPin the submodel interface pin on the supermodel side
	 * 
	 * @return the submodel interface pin on the submodel side
	 * 
	 * @author Daniel Kirschten
	 */
	protected Pin addSubmodelInterface(MovablePin supermodelPin)
	{
		super.addPin(supermodelPin);// do this first to be fail-fast if the supermodel does not belong to this component

		String name = supermodelPin.name;
		MovablePin submodelPin = new MovablePin(submodelInterface, name, supermodelPin.logicWidth, supermodelPin.getRelX() / submodelScale,
				supermodelPin.getRelY() / submodelScale);

		submodelPin.addPinMovedListener(p ->
		{
			double newRelX = p.getRelX() * submodelScale;
			double newRelY = p.getRelY() * submodelScale;
			if (supermodelPin.getRelX() != newRelX || supermodelPin.getRelY() != newRelY)
				supermodelPin.setRelPos(newRelX, newRelY);
		});
		supermodelPin.addPinMovedListener(p ->
		{
			double newRelX = p.getRelX() / submodelScale;
			double newRelY = p.getRelY() / submodelScale;
			if (submodelPin.getRelX() != newRelX || submodelPin.getRelY() != newRelY)
				submodelPin.setRelPos(newRelX, newRelY);
		});

		submodelInterface.addPin(submodelPin);

		submodelPins.put(name, submodelPin);
		supermodelPins.put(name, supermodelPin);

		// no need to call requestRedraw() because addPin() will request a redraw
		return submodelPin;
	}

	/**
	 * Removes a submodel interface pin.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void removeSubmodelInterface(String name)
	{
		super.removePin(name);// do this first to be fail-fast if this component doesn't have a pin with the given name
		Pin submodelPin = submodelPins.remove(name);
		submodelInterface.removePin(submodelPin.name);
		supermodelPins.remove(name);

		// no need to call requestRedraw() because removePin() will request a redraw
	}

	/**
	 * Returns a collection of submodel interface pins on the submodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Map<String, Pin> getSubmodelPins()
	{
		return submodelUnmovablePinsUnmodifiable;
	}

	/**
	 * Returns the submodel interface pin with the given name on the submodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getSubmodelPin(String name)
	{
		return getSubmodelMovablePin(name);
	}

	/**
	 * Returns a collection of movable submodel interface pins on the submodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	protected Map<String, MovablePin> getSubmodelMovablePins()
	{
		return submodelMovablePinsUnmodifiable;
	}

	/**
	 * Returns the movable submodel interface pin with the given name on the submodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	protected MovablePin getSubmodelMovablePin(String name)
	{
		return submodelPins.get(name);
	}

	/**
	 * Returns a collection of submodel interface pins on the supermodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Map<String, Pin> getSupermodelPins()
	{
		return supermodelUnmovablePinsUnmodifiable;
	}

	/**
	 * Returns the submodel interface pin with the given name on the supermodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	public Pin getSupermodelPin(String name)
	{
		return getSupermodelMovablePin(name);
	}

	/**
	 * Returns a collection of movable submodel interface pins on the supermodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	protected Map<String, MovablePin> getSupermodelMovablePins()
	{
		return supermodelMovablePinsUnmodifiable;
	}

	/**
	 * Returns the movable submodel interface pin with the given name on the supermodel side of this component.
	 * 
	 * @author Daniel Kirschten
	 */
	protected MovablePin getSupermodelMovablePin(String name)
	{
		return supermodelPins.get(name);
	}

	// high-level access

	/**
	 * Adds the given subcomponent ID to the set of allowed subcomponent IDs and links the given {@link GUIComponent} as the delegate target
	 * for this subcomponent ID. <br>
	 * Note that this method does not affect whether {@link #setSubcomponentHighLevelState(String, String, Object)
	 * set}/{@link #getSubcomponentHighLevelState(String, String)} will be called. <br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about subcomponent IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void addHighLevelStateSubcomponentID(String subcomponentID, GUIComponent subcomponent)
	{
		checkHighLevelStateIDPart(subcomponentID);
		subcomponentsByHighLevelStateSubcomponentID.put(subcomponentID, subcomponent);
	}

	/**
	 * Removes the given subcomponent ID from the set of allowed subcomponent IDs. <br>
	 * Note that this method does not affect whether {@link #setSubcomponentHighLevelState(String, String, Object)
	 * set}/{@link #getSubcomponentHighLevelState(String, String)} will be called.<br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about subcomponent IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void removeHighLevelStateSubcomponentID(String subcomponentID)
	{
		subcomponentsByHighLevelStateSubcomponentID.remove(subcomponentID);
	}

	/**
	 * Adds the given atomic state ID to the set of allowed atomic state IDs. <br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about atomic state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void addAtomicHighLevelStateID(String stateID)
	{
		checkHighLevelStateIDPart(stateID);
		highLevelAtomicStates.add(stateID);
	}

	/**
	 * Removes the given atomic state ID from the set of allowed atomic state IDs. <br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about atomic state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void removeAtomicHighLevelStateID(String stateID)
	{
		highLevelAtomicStates.remove(stateID);
	}

	@Override
	public final void setHighLevelState(String stateID, Object newState)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
			if (highLevelAtomicStates.contains(stateID))
				setAtomicHighLevelState(stateID, newState);
			else
				super.setHighLevelState(stateID, newState);
		else
			setSubcomponentHighLevelState(stateID.substring(0, indexOfDot), stateID.substring(indexOfDot + 1), newState);
	}

	/**
	 * This method is called in {@link #setHighLevelState(String, Object)} when the state ID is not atomic. The default implementation uses
	 * the information given to {@link #addHighLevelStateSubcomponentID(String, GUIComponent)
	 * add}/{@link #removeHighLevelStateSubcomponentID(String)} to decide which subcomponent to delegate to.<br>
	 * Note that {@link #addHighLevelStateSubcomponentID(String, GUIComponent) add}/{@link #removeHighLevelStateSubcomponentID(String)}
	 * don't affect whether this method will be called.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void setSubcomponentHighLevelState(String subcomponentID, String subcomponentHighLevelStateID, Object newState)
	{
		GUIComponent subcomponent = subcomponentsByHighLevelStateSubcomponentID.get(subcomponentID);
		if (subcomponent != null)
			subcomponent.setHighLevelState(subcomponentHighLevelStateID, newState);
		else
			super.setHighLevelState(subcomponentID + "." + subcomponentHighLevelStateID, newState);
	}

	/**
	 * This method is called in {@link #setHighLevelState(String, Object)} when the state ID is atomic and in the set of allowed atomic
	 * state IDs. <br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about atomic state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings({ "static-method", "unused" }) // this method is intended to be overridden
	protected void setAtomicHighLevelState(String stateID, Object newState)
	{
		throw new IllegalStateException("Unknown high level state ID: " + stateID);
	}

	@Override
	public final Object getHighLevelState(String stateID)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
		{
			if (highLevelAtomicStates.contains(stateID))
				return getAtomicHighLevelState(stateID);
			return super.getHighLevelState(stateID);
		}
		return getSubcomponentHighLevelState(stateID.substring(0, indexOfDot), stateID.substring(indexOfDot + 1));
	}

	/**
	 * This method is called in {@link #getHighLevelState(String, Object)} when the state ID is not atomic. The default implementation uses
	 * the information given to {@link #addHighLevelStateSubcomponentID(String, GUIComponent)
	 * add}/{@link #removeHighLevelStateSubcomponentID(String)} to decide which subcomponent to delegate to. <br>
	 * Note that {@link #addHighLevelStateSubcomponentID(String, GUIComponent) add}/{@link #removeHighLevelStateSubcomponentID(String)}
	 * don't affect whether this method will be called.
	 * 
	 * @author Daniel Kirschten
	 */
	protected Object getSubcomponentHighLevelState(String subcomponentID, String subcomponentHighLevelStateID)
	{
		GUIComponent subcomponent = subcomponentsByHighLevelStateSubcomponentID.get(subcomponentID);
		if (subcomponent != null)
			return subcomponent.getHighLevelState(subcomponentHighLevelStateID);
		return super.getHighLevelState(subcomponentID + "." + subcomponentHighLevelStateID);
	}

	/**
	 * This method is called in {@link SubmodelComponent#getHighLevelState(String)} when the state ID is in the set of allowed atomic state
	 * IDs. <br>
	 * See {@link GUIComponent#setHighLevelState(String, Object)} for details about atomic state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings("static-method") // this method is intended to be overridden
	protected Object getAtomicHighLevelState(String stateID)
	{
		throw new IllegalStateException("Unknown high level state ID: " + stateID);
	}

	private static void checkHighLevelStateIDPart(String stateIDPart)
	{
		if (stateIDPart.indexOf('.') != -1)
			throw new IllegalArgumentException("Illegal high level state ID part (contains dot): " + stateIDPart);

	}

	// "graphical" operations

	/**
	 * Sets the factor by which the submodel is scaled when rendering and calls redrawListeners. Note that the submodel interface pins will
	 * stay at their position relative to the supermodel, which means they will move relative to the submodel.
	 * 
	 * @author Daniel Kirschten
	 */
	protected void setSubmodelScale(double submodelScale)
	{
		this.submodelScale = submodelScale;

		for (Entry<String, MovablePin> e : supermodelPins.entrySet())
			getSubmodelMovablePin(e.getKey()).setRelPos(e.getValue().getRelX() * submodelScale, e.getValue().getRelY() * submodelScale);

		requestRedraw();// needed if there is no submodel interface pin
	}

	/**
	 * Returns the current factor by which the submodel is scaled when rendering.
	 * 
	 * @author Daniel Kirschten
	 */
	protected double getSubmodelScale()
	{
		return submodelScale;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		GCConfig conf = new GCConfig(gc);
		TranslatedGC tgc = new TranslatedGC(gc, getPosX(), getPosY(), submodelScale, true);
		conf.reset(tgc);
		double visibleRegionFillRatio = Math.max(getWidth() / visibleRegion.width, getHeight() / visibleRegion.height);
		double alphaFactor = map(visibleRegionFillRatio, maxVisibleRegionFillRatioForAlpha0, minVisibleRegionFillRatioForAlpha1, 0, 1);
		alphaFactor = Math.max(0, Math.min(1, alphaFactor));
		// we need to take the old alpha into account to support nested submodules better.
		int oldAlpha = gc.getAlpha();
		int submodelAlpha = Math.max(0, Math.min(255, (int) (oldAlpha * alphaFactor)));
		int labelAlpha = Math.max(0, Math.min(255, (int) (oldAlpha * (1 - alphaFactor))));
		if (submodelAlpha != 0)
		{
			gc.setAlpha(submodelAlpha);
			renderer.render(tgc, visibleRegion.translate(getPosX() / submodelScale, getPosY() / submodelScale, 1 / submodelScale));
		}
		if (labelAlpha != 0)
		{
			gc.setAlpha(labelAlpha);
			renderSymbol(gc, visibleRegion);
		}
		conf.reset(gc);
		// draw the outline after all other operations to make interface pins look better
		renderOutline(gc, visibleRegion);
	}

	// TODO make this a path
	/**
	 * Render the outline of this {@link SubmodelComponent}, e.g. the graphical elements that should stay visible if the submodel is drawn.
	 * 
	 * @author Daniel Kirschten
	 */
	protected abstract void renderOutline(GeneralGC gc, Rectangle visibleRegion);

	/**
	 * Render the symbol of this {@link SubmodelComponent}, e.g. the things that should be hidden if the submodel is drawn.
	 * 
	 * @author Daniel Kirschten
	 */
	protected abstract void renderSymbol(GeneralGC gc, Rectangle visibleRegion);

	private static double map(double val, double valMin, double valMax, double mapMin, double mapMax)
	{
		return mapMin + (val - valMin) * (mapMax - mapMin) / (valMax - valMin);
	}

	@Override
	public boolean clicked(double x, double y)
	{
		double scaledX = (x - getPosX()) / submodelScale;
		double scaledY = (y - getPosY()) / submodelScale;
		for (GUIComponent component : submodel.getComponentsByName().values())
			if (component.getBounds().contains(scaledX, scaledY) && component.clicked(scaledX, scaledY))
				return true;
		return false;
	}

	// serializing

	// TODO move the methods below to serializing classes

	public SubmodelComponentParams calculateParams()
	{
		return calculateParams(c -> "class:" + c.getClass().getCanonicalName());
	}

	/**
	 * @return {@link SubmodelComponentParams}, which describe this {@link SubmodelComponent}.
	 */
	public SubmodelComponentParams calculateParams(Function<GUIComponent, String> getIdentifier)
	{
		SubmodelComponentParams params = new SubmodelComponentParams();
		params.submodel = calculateSubmodelParams(getIdentifier);

		params.width = getWidth();
		params.height = getHeight();

		InterfacePinParams[] iPins = new InterfacePinParams[getPins().size()];
		int i = 0;
		for (Pin p : getPins().values())
		{
			InterfacePinParams iPinParams = new InterfacePinParams();
			iPins[i] = iPinParams;
			iPinParams.location = p.getRelPos();
			iPinParams.name = p.name;
			iPinParams.logicWidth = p.logicWidth;
			i++;
		}
		params.interfacePins = iPins;
		return params;
	}

	private SubmodelParameters calculateSubmodelParams(Function<GUIComponent, String> getIdentifier)
	{
		SubmodelParameters params = new SubmodelParameters();
		params.innerScale = getSubmodelScale();

		Map<String, GUIComponent> components = new HashMap<>(submodel.getComponentsByName());
		components.remove(SUBMODEL_INTERFACE_NAME);
		InnerComponentParams[] comps = new InnerComponentParams[components.size()];
		int i = 0;
		for (GUIComponent component : components.values())
		{
			InnerComponentParams inner = new InnerComponentParams();
			comps[i] = inner;
			inner.pos = new Point(component.getPosX(), component.getPosY());
			inner.id = getIdentifier.apply(component);
			inner.params = component.getParams();
			inner.name = component.name;
			i++;
		}
		params.subComps = comps;

		List<GUIWire> wireList = submodel.getWires();
		InnerWireParams wires[] = new InnerWireParams[wireList.size()];
		i = 0;
		for (GUIWire wire : wireList)
		{
			InnerWireParams inner = new InnerWireParams();
			wires[i] = inner;
			InnerPinParams pin1Params = new InnerPinParams(), pin2Params = new InnerPinParams();

			pin1Params.pinName = wire.getPin1().name;
			pin1Params.compName = wire.getPin1().component.name;
			pin2Params.pinName = wire.getPin2().name;
			pin2Params.compName = wire.getPin2().component.name;
			inner.pin1 = pin1Params;
			inner.pin2 = pin2Params;
			inner.path = wire.getPath();
			i++;
		}
		params.innerWires = wires;
		return params;
	}

	// operations no longer supported

	@Override
	protected void addPin(Pin pin)
	{
		throw new UnsupportedOperationException("Can't add pins to a SubmodelComponent directly, call addSubmodelInterface instead");
	}

	@Override
	protected void removePin(String name)
	{
		throw new UnsupportedOperationException("Can't remove pins of a SubmodelComponent directly, call removeSubmodelInterface instead");
	}
}