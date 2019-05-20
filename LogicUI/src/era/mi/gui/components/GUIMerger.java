package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Merger;
import era.mi.logic.wires.Wire.WireEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIMerger extends Merger implements BasicGUIComponent
{
	private final int inputCount;
	private final double height;
	private final List<WireEnd> connectedWireEnds;
	private final List<Point> WireEndConnectionPoints;

	public GUIMerger(WireEnd union, WireEnd... inputs)
	{
		super(union, inputs);

		List<WireEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> WireEndConnectionPointsModifiable = new ArrayList<>();

		this.inputCount = inputs.length;
		this.height = (inputCount - 1) * 10;

		{
			connectedWireEndsModifiable.addAll(Arrays.asList(inputs));
			double inputHeight = 0;
			for (int i = 0; i < inputCount; i++, inputHeight += 10)
				WireEndConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		connectedWireEndsModifiable.add(union);
		WireEndConnectionPointsModifiable.add(new Point(20, height / 2));

		this.connectedWireEnds = Collections.unmodifiableList(connectedWireEndsModifiable);
		this.WireEndConnectionPoints = Collections.unmodifiableList(WireEndConnectionPointsModifiable);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 20, height);
	}

	@Override
	public void render(GeneralGC gc)
	{
		double inputHeight = 0;
		for (int i = 0; i < inputCount; i++, inputHeight += 10)
			gc.drawLine(0, inputHeight, 10, inputHeight);
		gc.drawLine(10, 0, 10, height);
		gc.drawLine(10, height / 2, 20, height / 2);
	}

	@Override
	public int getConnectedWireEndsCount()
	{
		return connectedWireEnds.size();
	}

	@Override
	public WireEnd getConnectedWireEnd(int connectionIndex)
	{
		return connectedWireEnds.get(connectionIndex);
	}

	@Override
	public Point getWireEndConnectionPoint(int connectionI)
	{
		return WireEndConnectionPoints.get(connectionI);
	}
}