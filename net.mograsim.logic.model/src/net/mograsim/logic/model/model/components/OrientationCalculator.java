package net.mograsim.logic.model.model.components;

/**
 * This class simplifies the calculation of coordinates, especially for model components.
 * <p>
 * Supply it with the original width and height and an orientation, and use the methods {@link #newX(double, double)} and
 * {@link #newY(double, double)} to retrieve the new coordinates, relative to the upper left corner of {@link Orientation#RIGHT}. The
 * {@link #height()} and {@link #width()} methods return the width and height in the new orientation.
 * <p>
 * This is meant to be used in the context of a a classic display coordinate system, as done in the {@link ModelComponent}s.
 *
 * @see Orientation
 * @author Christian Femers
 */
public class OrientationCalculator
{
	final Orientation o;
	final double oldWHalf;
	final double oldHHalf;
	final double w;
	final double h;
	final double wHalf;
	final double hHalf;

	public OrientationCalculator(Orientation o, double width, double height)
	{
		this.o = o;
		this.oldWHalf = width / 2;
		this.oldHHalf = height / 2;

		if (o.swapsWidthAndHeight())
		{
			w = height;
			h = width;
			wHalf = oldHHalf;
			hHalf = oldWHalf;
		} else
		{
			w = width;
			h = height;
			wHalf = oldWHalf;
			hHalf = oldHHalf;
		}
	}

	/**
	 * Returns the new height (that equals the old width if {@link Orientation#swapsWidthAndHeight()} is true)
	 */
	public double height()
	{
		return h;
	}

	/**
	 * Returns the new width (that equals the old height if {@link Orientation#swapsWidthAndHeight()} is true)
	 */
	public double width()
	{
		return w;
	}

	public double newX(double x, double y)
	{
		return (x - oldWHalf) * o.trans11 + (y - oldHHalf) * o.trans12 + wHalf;
	}

	public double newY(double x, double y)
	{
		return (x - oldWHalf) * o.trans21 + (y - oldHHalf) * o.trans22 + hHalf;
	}

	public final Orientation getOrientation()
	{
		return o;
	}
}
