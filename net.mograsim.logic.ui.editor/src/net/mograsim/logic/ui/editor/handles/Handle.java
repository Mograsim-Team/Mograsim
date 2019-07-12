package net.mograsim.logic.ui.editor.handles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.editor.Editor.ComponentInfo;
import net.mograsim.logic.ui.editor.states.EditorState;

public abstract class Handle
{
	private final Rectangle bounds;
	private final Collection<Runnable> redrawListeners, destroyListeners;
	
	public Handle()
	{
		redrawListeners = new ArrayList<>();
		destroyListeners = new ArrayList<>();
		bounds = new Rectangle(0, 0, 0, 0);
		callRedrawListeners();
	}

	final public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		if (bounds.intersects(visibleRegion))
			render(gc);
	}

	protected abstract void render(GeneralGC gc);

	protected void setSize(double width, double height)
	{
		bounds.width = width;
		bounds.height = height;
		callRedrawListeners();
	}

	protected void moveTo(double x, double y)
	{
		bounds.x = x;
		bounds.y = y;
		callRedrawListeners();
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void addRedrawListener(Runnable listener)
	{
		redrawListeners.add(listener);
	}

	public void removeRedrawListener(Runnable listener)
	{
		redrawListeners.remove(listener);
	}

	protected void callRedrawListeners()
	{
		redrawListeners.forEach(l -> l.run());
	}

	public double getPosX()
	{
		return bounds.x;
	}

	public double getPosY()
	{
		return bounds.y;
	}

	void destroy()
	{
		destroyListeners.forEach(l -> l.run());
	}

	public void addDestroyListener(Runnable listener)
	{
		redrawListeners.add(listener);
	}

	public void removeDestroyListener(Runnable listener)
	{
		redrawListeners.remove(listener);
	}
	
	public boolean contains(double x, double y)
	{
		return bounds.contains(x, y);
	}
	
	public boolean contains(Point p)
	{
		return contains(p.x, p.y);
	}
	
	/**
	 * Register a mouse click
	 * @param x Coordinate of the click in the world, not the display context
	 * @param y Coordinate of the click in the world, not the display context
	 * @return true if the click was consumed, false otherwise
	 */
	public boolean click(double x, double y, int stateMask, EditorState state)
	{
		if(contains(x, y))
			return state.clickedHandle(new HandleClickInfo(this, stateMask));
		return false;
	}

	//@formatter:off
    public void reqMove(double x, double y) {}
    public void reqDelete() {}
    public Optional<ComponentInfo> reqCopy(Point refPoint) { return Optional.empty(); }
    public void onSelect() {}
    public void onDeselect() {}
    //@formatter:on
    
    public abstract HandleType getType();
    
    public static enum HandleType
    {
    	COMPONENT, STATIC_PIN, INTERFACE_PIN, WIRE_POINT, WIRE, CORNER;
    }
    
    public static class HandleClickInfo
    {
    	public final int stateMask;
    	public final Handle clicked;
    	
    	HandleClickInfo(Handle clicked, int stateMask)
    	{
    		this.clicked = clicked;
    		this.stateMask = stateMask;
    	}
    }
}
