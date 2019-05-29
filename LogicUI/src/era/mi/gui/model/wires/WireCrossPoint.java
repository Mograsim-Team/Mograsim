package era.mi.gui.model.wires;

import era.mi.gui.ColorHelper;
import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIComponent;
import era.mi.logic.types.BitVectorFormatter;
import era.mi.logic.wires.Wire.ReadEnd;
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