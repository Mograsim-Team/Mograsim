package net.mograsim.logic.model.model.components.submodels;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.haspamelodica.swt.helper.gcs.GCConfig;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.LogicUIRenderer;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;

/**
 * A {@link GUIComponent} consisting of another model. A <code>SubmodelComponent</code> can have so-called "interface pins" connecting the
 * inner and outer models.
 */
//TODO override getParams
public abstract class SubmodelComponent extends GUIComponent
{
	public static final String SUBMODEL_INTERFACE_NAME = "_submodelinterface";
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
	public double getSubmodelScale()
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