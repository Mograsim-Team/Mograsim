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
import net.mograsim.logic.ui.model.components.params.GeneralComponentParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams;
import net.mograsim.logic.ui.model.components.params.RectComponentParams.InnerComponentParams;
import net.mograsim.logic.ui.model.components.params.RectComponentParams.InnerPinParams;
import net.mograsim.logic.ui.model.components.params.RectComponentParams.InnerWireParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public abstract class SubmodelComponent extends GUIComponent
{
	protected final ViewModelModifiable submodelModifiable;
	public final ViewModel submodel;
	private final Map<PinMovable, PinMovable> submodelPinsPerSupermodelPin;
	private final Map<Pin, Pin> submodelPinsPerSupermodelPinUnmodifiable;
	private final Map<PinMovable, PinMovable> supermodelPinsPerSubmodelPin;
	private final Map<Pin, Pin> supermodelPinsPerSubmodelPinUnmodifiable;
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
		this.submodelPinsPerSupermodelPin = new HashMap<>();
		this.submodelPinsPerSupermodelPinUnmodifiable = Collections.unmodifiableMap(submodelPinsPerSupermodelPin);
		this.supermodelPinsPerSubmodelPin = new HashMap<>();
		this.supermodelPinsPerSubmodelPinUnmodifiable = Collections.unmodifiableMap(supermodelPinsPerSubmodelPin);
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

		for (Entry<PinMovable, PinMovable> e : supermodelPinsPerSubmodelPin.entrySet())
			e.getKey().setRelPos(e.getValue().getRelX() * submodelScale, e.getValue().getRelY() * submodelScale);

		requestRedraw();// needed if there is no submodel interface pin
	}

	protected double getSubmodelScale()
	{
		return submodelScale;
	}

	/**
	 * Returns the submodel pin.
	 */
	protected Pin addSubmodelInterface(int logicWidth, double relX, double relY)
	{
		PinMovable submodelPin = new PinMovable(submodelInterface, logicWidth, relX / submodelScale, relY / submodelScale);
		submodelInterface.addPin(submodelPin);

		PinMovable supermodelPin = new PinMovable(this, logicWidth, relX, relY);
		addPin(supermodelPin);

		submodelPinsPerSupermodelPin.put(supermodelPin, submodelPin);
		supermodelPinsPerSubmodelPin.put(submodelPin, supermodelPin);

		// no need to call requestRedraw() because addPin() will request a redraw
		return submodelPin;
	}

	protected void moveSubmodelInterface(Pin supermodelPin, double relX, double relY)
	{
		PinMovable submodelPin = getSubmodelMovablePin(supermodelPin);
		PinMovable supermodelPinMovable = getSupermodelMovablePin(submodelPin);

		submodelPin.setRelPos(relX / submodelScale, relY / submodelScale);
		supermodelPinMovable.setRelPos(relX, relY);

		// no need to call requestRedraw() because setRelPos() will request a redraw
	}

	protected void removeSubmodelInterface(Pin supermodelPin)
	{
		removePin(supermodelPin);
		Pin submodelPin = getSubmodelMovablePin(supermodelPin);
		submodelInterface.removePin(submodelPin);

		submodelPinsPerSupermodelPin.remove(supermodelPin);
		supermodelPinsPerSubmodelPin.remove(submodelPin);

		// no need to call requestRedraw() because removePin() will request a redraw
	}

	public Map<Pin, Pin> getSupermodelPinsPerSubmodelPin()
	{
		return supermodelPinsPerSubmodelPinUnmodifiable;
	}

	public Pin getSupermodelPin(Pin submodelPin)
	{
		return getSupermodelMovablePin(submodelPin);
	}

	protected PinMovable getSupermodelMovablePin(Pin submodelPin)
	{
		return supermodelPinsPerSubmodelPin.get(submodelPin);
	}

	public Map<Pin, Pin> getSubmodelPinsPerSupermodelPin()
	{
		return submodelPinsPerSupermodelPinUnmodifiable;
	}

	public Pin getSubmodelPin(Pin supermodelPin)
	{
		return getSubmodelMovablePin(supermodelPin);
	}

	protected PinMovable getSubmodelMovablePin(Pin supermodelPin)
	{
		return submodelPinsPerSupermodelPin.get(supermodelPin);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		GCConfig conf = new GCConfig(gc);
		TranslatedGC tgc = new TranslatedGC(gc, posX, posY, submodelScale, true);
		conf.reset(tgc);
		double visibleRegionFillRatio = Math.max(getBounds().width / visibleRegion.width, getBounds().height / visibleRegion.height);
		double alphaFactor = map(visibleRegionFillRatio, maxVisibleRegionFillRatioForAlpha0, minVisibleRegionFillRatioForAlpha1, 0, 1);
		alphaFactor = Math.max(0, Math.min(1, alphaFactor));
		// we need to take the old alpha into account to support nested submodules better.
		int oldAlpha = gc.getAlpha();
		int submodelAlpha = Math.max(0, Math.min(255, (int) (oldAlpha * alphaFactor)));
		int labelAlpha = Math.max(0, Math.min(255, (int) (oldAlpha * (1 - alphaFactor))));
		if (submodelAlpha != 0)
		{
			gc.setAlpha(submodelAlpha);
			renderer.render(tgc, visibleRegion.translate(posX / submodelScale, posY / submodelScale, 1 / submodelScale));
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
		// TODO
		double scaledX = (x - getBounds().x) / submodelScale;
		double scaledY = (y - getBounds().y) / submodelScale;
		double roundedScaledX = Math.round(scaledX / 5 * 2) * 5 / 2.;
		double roundedScaledY = Math.round(scaledY / 5 * 2) * 5 / 2.;
		System.out.println(scaledX + "|" + scaledY + ", rounded " + roundedScaledX + "|" + roundedScaledY);
		return true;
	}

	private static class PinMovable extends Pin
	{
		public PinMovable(GUIComponent component, int logicWidth, double relX, double relY)
		{
			super(component, logicWidth, relX, relY);
		}

		@Override
		protected void setRelPos(double relX, double relY)
		{
			super.setRelPos(relX, relY);
		}
	}

	public SubComponentParams calculateParams()
	{
		SubComponentParams params = new SubComponentParams();
		params.composition = calculateCompositionParams();

		Rectangle bounds = getBounds();
		params.width = bounds.width;
		params.height = bounds.height;

		List<Pin> pinList = pinsUnmodifiable;
		InterfacePinParams[] iPins = new InterfacePinParams[pinList.size()];
		int i = 0;
		for (Pin p : pinList)
		{
			InterfacePinParams iPinParams = new InterfacePinParams();
			iPins[i] = iPinParams;
			iPinParams.location = p.getRelPos();
			iPinParams.logicWidth = p.logicWidth;
			i++;
		}
		params.interfacePins = iPins;
		return params;
	}

	protected GeneralComponentParams calculateCompositionParams()
	{
		GeneralComponentParams params = new GeneralComponentParams();
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
			inner.logicWidth = component.getPins().get(0).logicWidth; // This could be done a little more elegantly
			Rectangle bounds = component.getBounds();
			inner.pos = new Point(bounds.x, bounds.y);
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

			pin1Params.pinIndex = wire.getPin1().component.getPins().indexOf(wire.getPin1());
			pin1Params.compId = compList.indexOf(wire.getPin1().component);
			pin2Params.pinIndex = wire.getPin2().component.getPins().indexOf(wire.getPin2());
			pin2Params.compId = compList.indexOf(wire.getPin2().component);
			inner.pin1 = pin1Params;
			inner.pin2 = pin2Params;
			inner.path = wire.getPath();
			i++;
		}
		params.innerWires = wires;
		return params;
	}
}