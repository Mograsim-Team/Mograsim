package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Mux;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIMux extends Mux implements BasicGUIComponent
{
	private final double height;
	private final List<ReadEnd> connectedWireEnds;
	private final List<Point> WireEndConnectionPoints;

	public GUIMux(int processTime, ReadWriteEnd out, ReadEnd select, ReadEnd... inputs)
	{
		super(processTime, out, select, inputs);

		double height = inputs.length * 5;
		if (height < 10)
			height = 10;
		this.height = height;

		List<ReadEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> WireEndConnectionPointsModifiable = new ArrayList<>();

		connectedWireEndsModifiable.add(out);
		WireEndConnectionPointsModifiable.add(new Point(20, 10 + height / 2));

		connectedWireEndsModifiable.add(select);
		WireEndConnectionPointsModifiable.add(new Point(10, 5));

		{
			connectedWireEndsModifiable.addAll(Arrays.asList(inputs));
			double inputHeightIncrement = (height + 20) / inputs.length;
			double inputHeight = inputHeightIncrement / 2;
			for (int i = 0; i < inputs.length; i++, inputHeight += inputHeightIncrement)
				WireEndConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		this.connectedWireEnds = Collections.unmodifiableList(connectedWireEndsModifiable);
		this.WireEndConnectionPoints = Collections.unmodifiableList(WireEndConnectionPointsModifiable);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 20, height + 20);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawPolygon(new double[] { 0, 0, 20, 10, 20, height + 10, 0, height + 20 });
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