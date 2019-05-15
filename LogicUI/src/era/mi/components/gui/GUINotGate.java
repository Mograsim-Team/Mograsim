package era.mi.components.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.gates.NotGate;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUINotGate extends NotGate implements BasicGUIComponent
{
	private static final String LABEL = "\u22651";//>=1

	private final List<WireArray>	connectedWireArrays;
	private final List<Point>		wireArrayConnectionPoints;

	public GUINotGate(int processTime, WireArray in, WireArray out)
	{
		super(processTime, in, out);

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		connectedWireArraysModifiable.add(in);
		wireArrayConnectionPointsModifiable.add(new Point(0, 5));

		connectedWireArraysModifiable.add(out);
		wireArrayConnectionPointsModifiable.add(new Point(20, 5));

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawRectangle(0, 0, 17, 10);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 5, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(LABEL);
		gc.drawText(LABEL, 10 - textExtent.x / 2, 5 - textExtent.y / 2, true);
		gc.setFont(oldFont);
		gc.drawOval(17, 3.5, 3, 3);
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