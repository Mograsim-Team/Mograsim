package era.mi.gui.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.wires.Pin;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public abstract class GUIComponent
{
	protected final ViewModel model;
	private final Rectangle bounds;
	private final List<Pin> pins;
	protected final List<Pin> pinsUnmodifiable;

	public GUIComponent(ViewModel model)
	{
		this.model = model;
		this.bounds = new Rectangle(0, 0, 0, 0);
		this.pins = new ArrayList<>();
		this.pinsUnmodifiable = Collections.unmodifiableList(pins);
	}

	public void moveTo(double x, double y)
	{
		bounds.x = x;
		bounds.y = y;
	}

	/**
	 * Returns the bounds of this component. Used for calculating which component is clicked.
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	// TODO
	/**
	 * Called when this component is clicked. Relative coordinates of the click are given. Returns true if this component has to be redrawn.
	 */
	public boolean clicked(double x, double y)
	{
		return false;
	}

	/**
	 * Returns a list of pins of this component.
	 */
	public List<Pin> getPins()
	{
		return pinsUnmodifiable;
	}

	/**
	 * Render this component to the given gc.
	 */
	public abstract void render(GeneralGC gc, Rectangle visibleRegion);

	protected void setSize(double width, double height)
	{
		bounds.width = width;
		bounds.height = height;
	}

	protected void addPin(Pin pin)
	{// TODO notify pins they are "created"
		pins.add(pin);
	}

	protected void removePin(Pin pin)
	{// TODO notify pins they are "destroyed"
		pins.remove(pin);
	}
}