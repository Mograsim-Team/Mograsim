package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Splitter;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUISplitter extends Splitter implements BasicGUIComponent
{
	private final int outputCount;
	private final double height;
	private final List<ReadEnd> connectedWireEnds;
	private final List<Point> WireEndConnectionPoints;

	public GUISplitter(ReadEnd input, ReadWriteEnd... outputs)
	{
		super(input, outputs);

		List<ReadEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> WireEndConnectionPointsModifiable = new ArrayList<>();

		this.outputCount = outputs.length;
		this.height = (outputCount - 1) * 10;

		connectedWireEndsModifiable.add(input);
		WireEndConnectionPointsModifiable.add(new Point(0, height / 2));

		{
			connectedWireEndsModifiable.addAll(Arrays.asList(outputs));
			double outputHeight = 0;
			for (int i = 0; i < outputCount; i++, outputHeight += 10)
				WireEndConnectionPointsModifiable.add(new Point(20, outputHeight));
		}

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
		gc.drawLine(0, height / 2, 10, height / 2);
		gc.drawLine(10, 0, 10, height);
		double outputHeight = 0;
		for (int i = 0; i < outputCount; i++, outputHeight += 10)
			gc.drawLine(10, outputHeight, 20, outputHeight);
	}

	@Override
	public int getConnectedWireEndsCount()
	{
		return connectedWireEnds.size();
	}

	@Override
	public ReadEnd getConnectedWireEnd(int connectionIndex)
	{
		return connectedWireEnds.get(connectionIndex);
	}

	@Override
	public Point getWireEndConnectionPoint(int connectionI)
	{
		return WireEndConnectionPoints.get(connectionI);
	}
}