package net.mograsim.logic.model.model.components;

/**
 * For components that can have different orientations. The meaning is not clearly defined, however it is common that the orientation
 * denotes the direction the output is facing or the general flow of signals. <code>_ALT</code> represents an alternative, which is normally
 * a mirrored version. A component can choose to not support some variants.
 * <p>
 * In terms of calculation, {@link #RIGHT} is considered the default.
 * <p>
 * Note that this needs to be interpreted using the GUI coordinate system, meaning that UP and DOWN are swapped.
 * 
 * @author Christian Femers
 */
public enum Orientation
{
	/**
	 * The orientation <code>RIGHT</code> is the default orientation, all others are defined relative to it.
	 */
	RIGHT(1, 0, 0, 1), LEFT(-1, 0, 0, -1), UP(0, 1, -1, 0), DOWN(0, -1, 1, 0), RIGHT_ALT(1, 0, 0, -1), LEFT_ALT(-1, 0, 0, 1),
	UP_ALT(0, -1, -1, 0), DOWN_ALT(0, 1, 1, 0);

	// simple 2D transformation matrix
	final double trans11;
	final double trans12;
	final double trans21;
	final double trans22;

	private Orientation(double trans11, double trans12, double trans21, double trans22)
	{
		this.trans11 = trans11;
		this.trans12 = trans12;
		this.trans21 = trans21;
		this.trans22 = trans22;
	}

	/**
	 * Performs a simple rotation around the origin. This does not work for the display coordinate system.
	 * 
	 * @return the point's new X coordinate
	 */
	public double getNewX(double rightX, double rightY)
	{
		return rightX * trans11 + rightY * trans12;
	}

	/**
	 * Performs a simple rotation around the origin. This does not work for the display coordinate system.
	 * 
	 * @return the point's new Y coordinate
	 */
	public double getNewY(double rightX, double rightY)
	{
		return rightX * trans21 + rightY * trans22;
	}

	public boolean doesMirror()
	{
		return ordinal() > 3;
	}

	public boolean swapsWidthAndHeight()
	{
		return trans11 == 0;
	}
}