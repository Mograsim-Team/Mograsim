package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.gates.AndGate;
import era.mi.logic.wires.Wire.WireEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIAndGate extends AndGate implements BasicGUIComponent
{
	private static final String LABEL = "&";

	private final int inputCount;
	private final double height;
	private final List<WireEnd> connectedWireEnds;
	private final List<Point> wireEndConnectionPoints;

	public GUIAndGate(int processTime, WireEnd out, WireEnd... in)
	{
		super(processTime, out, in);

		List<WireEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> wireEndConnectionPointsModifiable = new ArrayList<>();

		this.inputCount = in.length;
		this.height = inputCount * 10;

		{
			connectedWireEndsModifiable.addAll(Arrays.asList(in));
			double inputHeight = 5;
			for (int i = 0; i < inputCount; i++, inputHeight += 10)
				wireEndConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		connectedWireEndsModifiable.add(out);
		wireEndConnectionPointsModifiable.add(new Point(20, height / 2));

		this.connectedWireEnds = Collections.unmodifiableList(connectedWireEndsModifiable);
		this.wireEndConnectionPoints = Collections.unmodifiableList(wireEndConnectionPointsModifiable);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 20, height);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawRectangle(0, 0, 20, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 5, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(LABEL);
		gc.drawText(LABEL, 10 - textExtent.x / 2, (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
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
		return wireEndConnectionPoints.get(connectionI);
	}
}