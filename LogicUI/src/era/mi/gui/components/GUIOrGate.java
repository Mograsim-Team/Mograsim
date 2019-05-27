package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.components.gates.OrGate;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIOrGate extends OrGate implements BasicGUIComponent
{
	private static final String LABEL = "\u22651";// >=1

	private final int inputCount;
	private final double height;
	private final List<ReadEnd> connectedWireEnds;
	private final List<Point> WireEndConnectionPoints;

	public GUIOrGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, out, in);

		List<ReadEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> WireEndConnectionPointsModifiable = new ArrayList<>();

		this.inputCount = in.length;
		this.height = inputCount * 10;

		{
			connectedWireEndsModifiable.addAll(Arrays.asList(in));
			double inputHeight = 5;
			for (int i = 0; i < inputCount; i++, inputHeight += 10)
				WireEndConnectionPointsModifiable.add(new Point(0, inputHeight));
		}

		connectedWireEndsModifiable.add(out);
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