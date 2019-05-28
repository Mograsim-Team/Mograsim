package era.mi.gui.model.wires;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import era.mi.gui.model.ViewModel;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUIWire
{
	private final ViewModel model;
	private Pin pin1;
	private Pin pin2;
	private double[] path;

	private Wire wire;

	public GUIWire(ViewModel model, Pin pin1, Pin pin2, Point... path)
	{
		this.model = model;
		this.path = new double[path.length * 2 + 4];
		for (int srcI = 0, dstI = 2; srcI < path.length; srcI++, dstI += 2)
		{
			this.path[dstI + 0] = path[srcI].x;
			this.path[dstI + 1] = path[srcI].y;
		}

		this.pin1 = pin1;
		this.pin2 = pin2;

		pin1.addPinMovedListener(p -> pin1Moved());
		pin2.addPinMovedListener(p -> pin2Moved());
		pin1Moved();
		pin2Moved();

		model.wireCreated(this);
	}

	private void pin1Moved()
	{
		Point pos = pin1.getPos();
		this.path[0] = pos.x;
		this.path[1] = pos.y;
	}

	private void pin2Moved()
	{
		Point pos = pin2.getPos();
		this.path[this.path.length - 2] = pos.x;
		this.path[this.path.length - 1] = pos.y;
	}

	public void destroy()
	{
		model.wireDestroyed(this);
	}

	public void render(GeneralGC gc)
	{
		Color oldFG = gc.getForeground();
		gc.setForeground(gc.getDevice().getSystemColor(getSWTColorConstantForWire(wire)));
		gc.drawPolyline(path);
		gc.setForeground(oldFG);
	}

	public void setLogicModelWire(Wire wire)
	{
		this.wire = wire;
	}

	public static int getSWTColorConstantForWire(Wire wire)
	{
		if (wire != null && wire.length == 1)
			return getSWTColorConstantForBit(wire.getValue());
		else
			return SWT.COLOR_BLACK;
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