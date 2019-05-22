package era.mi.gui.wires;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import era.mi.gui.components.BasicGUIComponent;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUIWire
{
	private final Wire wire;
	private final double[] path;

	public GUIWire(Runnable redraw, BasicGUIComponent component1, int component1ConnectionIndex, Point component1Pos,
			BasicGUIComponent component2, int component2ConnectionIndex, Point component2Pos, Point... path)
	{
		this.wire = component1.getConnectedWireEnd(component1ConnectionIndex).getWire();
		if (!Objects.equals(wire, component2.getConnectedWireEnd(component2ConnectionIndex).getWire()))
			throw new IllegalArgumentException("Given connection points are not connected!");
		this.path = new double[path.length * 2 + 4];
		Point component1ConnectionPoint = component1.getWireEndConnectionPoint(component1ConnectionIndex);
		this.path[0] = component1Pos.x + component1ConnectionPoint.x;
		this.path[1] = component1Pos.y + component1ConnectionPoint.y;
		for (int srcI = 0, dstI = 2; srcI < path.length; srcI++, dstI += 2)
		{
			this.path[dstI + 0] = path[srcI].x;
			this.path[dstI + 1] = path[srcI].y;
		}
		Point component2ConnectionPoint = component2.getWireEndConnectionPoint(component2ConnectionIndex);
		this.path[this.path.length - 2] = component2Pos.x + component2ConnectionPoint.x;
		this.path[this.path.length - 1] = component2Pos.y + component2ConnectionPoint.y;

		wire.addObserver((initiator, oldValues) -> redraw.run());
	}

	public void render(GeneralGC gc)
	{
		Color oldFG = gc.getForeground();
		if (wire.length == 1)
			gc.setForeground(gc.getDevice().getSystemColor(getSWTColorConstantForBit(wire.getValue())));
		gc.drawPolyline(path);
		gc.setForeground(oldFG);
	}

	public static int getSWTColorConstantForBit(Bit bit)
	{
		switch (bit)
		{
		case ONE:
			return SWT.COLOR_GREEN;
		case ZERO:
			return SWT.COLOR_BLUE;
		case Z:
			return SWT.COLOR_BLACK;
		case U:
		case X:
			return SWT.COLOR_RED;
		default:
			throw new IllegalArgumentException("Unknown enum constant: " + bit);
		}
	}
}