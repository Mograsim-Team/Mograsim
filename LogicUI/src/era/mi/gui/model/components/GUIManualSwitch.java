package era.mi.gui.model.components;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.wires.Pin;
import era.mi.logic.components.ManualSwitch;
import era.mi.logic.types.BitVectorFormatter;
import era.mi.logic.wires.Wire.ReadEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public class GUIManualSwitch extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 15;
	private static final double fontHeight = 5;

	private final Pin outputPin;

	private ManualSwitch logicSwitch;
	private ReadEnd end;

	public GUIManualSwitch(ViewModel model)
	{
		super(model);
		setSize(width, height);
		addPin(this.outputPin = new Pin(this, 1, width, height / 2));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		gc.drawRectangle(posX, posY, width, height);
		String label = BitVectorFormatter.formatValueAsString(end);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (width - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	public void setLogicModelBinding(ManualSwitch logicSwitch, ReadEnd end)
	{
		this.logicSwitch = logicSwitch;
		this.end = end;
		// TODO when ManualSwitch supports it, add listeners
		end.addObserver((i, o) -> callComponentLookChangedListeners());
	}

	@Override
	public boolean clicked(double x, double y)
	{
		if (logicSwitch != null)
			logicSwitch.toggle();
		return true;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}
}