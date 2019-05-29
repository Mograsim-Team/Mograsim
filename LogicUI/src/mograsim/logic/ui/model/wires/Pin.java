package mograsim.logic.ui.model.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import mograsim.logic.ui.model.components.GUIComponent;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class Pin
{
	public final GUIComponent component;
	public final int logicWidth;

	protected double relX;
	protected double relY;

	private final List<Consumer<? super Pin>> pinMovedListeners;

	public Pin(GUIComponent component, int logicWidth, double relX, double relY)
	{
		this.component = component;
		this.logicWidth = logicWidth;
		this.relX = relX;
		this.relY = relY;

		this.pinMovedListeners = new ArrayList<>();

		component.addComponentMovedListener(c -> callPinMovedListeners());
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

	// @formatter:off
	public void addPinMovedListener   (Consumer<? super Pin> listener){pinMovedListeners.add   (listener);}

	public void removePinMovedListener(Consumer<? super Pin> listener){pinMovedListeners.remove(listener);}

	private void callPinMovedListeners() {pinMovedListeners.forEach(l -> l.accept(this));}
	// @formatter:on

	protected void setRelPos(double relX, double relY)
	{
		this.relX = relX;
		this.relY = relY;
		callPinMovedListeners();
	}
}