package era.mi.components.gui;

import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public interface BasicGUIComponent
{
	/**
	 * Render this component to the given gc, at coordinates (0, 0).
	 */
	public void render(GeneralGC gc);

	//TODO this code will be replaced by code in BasicComponent.
	/**
	 * Returns how many wire arrays are connected to this component.
	 * (Connections are static - they can't be removed and no new ones can be added)
	 */
	public int getConnectedWireArraysCount();
	/**
	 * Returns the n-th wire array connected to this component.
	 */
	public WireArray getConnectedWireArray(int connectionIndex);
	/**
	 * Returns relative coordinates where the n-th wire array is connected to this component.
	 */
	public Point getWireArrayConnectionPoint(int connectionIndex);
}