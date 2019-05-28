package era.mi.logic.types;

/**
 * A way to define a color with the possibility to use colors built into the system (called "system colors" in SWT).
 * <p>
 * A {@link ColorDefinition} is defined either by a {@link BuiltInColor} constant, in which case <code>r==g==b==-1</code>, or by red / green
 * / blue components, in which case <code>builtInColor==null</code>
 */
public class ColorDefinition
{
	/**
	 * The built-in color constant defining this color.
	 */
	public final ColorDefinition.BuiltInColor builtInColor;
	/**
	 * The red color component defining this color.
	 */
	public final int r;
	/**
	 * The green color component defining this color.
	 */
	public final int g;
	/**
	 * The blue color component defining this color.
	 */
	public final int b;

	public ColorDefinition(ColorDefinition.BuiltInColor col)
	{
		if (col == null)
			throw new IllegalArgumentException("Illegal built-in color: " + col);
		this.builtInColor = col;
		this.r = -1;
		this.g = -1;
		this.b = -1;
	}

	public ColorDefinition(int r, int g, int b)
	{
		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
			throw new IllegalArgumentException("Illegal color components: r=" + r + "; g=" + g + "; b=" + b);
		this.builtInColor = null;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static enum BuiltInColor
	{
		COLOR_WHITE, COLOR_BLACK, COLOR_RED, COLOR_DARK_RED, COLOR_GREEN, COLOR_DARK_GREEN, COLOR_YELLOW, COLOR_DARK_YELLOW, COLOR_BLUE,
		COLOR_DARK_BLUE, COLOR_MAGENTA, COLOR_DARK_MAGENTA, COLOR_CYAN, COLOR_DARK_CYAN, COLOR_GRAY, COLOR_DARK_GRAY;
	}

}