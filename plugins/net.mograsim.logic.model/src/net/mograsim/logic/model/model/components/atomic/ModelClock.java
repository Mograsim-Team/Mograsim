package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonSyntaxException;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.OrientationCalculator;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.ClockAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.preferences.Preferences;

public class ModelClock extends ModelComponent
{
	private static final double width = 20;
	private static final double height = 20;
	private static final double fontHeight = 5;

	private final Pin outputPin;

	private final LogicObserver logicObs;
	private ModelClockParams params;
	private OrientationCalculator oc;
	private CoreClock clock;

	public ModelClock(LogicModelModifiable model, ModelClockParams params)
	{
		this(model, params, null);
	}

	public ModelClock(LogicModelModifiable model, ModelClockParams params, String name)
	{
		super(model, name, false);
		this.params = params;
		logicObs = (i) -> model.requestRedraw();

		oc = new OrientationCalculator(params.orientation, width, height);
		setSize(oc.width(), oc.height());

		this.outputPin = new Pin(model, this, "", 1, PinUsage.OUTPUT, oc.newX(width, height / 2), oc.newY(width, height / 2));
		addPin(outputPin);

		setHighLevelStateHandler(new HighLevelStateHandler()
		{
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
					throw new IllegalArgumentException("No high level state with ID " + stateID);
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
					throw new IllegalArgumentException("No high level state with ID " + stateID);
				}
			}

			@Override
			public String getIDForSerializing(IdentifyParams idParams)
			{
				return null;
			}

			@Override
			public Object getParamsForSerializing(IdentifyParams idParams)
			{
				return null;
			}
		});

		init();
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

	public void setCoreModelBinding(CoreClock clock)
	{
		if (this.clock != null)
			this.clock.deregisterObserver(logicObs);
		this.clock = clock;
		if (clock != null)
			clock.registerObserver(logicObs);
	}

	public boolean hasCoreModelBinding()
	{
		return clock != null;
	}

	public CoreClock getClock()
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
		return "Clock";
	}

	@Override
	public ModelClockParams getParamsForSerializing(IdentifyParams idParams)
	{
		return params;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new ClockAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelClock.class.getName(), (m, p, n) ->
		{
			ModelClockParams params = JsonHandler.fromJsonTree(p, ModelClockParams.class);
			if (params == null)
				throw new JsonSyntaxException("Invalid!!!");
			return new ModelClock(m, params, n);
		});
	}

	public static class ModelClockParams
	{
		int delta;
		Orientation orientation;

		public ModelClockParams(int delta, Orientation orientation)
		{
			this.delta = delta;
			this.orientation = orientation;
		}
	}
}
