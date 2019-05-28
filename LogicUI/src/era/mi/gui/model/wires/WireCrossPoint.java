package era.mi.gui.model.wires;

import org.eclipse.swt.graphics.Color;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIComponent;
import era.mi.logic.wires.Wire;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class WireCrossPoint extends GUIComponent
{
	private Wire wire;

	public WireCrossPoint(ViewModel model)
	{
		super(model);
		setSize(0, 0);
		addPin(new Pin(this, 0, 0));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color oldBG = gc.getBackground();
		gc.setBackground(gc.getDevice().getSystemColor(GUIWire.getSWTColorConstantForWire(wire)));
		gc.fillOval(-1, -1, 2, 2);
		gc.setBackground(oldBG);
	}

	public void setLogicModelWire(Wire wire)
	{
		this.wire = wire;
	}
}