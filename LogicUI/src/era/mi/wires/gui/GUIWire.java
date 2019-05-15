package era.mi.wires.gui;

import java.util.Objects;

import era.mi.components.gui.BasicGUIComponent;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUIWire
{
	private final WireArray	wa;
	private final double[]	path;

	public GUIWire(Runnable redraw, BasicGUIComponent component1, int component1ConnectionIndex, Point component1Pos, BasicGUIComponent component2, int component2ConnectionIndex, Point component2Pos, Point... path)
	{
		this.wa = component1.getConnectedWireArray(component1ConnectionIndex);
		if(!Objects.equals(wa, component2.getConnectedWireArray(component2ConnectionIndex)))
			throw new IllegalArgumentException("Given connection points are not connected!");
		this.path = new double[path.length * 2 + 4];
		Point component1ConnectionPoint = component1.getWireArrayConnectionPoint(component1ConnectionIndex);
		this.path[0] = component1Pos.x + component1ConnectionPoint.x;
		this.path[1] = component1Pos.y + component1ConnectionPoint.y;
		for(int srcI = 0, dstI = 2; srcI < path.length; srcI ++, dstI += 2)
		{
			this.path[dstI + 0] = path[srcI].x;
			this.path[dstI + 1] = path[srcI].y;
		}
		Point component2ConnectionPoint = component2.getWireArrayConnectionPoint(component2ConnectionIndex);
		this.path[this.path.length - 2] = component2Pos.x + component2ConnectionPoint.x;
		this.path[this.path.length - 1] = component2Pos.y + component2ConnectionPoint.y;

		wa.addObserver((initiator, oldValues) -> redraw.run());
	}

	public void render(GeneralGC gc)
	{
		gc.drawPolyline(path);
	}
}