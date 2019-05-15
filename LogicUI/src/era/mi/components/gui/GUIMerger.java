package era.mi.components.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Merger;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIMerger extends Merger implements BasicGUIComponent
{
	private final int				inputCount;
	private final double			height;
	private final List<WireArray>	connectedWireArrays;
	private final List<Point>		wireArrayConnectionPoints;

	public GUIMerger(WireArray union, WireArray... inputs)
	{
		super(union, inputs);

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		this.inputCount = inputs.length;
		this.height = (inputCount - 1) * 10;

		{
			connectedWireArraysModifiable.addAll(Arrays.asList(inputs));
			double inputHeight = 0;
			for(int i = 0; i < inputCount; i ++, inputHeight += 10)
				wireArrayConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		connectedWireArraysModifiable.add(union);
		wireArrayConnectionPointsModifiable.add(new Point(20, height / 2));

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
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
		for(int i = 0; i < inputCount; i ++, inputHeight += 10)
			gc.drawLine(0, inputHeight, 10, inputHeight);
		gc.drawLine(10, 0, 10, height);
		gc.drawLine(10, height / 2, 20, height / 2);
	}

	@Override
	public int getConnectedWireArraysCount()
	{
		return connectedWireArrays.size();
	}
	@Override
	public WireArray getConnectedWireArray(int connectionIndex)
	{
		return connectedWireArrays.get(connectionIndex);
	}
	@Override
	public Point getWireArrayConnectionPoint(int connectionI)
	{
		return wireArrayConnectionPoints.get(connectionI);
	}
}