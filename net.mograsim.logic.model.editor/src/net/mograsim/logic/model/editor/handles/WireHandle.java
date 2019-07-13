package net.mograsim.logic.model.editor.handles;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.editor.states.EditorState;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.GUIWire.PathChangedListener;

public class WireHandle extends Handle implements PathChangedListener
{
	private boolean selected = false;
	private final static double WIDTH = 2.0;
	private final static double WIDTH_SQUARED = WIDTH * WIDTH;
	public final GUIWire parent;

	public WireHandle(GUIWire parent)
	{
		this.parent = parent;
		parent.addPathChangedListener(this);
		updateBounds();
	}
	
	@Override
	void destroy()
	{
		super.destroy();
		parent.removePathChangedListener(this);
	}

	public void updateBounds()
	{
		Rectangle r = parent.getBounds();
		moveTo(r.x, r.y);
		setSize(r.width, r.height);
	}
	
	@Override
	public void render(GeneralGC gc)
	{
		if(selected)
		{
			gc.setLineWidth(WIDTH);
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
			gc.drawPolyline(parent.getEffectivePath());
		}
	}
		
	@Override
	public void onSelect()
	{
		selected = true;
		callRedrawListeners();
	}

	@Override
	public void onDeselect()
	{
		selected = false;
		callRedrawListeners();
	}
	
	@Override
	public void reqDelete()
	{
		parent.destroy();
	}
	
	@Override
	public boolean contains(double x, double y)
	{
		return click(parent, x, y).isPresent();
	}
	
	@Override
	public boolean click(double x, double y, int stateMask, EditorState state)
	{
		Optional<WireClickData> op = click(parent, x, y);
		if(op.isEmpty())
			return false;
		WireClickData data = op.get();
		return state.clickedHandle(new WireHandleClickInfo(this, data.segment, data.pos, stateMask));
	}
	
	public static class WireHandleClickInfo extends HandleClickInfo
	{
		public final int segment;
		public final Point posOnWire;
		WireHandleClickInfo(WireHandle clicked, int segment, Point posOnWire, int stateMask)
		{
			super(clicked, stateMask);
			this.segment = segment;
			this.posOnWire = posOnWire;
		}
		
	}
	
	private static Optional<WireClickData> click(GUIWire w, double x, double y)
	{
		Rectangle modifiedBounds = w.getBounds();
		modifiedBounds.x -= WIDTH;
		modifiedBounds.y -= WIDTH;
		modifiedBounds.width += WIDTH * 2;
		modifiedBounds.height += WIDTH * 2;
		if (modifiedBounds.contains(x, y))
		{
			double[] effectivePath = w.getEffectivePath();
			for (int i = 3; i < effectivePath.length; i += 2)
			{
				double a1 = effectivePath[i - 3], a2 = effectivePath[i - 2], b1 = effectivePath[i - 1],
						b2 = effectivePath[i], r1 = b2 - a2, r2 = a1 - b1;

				double f = ((x - a1) * r2 + (a2 - y) * r1) / (-r2 * r2 - r1 * r1);
				if (f >= 0 && f <= 1)
				{
					double e1 = a1 + f * (b1 - a1), e2 = a2 + f * (b2 - a2);
					r1 = e1 - x;
					r2 = e2 - y;
					if (r1 * r1 + r2 * r2 <= WIDTH_SQUARED)
						return Optional.of(new WireClickData(new Point(e1, e2), (i / 2) - 1));
				}
			}
		}
		return Optional.empty();
	}

	private final static class WireClickData
	{
		WireClickData(Point pos, int segment)
		{
			this.pos = pos;
			this.segment = segment;
		}

		/**
		 * Position on the wire that is closest to the click
		 */
		public final Point pos;
		/**
		 * Segment of the wire that the {@link Point} pos is on
		 */
		public final int segment;
	}
	
	@Override
	public HandleType getType()
	{
		return HandleType.WIRE;
	}

	@Override
	public void pathChanged(GUIWire wire, int diff)
	{
		updateBounds();
	}
}
