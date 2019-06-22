package net.mograsim.logic.ui.model.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.haspamelodica.swt.helper.gcs.GCConfig;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.LogicUIRenderer;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.ComponentCompositionParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.ComponentCompositionParams.InnerComponentParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.InnerPinParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.InnerWireParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.model.wires.Pin;

public abstract class SubmodelComponent extends GUIComponent
{
	protected final ViewModelModifiable submodelModifiable;
	public final ViewModel submodel;
	private final Map<String, MovablePin> submodelPins;
	private final Map<String, MovablePin> submodelMovablePinsUnmodifiable;
	private final Map<String, Pin> submodelUnmovablePinsUnmodifiable;
	private final Map<String, MovablePin> supermodelPins;
	private final Map<String, MovablePin> supermodelMovablePinsUnmodifiable;
	private final Map<String, Pin> supermodelUnmovablePinsUnmodifiable;
	private final SubmodelInterface submodelInterface;

	private double submodelScale;
	private double maxVisibleRegionFillRatioForAlpha0;
	private double minVisibleRegionFillRatioForAlpha1;
	private final LogicUIRenderer renderer;

	public SubmodelComponent(ViewModelModifiable model)
	{
		super(model);
		this.submodelModifiable = new ViewModelModifiable();
		this.submodel = submodelModifiable;
		this.submodelPins = new HashMap<>();
		this.submodelMovablePinsUnmodifiable = Collections.unmodifiableMap(submodelPins);
		this.submodelUnmovablePinsUnmodifiable = Collections.unmodifiableMap(submodelPins);
		this.supermodelPins = new HashMap<>();
		this.supermodelMovablePinsUnmodifiable = Collections.unmodifiableMap(supermodelPins);
		this.supermodelUnmovablePinsUnmodifiable = Collections.unmodifiableMap(supermodelPins);
		this.submodelInterface = new SubmodelInterface(submodelModifiable);

		this.submodelScale = 1;
		this.maxVisibleRegionFillRatioForAlpha0 = 0.4;
		this.minVisibleRegionFillRatioForAlpha1 = 0.8;
		this.renderer = new LogicUIRenderer(submodelModifiable);

		submodelModifiable.addRedrawListener(this::requestRedraw);
	}

	protected void setSubmodelScale(double submodelScale)
	{
		this.submodelScale = submodelScale;

		for (Entry<String, MovablePin> e : supermodelPins.entrySet())
			getSubmodelMovablePin(e.getKey()).setRelPos(e.getValue().getRelX() * submodelScale, e.getValue().getRelY() * submodelScale);

		requestRedraw();// needed if there is no submodel interface pin
	}

	protected double getSubmodelScale()
	{
		return submodelScale;
	}

	/**
	 * Returns the submodel pin.
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

	protected void removeSubmodelInterface(String name)
	{
		super.removePin(name);
		Pin submodelPin = getSubmodelMovablePin(name);
		submodelInterface.removePin(submodelPin.name);

		submodelPins.remove(name);
		supermodelPins.remove(name);

		// no need to call requestRedraw() because removePin() will request a redraw
	}

	public Map<String, Pin> getSubmodelPins()
	{
		return submodelUnmovablePinsUnmodifiable;
	}

	public Pin getSubmodelPin(String name)
	{
		return getSubmodelMovablePin(name);
	}

	protected Map<String, MovablePin> getSubmodelMovablePins()
	{
		return submodelMovablePinsUnmodifiable;
	}

	protected MovablePin getSubmodelMovablePin(String name)
	{
		return submodelPins.get(name);
	}

	public Map<String, Pin> getSupermodelPins()
	{
		return supermodelUnmovablePinsUnmodifiable;
	}

	public Pin getSupermodelPin(String name)
	{
		return getSupermodelMovablePin(name);
	}

	protected Map<String, MovablePin> getSupermodelMovablePins()
	{
		return supermodelMovablePinsUnmodifiable;
	}

	protected MovablePin getSupermodelMovablePin(String name)
	{
		return supermodelPins.get(name);
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

	protected abstract void renderOutline(GeneralGC gc, Rectangle visibleRegion);

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
		for (GUIComponent component : submodel.getComponents())
			if (component.getBounds().contains(scaledX, scaledY) && component.clicked(scaledX, scaledY))
				return true;
		return false;
	}

	/**
	 * @return {@link SubmodelComponentParams}, which describe this {@link SubmodelComponent}.
	 */
	public SubmodelComponentParams calculateParams()
	{
		SubmodelComponentParams params = new SubmodelComponentParams();
		params.type = SubmodelComponent.class.getSimpleName();
		params.composition = calculateCompositionParams();

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

	protected ComponentCompositionParams calculateCompositionParams()
	{
		ComponentCompositionParams params = new ComponentCompositionParams();
		params.innerScale = getSubmodelScale();

		List<GUIComponent> compList = submodelModifiable.getComponents();
		Iterator<GUIComponent> componentIt = compList.iterator();
		componentIt.next(); // Skip inner SubmodelInterface
		InnerComponentParams[] comps = new InnerComponentParams[compList.size() - 1];
		int i = 0;
		while (componentIt.hasNext())
		{
			GUIComponent component = componentIt.next();
			InnerComponentParams inner = new InnerComponentParams();
			comps[i] = inner;
			inner.params = component.getInstantiationParameters();
			inner.pos = new Point(getPosX(), getPosY());
			inner.type = component.getIdentifier();
			i++;
		}
		params.subComps = comps;

		List<GUIWire> wireList = submodelModifiable.getWires();
		InnerWireParams wires[] = new InnerWireParams[wireList.size()];
		i = 0;
		for (GUIWire wire : wireList)
		{
			InnerWireParams inner = new InnerWireParams();
			wires[i] = inner;
			InnerPinParams pin1Params = new InnerPinParams(), pin2Params = new InnerPinParams();

			pin1Params.pinName = wire.getPin1().name;
			pin1Params.compId = compList.indexOf(wire.getPin1().component);
			pin2Params.pinName = wire.getPin2().name;
			pin2Params.compId = compList.indexOf(wire.getPin2().component);
			inner.pin1 = pin1Params;
			inner.pin2 = pin2Params;
			inner.path = wire.getPath();
			i++;
		}
		params.innerWires = wires;
		return params;
	}

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