package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.Mux;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIMux extends Mux implements BasicGUIComponent
{
	private final double height;
	private final List<WireArray> connectedWireArrays;
	private final List<Point> wireArrayConnectionPoints;

	public GUIMux(int processTime, WireArray out, WireArray select, WireArray... inputs)
	{
		super(processTime, out, select, inputs);

		double height = inputs.length * 5;
		if (height < 10)
			height = 10;
		this.height = height;

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		connectedWireArraysModifiable.add(out);
		wireArrayConnectionPointsModifiable.add(new Point(20, 10 + height / 2));

		connectedWireArraysModifiable.add(select);
		wireArrayConnectionPointsModifiable.add(new Point(10, 5));

		{
			connectedWireArraysModifiable.addAll(Arrays.asList(inputs));
			double inputHeightIncrement = (height + 20) / inputs.length;
			double inputHeight = inputHeightIncrement / 2;
			for (int i = 0; i < inputs.length; i++, inputHeight += inputHeightIncrement)
				wireArrayConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
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