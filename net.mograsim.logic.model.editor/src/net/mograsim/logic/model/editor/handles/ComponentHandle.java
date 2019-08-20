package net.mograsim.logic.model.editor.handles;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.Editor.ComponentInfo;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.serializing.IdentifierGetter;

public class ComponentHandle extends Handle
{
	public final GUIComponent parent;
	private final static double POS_OFFSET = 2.0d;
	private final static double LENGTH_OFFSET = POS_OFFSET * 2;
	boolean selected = false;

	public ComponentHandle(GUIComponent parent)
	{
		super(4);
		this.parent = parent;
		Rectangle bounds = parent.getBounds();
		setSize(bounds.width, bounds.height);
		parent.addComponentResizedListener((c) ->
		{
			Rectangle pBounds = c.getBounds();
			setSize(pBounds.width, pBounds.height);
		});
		moveTo(parent.getPosX(), parent.getPosY());
	}

	@Override
	protected void moveTo(double x, double y)
	{
		super.moveTo(x, y);
		parent.moveTo(x, y);
	}

	@Override
	protected void render(GeneralGC gc)
	{
		if (selected)
		{
			gc.setLineWidth(2);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			Rectangle bounds = getBounds();
			bounds.x -= POS_OFFSET;
			bounds.y -= POS_OFFSET;
			bounds.width += LENGTH_OFFSET;
			bounds.height += LENGTH_OFFSET;
			gc.drawRectangle(bounds);
		}
	}

	@Override
	public void reqMove(double x, double y)
	{
		moveTo(x, y);
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
	public Optional<ComponentInfo> reqCopy(Point refPoint)
	{
		return Optional.of(new ComponentInfo(parent.getPosX() - refPoint.x, parent.getPosY() - refPoint.y, Editor.getIdentifier(parent),
				parent.getParamsForSerializing(new IdentifierGetter())));
	}

	@Override
	public HandleType getType()
	{
		return HandleType.COMPONENT;
	}
}