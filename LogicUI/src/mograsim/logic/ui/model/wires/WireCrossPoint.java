package mograsim.logic.ui.model.wires;

import mograsim.logic.core.types.BitVectorFormatter;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.ui.ColorHelper;
import mograsim.logic.ui.model.ViewModel;
import mograsim.logic.ui.model.components.GUIComponent;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

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
		end.addObserver((i, o) -> callComponentLookChangedListeners());
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