package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.ManualSwitchAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.Preferences;

public class GUIManualSwitch extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 15;
	private static final double fontHeight = 5;
	private static final double heightMiniButtons = 4; // 0 is disabled

	public final int logicWidth;
	private final Pin outputPin;

	private final LogicObserver logicObs;
	private ManualSwitch logicSwitch;

	public GUIManualSwitch(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUIManualSwitch(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, name);
		this.logicWidth = logicWidth;
		logicObs = (i) -> model.requestRedraw();

		setSize(width, height);
		addPin(this.outputPin = new Pin(this, "", logicWidth, PinUsage.OUTPUT, width, height / 2));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = BitVectorFormatter.formatAsString(logicSwitch == null ? null : logicSwitch.getValues());
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (width - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);

		if (logicSwitch != null && logicWidth > 1 && heightMiniButtons > 0 && visibleRegion.y < getPosY() + heightMiniButtons)
		{
			double x = getPosX();
			double y = getPosY();
			gc.drawLine(x, y + heightMiniButtons, x + width, y + heightMiniButtons);
			Color c = gc.getBackground();
			gc.setBackground(gc.getForeground());
			BitVector bv = logicSwitch.getValues();
			double part = width / bv.length();
			for (int i = 0; i < bv.length(); i++)
			{
				double start = x + part * i;
				if (i != 0)
					gc.drawLine(start, y, start, y + heightMiniButtons);
				if (bv.getMSBit(i) == Bit.ONE)
				{
//					gc.fillRectangle(start, y, part, heightMiniButtons); // alternative, but not always visible what Bit is where 
					gc.drawLine(start, y, start + part, y + heightMiniButtons);
					gc.drawLine(start + part, y, start, y + heightMiniButtons);
				}
			}
			gc.setBackground(c);
		}
	}

	public void setLogicModelBinding(ManualSwitch logicSwitch)
	{
		if (this.logicSwitch != null)
			this.logicSwitch.deregisterObserver(logicObs);
		this.logicSwitch = logicSwitch;
		if (logicSwitch != null)
			logicSwitch.registerObserver(logicObs);
	}

	public boolean hasLogicModelBinding()
	{
		return logicSwitch != null;
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		switch (stateID)
		{
		case "out":
			if (logicSwitch != null)
				return logicSwitch.getValues();
			return null;
		default:
			return super.getHighLevelState(stateID);
		}
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		switch (stateID)
		{
		case "out":
			if (logicSwitch != null)
				logicSwitch.setState((BitVector) newState);
			break;
		default:
			super.setHighLevelState(stateID, newState);
			break;
		}
	}

	@Override
	public boolean clicked(double x, double y)
	{
		if (logicSwitch != null)
		{
			if (heightMiniButtons > 0 && y - getPosY() < heightMiniButtons)
			{
				int part = (int) ((x - getPosX()) * logicWidth / width);
				logicSwitch.setState(logicSwitch.getValues().withBitChanged(part, Bit::not));
			} else
			{
				logicSwitch.toggle();
			}
		}
		return true;
	}

	public ManualSwitch getManualSwitch()
	{
		return logicSwitch;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}

	@Override
	public JsonElement getParamsForSerializing(IdentifierGetter idGetter)
	{
		return new JsonPrimitive(logicWidth);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new ManualSwitchAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUIManualSwitch.class.getName(),
				(m, p, n) -> new GUIManualSwitch(m, p.getAsInt(), n));
	}
}