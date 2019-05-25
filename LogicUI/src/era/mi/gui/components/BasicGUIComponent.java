package era.mi.gui.components;

import era.mi.logic.wires.Wire.ReadEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public interface BasicGUIComponent
{
	/**
	 * Render this component to the given gc, at coordinates (0, 0).
	 */
	public void render(GeneralGC gc);

	/**
	 * Returns the bounds of this component. Used for calculating which component is clicked.
	 */
	public Rectangle getBounds();

	/**
	 * Called when this component is clicked. Relative coordinates of the click are given. Returns true if this component has to be redrawn.
	 */
	public default boolean clicked(double x, double y)
	{
		return false;
	}

	// TODO this code will be replaced by code in BasicComponent.
	/**
	 * Returns how many wire arrays are connected to this component. (Connections are static - they can't be removed and no new ones can be
	 * added)
	 */
	public int getConnectedWireEndsCount();

	/**
	 * Returns the n-th wire array connected to this component.
	 */
	public ReadEnd getConnectedWireEnd(int connectionIndex);

	/**
	 * Returns relative coordinates where the n-th wire array is connected to this component.
	 */
	public Point getWireEndConnectionPoint(int connectionIndex);
}