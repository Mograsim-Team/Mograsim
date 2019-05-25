package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import era.mi.logic.components.ManualSwitch;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIManualSwitch extends ManualSwitch implements BasicGUIComponent
{
	private static final Map<Bit, String> bitNames;
	static
	{
		Map<Bit, String> bitNamesModifiable = new HashMap<>();
		bitNamesModifiable.put(Bit.ONE, "1");
		bitNamesModifiable.put(Bit.ZERO, "0");
		bitNamesModifiable.put(Bit.Z, "Z");
		bitNamesModifiable.put(Bit.U, "U");
		bitNamesModifiable.put(Bit.X, "X");
		bitNames = Collections.unmodifiableMap(bitNamesModifiable);
	}

	private final ReadEnd we;
	private final List<ReadEnd> connectedWireEnds;
	private final List<Point> wireEndConnectionPoints;

	public GUIManualSwitch(ReadWriteEnd output)
	{
		super(output);

		this.we = output;

		List<ReadEnd> connectedWireEndsModifiable = new ArrayList<>();
		List<Point> wireEndConnectionPointsModifiable = new ArrayList<>();

		connectedWireEndsModifiable.add(output);
		wireEndConnectionPointsModifiable.add(new Point(20, 7.5));

		this.connectedWireEnds = Collections.unmodifiableList(connectedWireEndsModifiable);
		this.wireEndConnectionPoints = Collections.unmodifiableList(wireEndConnectionPointsModifiable);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 20, 15);
	}

	@Override
	public void render(GeneralGC gc)
	{
		gc.drawRectangle(0, 0, 20, 15);
		String label = bitNames.get(we.getValue());
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 6, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, 10 - textExtent.x / 2, 7.5 - textExtent.y / 2, true);
		gc.setFont(oldFont);
	}

	@Override
	public boolean clicked(double x, double y)
	{
		toggle();
		return true;
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
		return wireEndConnectionPoints.get(connectionI);
	}
}