package era.mi.gui.model.wires;

import era.mi.gui.model.components.GUIComponent;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class Pin
{
	public final GUIComponent component;
	protected double relX;
	protected double relY;

	public Pin(GUIComponent component, double relX, double relY)
	{
		this.component = component;
		this.relX = relX;
		this.relY = relY;
	}

	public double getRelX()
	{
		return relX;
	}

	public double getRelY()
	{
		return relY;
	}

	public Point getRelPos()
	{
		return new Point(relX, relY);
	}

	public Point getPos()
	{
		Rectangle componentBounds = component.getBounds();
		return new Point(relX + componentBounds.x, relY + componentBounds.y);
	}

	protected void setRelPos(double relX, double relY)
	{
		this.relX = relX;
		this.relY = relY;
	}
}