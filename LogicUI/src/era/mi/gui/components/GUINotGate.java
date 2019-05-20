package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.gates.NotGate;
import era.mi.logic.wires.Wire.WireEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUINotGate extends NotGate implements BasicGUIComponent
{
	private static final String LABEL = "\u22651";// >=1

	private final List<WireEnd> connectedWireEnds;
	private final List<Point> WireEndConnectionPoints;

	public GUINotGate(int processTime, WireEnd in, WireEnd out)
	{
		super(processTime, in, out);

		List<WireEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> WireEndConnectionPointsModifiable = new ArrayList<>();

		connectedWireEndsModifiable.add(in);
		WireEndConnectionPointsModifiable.add(new Point(0, 5));

		connectedWireEndsModifiable.add(out);
		WireEndConnectionPointsModifiable.add(new Point(20, 5));

		this.connectedWireEnds = Collections.unmodifiableList(connectedWireEndsModifiable);
		this.WireEndConnectionPoints = Collections.unmodifiableList(WireEndConnectionPointsModifiable);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 20, 10);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawRectangle(0, 0, 17, 10);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 5, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(LABEL);
		gc.drawText(LABEL, 8.5 - textExtent.x / 2, 5 - textExtent.y / 2, true);
		gc.setFont(oldFont);
		gc.drawOval(17, 3.5, 3, 3);
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