package net.mograsim.logic.ui.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.editor.handles.Handle;

public class Selection implements Iterable<Handle>
{
	private Set<Handle> selection = new HashSet<>();

	public Map<Handle, Point> calculateOffsets()
	{
		Map<Handle, Point> offsets = new HashMap<>();
		Point ref = getTopLeft();
		selection.forEach(h -> offsets.put(h, new Point(h.getPosX() - ref.x, h.getPosY() - ref.y)));
		return offsets;
	}

	public Rectangle getBounds()
	{
		Point pos1 = getTopLeft();
		Point pos2 = getBottomRight();
		return new Rectangle(pos1.x, pos1.y, pos2.x - pos1.x, pos2.y - pos1.y);
	}

	public double getWidth()
	{// TODO: Compute this more efficiently
		return getTopRight().x - getTopLeft().x;
	}

	public double getHeight()
	{
		return getBottomLeft().y - getTopLeft().y;
	}

	public Point getTopLeft()
	{
		return getCorner(Double.MAX_VALUE, Double::min, r -> 0, Double.MAX_VALUE, Double::min, r -> 0);
	}

	public Point getTopRight()
	{
		return getCorner(-Double.MAX_VALUE, Double::max, r -> r.width, Double.MAX_VALUE, Double::min, r -> 0);
	}

	public Point getBottomLeft()
	{
		return getCorner(Double.MAX_VALUE, Double::min, r -> 0, -Double.MAX_VALUE, Double::max, r -> r.height);
	}

	public Point getBottomRight()
	{
		return getCorner(-Double.MAX_VALUE, Double::max, r -> r.width, -Double.MAX_VALUE, Double::max, r -> r.height);
	}

	public Point getCorner(double xIdentity, DoubleBinaryOperator xOp, Offset xOffset, double yIdentity,
			DoubleBinaryOperator yOp, Offset yOffset)
	{
		double x = xIdentity, y = yIdentity;
		for (Handle c : selection)
		{
			Rectangle bounds = c.getBounds();
			x = xOp.applyAsDouble(x, bounds.x + xOffset.computeOffset(bounds));
			y = yOp.applyAsDouble(y, bounds.y + yOffset.computeOffset(bounds));
		}
		return new Point(x, y);
	}

	private static interface Offset
	{
		public double computeOffset(Rectangle bounds);
	}

	public void add(Handle h)
	{
		selection.add(h);
		h.onSelect();
	}

	public void remove(Handle h)
	{
		selection.remove(h);
		h.onDeselect();
	}

	public void clear()
	{
		selection.forEach(h -> h.onDeselect());
		selection.clear();
	}

	public int size()
	{
		return selection.size();
	}

	public boolean contains(Handle h)
	{
		return selection.contains(h);
	}

	public void addAll(Collection<Handle> handles)
	{
		handles.forEach(h -> h.onSelect());
		selection.addAll(handles);
	}

	@Override
	public Iterator<Handle> iterator()
	{
		return selection.iterator();
	}

	@Override
	public String toString()
	{
		return selection.toString();
	}

	public boolean isEmpty()
	{
		return selection.isEmpty();
	}
}
