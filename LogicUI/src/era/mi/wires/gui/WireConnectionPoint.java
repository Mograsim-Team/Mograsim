package era.mi.wires.gui;

import org.eclipse.swt.graphics.Color;

import era.mi.components.gui.BasicGUIComponent;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class WireConnectionPoint implements BasicGUIComponent
{
	private final WireArray	wa;
	private final int		wiresCrossing;

	public WireConnectionPoint(WireArray wa, int wiresCrossing)
	{
		this.wa = wa;
		this.wiresCrossing = wiresCrossing;
	}

	@Override
	public void render(GeneralGC gc)
	{
		Color oldBG = gc.getBackground();
		Color fg = gc.getForeground();
		gc.setBackground(fg);
		gc.fillOval(-2, -2, 4, 4);
		gc.setBackground(oldBG);
	}
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(0, 0, 0, 0);
	}
	@Override
	public int getConnectedWireArraysCount()
	{
		return wiresCrossing;
	}
	@Override
	public WireArray getConnectedWireArray(int connectionIndex)
	{
		return wa;
	}
	@Override
	public Point getWireArrayConnectionPoint(int connectionIndex)
	{
		return new Point(0, 0);
	}
}