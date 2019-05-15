package era.mi.components.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Splitter;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUISplitter extends Splitter implements BasicGUIComponent
{
	private final int				outputCount;
	private final double			height;
	private final List<WireArray>	connectedWireArrays;
	private final List<Point>		wireArrayConnectionPoints;

	public GUISplitter(WireArray input, WireArray... outputs)
	{
		super(input, outputs);

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		this.outputCount = outputs.length;
		this.height = (outputCount - 1) * 10;

		connectedWireArraysModifiable.add(input);
		wireArrayConnectionPointsModifiable.add(new Point(0, height / 2));

		{
			connectedWireArraysModifiable.addAll(Arrays.asList(outputs));
			double outputHeight = 0;
			for(int i = 0; i < outputCount; i ++, outputHeight += 10)
				wireArrayConnectionPointsModifiable.add(new Point(20, outputHeight));
		}

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawLine(0, height / 2, 10, height / 2);
		gc.drawLine(10, 0, 10, height);
		double outputHeight = 0;
		for(int i = 0; i < outputCount; i ++, outputHeight += 10)
			gc.drawLine(10, outputHeight, 20, outputHeight);
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