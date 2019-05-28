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
	private ReadEnd end;

	public WireCrossPoint(ViewModel model)
	{
		super(model);
		setSize(0, 0);
		addPin(new Pin(this, 0, 0));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		ColorHelper.executeWithDifferentBackground(gc, BitVectorFormatter.formatAsColor(end), () -> gc.fillOval(-1, -1, 2, 2));
	}

	public void setLogicModelBinding(ReadEnd end)
	{
		this.end = end;
		end.addObserver((i, o) -> callComponentChangedListeners());
	}
}