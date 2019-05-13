package era.mi.components.gui;

import java.util.List;

import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public interface BasicGUIComponent
{
	public void render(GeneralGC gc);

	public int getConnectedWireArraysCount();
	public WireArray getConnectedWireArray(int connectionIndex);
	public Point getWireArrayConnectionPoint(int connectionIndex);
}