package era.mi.gui.wires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import era.mi.gui.components.BasicGUIComponent;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class WireConnectionPoint implements BasicGUIComponent
{
	private final Wire wire;
	private final List<WireEnd> wireEnds;
	private final int wiresCrossing;

	public WireConnectionPoint(Wire wire, int wiresCrossing)
	{
		this.wire = wire;
		List<WireEnd> wireEndsModifiable = new ArrayList<>();
		for (int i = 0; i < wiresCrossing; i++)
			wireEndsModifiable.add(wire.createReadOnlyEnd());
		wireEnds = Collections.unmodifiableList(wireEndsModifiable);
		this.wiresCrossing = wiresCrossing;
	}

	@Override
	public void render(GeneralGC gc)
	{
		Color oldBG = gc.getBackground();
		if (wire.length == 1)
			gc.setBackground(gc.getDevice().getSystemColor(GUIWire.getSWTColorConstantForBit(wire.getValue())));
		gc.fillOval(-1, -1, 2, 2);
		gc.setBackground(oldBG);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 0, 0);
	}

	@Override
	public int getConnectedWireEndsCount()
	{
		return wiresCrossing;
	}

	@Override
	public WireEnd getConnectedWireEnd(int connectionIndex)
	{
		return wireEnds.get(connectionIndex);
	}

	@Override
	public Point getWireEndConnectionPoint(int connectionIndex)
	{
		return new Point(0, 0);
	}
}