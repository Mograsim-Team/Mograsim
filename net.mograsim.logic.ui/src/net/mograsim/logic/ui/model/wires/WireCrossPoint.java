package net.mograsim.logic.ui.model.wires;

import net.mograsim.logic.ui.ColorHelper;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public class WireCrossPoint extends GUIComponent
{
	private final Pin pin;

	private ReadEnd end;
	private final int logicWidth;

	public WireCrossPoint(ViewModel model, int logicWidth)
	{
		super(model);
		this.logicWidth = logicWidth;
		setSize(0, 0);
		addPin(this.pin = new Pin(this, logicWidth, 0, 0));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Rectangle bounds = getBounds();
		ColorHelper.executeWithDifferentBackground(gc, BitVectorFormatter.formatAsColor(end),
				() -> gc.fillOval(bounds.x - 1, bounds.y - 1, 2, 2));
	}

	public void setLogicModelBinding(ReadEnd end)
	{
		this.end = end;
		end.registerObserver((i) -> callComponentLookChangedListeners());
	}

	public int getLogicWidth()
	{
		return logicWidth;
	}

	public Pin getPin()
	{
		return pin;
	}
}