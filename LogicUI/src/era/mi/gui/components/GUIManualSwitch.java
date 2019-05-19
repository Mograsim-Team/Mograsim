package era.mi.gui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import era.mi.logic.Bit;
import era.mi.logic.components.ManualSwitch;
import era.mi.logic.wires.WireArray;
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

	private final WireArray wa;
	private final List<WireArray> connectedWireArrays;
	private final List<Point> wireArrayConnectionPoints;

	public GUIManualSwitch(WireArray output)
	{
		super(output);

		this.wa = output;

		List<WireArray> connectedWireArraysModifiable = new ArrayList<>();
		List<Point> wireArrayConnectionPointsModifiable = new ArrayList<>();

		connectedWireArraysModifiable.add(output);
		wireArrayConnectionPointsModifiable.add(new Point(20, 7.5));

		this.connectedWireArrays = Collections.unmodifiableList(connectedWireArraysModifiable);
		this.wireArrayConnectionPoints = Collections.unmodifiableList(wireArrayConnectionPointsModifiable);
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
		String label = bitNames.get(wa.getValue());
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