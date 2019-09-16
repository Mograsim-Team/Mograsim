package net.mograsim.logic.model.snippets.symbolrenderers;

import java.util.HashMap;
import java.util.Map;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class PinNamesSymbolRenderer implements Renderer
{
	private final ModelComponent component;
	private final Map<Pin, Position> pinPositions;
	private final double pinLabelHeight;
	private final double pinLabelMargin;

	public PinNamesSymbolRenderer(ModelComponent component, PinNamesParams params)
	{
		this.component = component;
		this.pinPositions = new HashMap<>();
		this.pinLabelHeight = params.pinLabelHeight;
		this.pinLabelMargin = params.pinLabelMargin;
		if (params.pinNamePositions != null)
			params.pinNamePositions.forEach(this::setPinPosition);
		component.addPinRemovedListener(p -> setPinPosition(p, null));
	}

	public void setPinPosition(String pinName, Position position)
	{
		setPinPosition(component.getPin(pinName), position);
	}

	public void setPinPosition(Pin pin, Position position)
	{
		if (position == null)
			pinPositions.remove(pin);
		else
			pinPositions.put(pin, position);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), pinLabelHeight, oldFont.getStyle()));
		for (Pin pin : component.getPins().values())
		{
			Position pos = pinPositions.get(pin);
			if (pos == null)
				pos = Position.RIGHT;

			Point topleft = pin.getPos();
			Point textExtent = gc.textExtent(pin.name);

			double x2 = topleft.x - textExtent.x - pinLabelMargin;
			double y2 = topleft.y - textExtent.y - pinLabelMargin;
			double x1 = topleft.x + pinLabelMargin;
			double y1 = topleft.y + pinLabelMargin;

			double x = pos.posX * x1 + (1 - pos.posX) * x2;
			double y = pos.posY * y1 + (1 - pos.posY) * y2;

			gc.drawText(pin.name, x, y, true);
		}
		gc.setFont(oldFont);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "pinNames";
	}

	@Override
	public PinNamesParams getParamsForSerializing(IdentifyParams idParams)
	{
		PinNamesParams params = new PinNamesParams();
		params.pinNamePositions = new HashMap<>();
		pinPositions.forEach((pin, pos) -> params.pinNamePositions.put(pin.name, pos));
		return params;
	}

	public static class PinNamesParams
	{
		public Map<String, Position> pinNamePositions;
		public double pinLabelHeight;
		public double pinLabelMargin;

		public static enum Position
		{
			TOP(.5, 0), TOP_LEFT(0, 0), LEFT(0, .5), BOTTOM_LEFT(0, 1), BOTTOM(.5, 1), BOTTOM_RIGHT(1, 1), RIGHT(1, .5), TOP_RIGHT(1, 0);

			private final double posX, posY;

			private Position(double posX, double posY)
			{
				this.posX = posX;
				this.posY = posY;
			}
		}
	}

	static
	{
		SubmodelComponentSnippetSuppliers.symbolRendererSupplier.setSnippetSupplier(PinNamesSymbolRenderer.class.getCanonicalName(),
				SnippetDefinintion.create(PinNamesParams.class, PinNamesSymbolRenderer::new));
	}
}