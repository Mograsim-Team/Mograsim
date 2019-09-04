package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonSyntaxException;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.Clock;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.OrientationCalculator;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.ClockAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.preferences.Preferences;

public class GUIClock extends GUIComponent
{
	private static final double width = 20;
	private static final double height = 20;
	private static final double fontHeight = 5;

	private final Pin outputPin;

	private final LogicObserver logicObs;
	private GUIClockParams params;
	private OrientationCalculator oc;
	private Clock clock;

	public GUIClock(ViewModelModifiable model, GUIClockParams params)
	{
		this(model, params, null);
	}

	public GUIClock(ViewModelModifiable model, GUIClockParams params, String name)
	{
		super(model, name);
		this.params = params;
		logicObs = (i) -> model.requestRedraw();

		oc = new OrientationCalculator(params.orientation, width, height);
		setSize(oc.width(), oc.height());

		this.outputPin = new Pin(this, "", 1, PinUsage.OUTPUT, oc.newX(width, height / 2), oc.newY(width, height / 2));
		addPin(outputPin);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = clock == null ? "null" : (clock.isOn() ? "|" : "\u2015");
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (oc.width() - textExtent.x) / 2, getPosY() + (oc.height() - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	public void setLogicModelBinding(Clock clock)
	{
		if (this.clock != null)
			this.clock.deregisterObserver(logicObs);
		this.clock = clock;
		if (clock != null)
			clock.registerObserver(logicObs);
	}

	public boolean hasLogicModelBinding()
	{
		return clock != null;
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		switch (stateID)
		{
		case "out":
			if (clock != null)
				return clock.getOut().getInputValues();
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
			throw new UnsupportedOperationException("cannot set state of clock");
		default:
			super.setHighLevelState(stateID, newState);
		}
	}

	public Clock getClock()
	{
		return clock;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}

	public int getDelta()
	{
		return params.delta;
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "GUIClock";
	}

	@Override
	public GUIClockParams getParamsForSerializing(IdentifyParams idParams)
	{
		return params;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new ClockAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUIClock.class.getName(), (m, p, n) ->
		{
			GUIClockParams params = JsonHandler.fromJsonTree(p, GUIClockParams.class);
			if (params == null)
				throw new JsonSyntaxException("Invalid!!!");
			return new GUIClock(m, params, n);
		});
	}

	public static class GUIClockParams
	{
		int delta;
		Orientation orientation;

		public GUIClockParams(int delta, Orientation orientation)
		{
			this.delta = delta;
			this.orientation = orientation;
		}
	}
}
