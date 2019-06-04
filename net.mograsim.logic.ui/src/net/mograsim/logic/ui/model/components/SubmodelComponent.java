package net.mograsim.logic.ui.model.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.haspamelodica.swt.helper.gcs.GCConfig;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.LogicUIRenderer;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.Pin;

public class SubmodelComponent extends GUIComponent
{
	protected final ViewModelModifiable submodelModifiable;
	public final ViewModel submodel;
	private final Map<PinMovable, PinMovable> submodelPinsPerSupermodelPin;
	private final Map<Pin, Pin> submodelPinsPerSupermodelPinUnmodifiable;
	private final Map<PinMovable, PinMovable> supermodelPinsPerSubmodelPin;
	private final Map<Pin, Pin> supermodelPinsPerSubmodelPinUnmodifiable;
	private final SubmodelInterface submodelInterface;

	private final String label;
	private double submodelScale;
	private double maxVisibleRegionFillRatioForAlpha0;
	private double minVisibleRegionFillRatioForAlpha1;
	private final LogicUIRenderer renderer;

	public SubmodelComponent(ViewModelModifiable model, String label)
	{
		super(model);
		this.submodelModifiable = new ViewModelModifiable();
		this.submodel = submodelModifiable;
		this.submodelPinsPerSupermodelPin = new HashMap<>();
		this.submodelPinsPerSupermodelPinUnmodifiable = Collections.unmodifiableMap(submodelPinsPerSupermodelPin);
		this.supermodelPinsPerSubmodelPin = new HashMap<>();
		this.supermodelPinsPerSubmodelPinUnmodifiable = Collections.unmodifiableMap(supermodelPinsPerSubmodelPin);
		this.submodelInterface = new SubmodelInterface(submodelModifiable);

		this.label = label;
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
		gc.setAlpha(Math.max(0, Math.min(255, (int) (oldAlpha * alphaFactor))));
		renderer.render(tgc, visibleRegion.translate(-posX, -posY, 1 / submodelScale));
		gc.setAlpha(Math.max(0, Math.min(255, (int) (oldAlpha * (1 - alphaFactor)))));
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 6, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (getBounds().width - textExtent.x) / 2, posY + (getBounds().height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
		conf.reset(gc);
		// draw the "bounding box" after all other operations to make interface pins look better
		gc.drawRectangle(getBounds());
	}

	private static double map(double val, double valMin, double valMax, double mapMin, double mapMax)
	{
		return mapMin + (val - valMin) * (mapMax - mapMin) / (valMax - valMin);
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
}