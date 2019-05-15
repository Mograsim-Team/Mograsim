package era.mi.components.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.gates.AndGate;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUIAndGate extends AndGate implements BasicGUIComponent
{
	private static final String LABEL = "&";

	private final int				inputCount;
	private final double			height;
	private final List<WireArray>	connectedWireArrays;
	private final List<Point>		wireArrayConnectionPoints;

	public GUIAndGate(int processTime, WireArray out, WireArray... in)
	{
		super(processTime, out, in);

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		this.inputCount = in.length;
		this.height = inputCount * 10;

		{
			connectedWireArraysModifiable.addAll(Arrays.asList(in));
			double inputHeight = 5;
			for(int i = 0; i < inputCount; i ++, inputHeight += 10)
				wireArrayConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		connectedWireArraysModifiable.add(out);
		wireArrayConnectionPointsModifiable.add(new Point(20, height / 2));

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawRectangle(0, 0, 17, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 5, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(LABEL);
		gc.drawText(LABEL, 8.5 - textExtent.x / 2, (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
		gc.drawOval(17, height / 2 - 1.5, 3, 3);
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