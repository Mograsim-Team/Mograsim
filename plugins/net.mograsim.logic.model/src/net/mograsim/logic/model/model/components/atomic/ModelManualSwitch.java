package net.mograsim.logic.model.model.components.atomic;

import static net.mograsim.logic.model.preferences.RenderPreferences.FOREGROUND_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.TEXT_COLOR;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.BitVectorFormatter;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.ManualSwitchAdapter;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.util.TextRenderingHelper;

public class ModelManualSwitch extends ModelComponent
{
	private static final double width = 20;
	private static final double height = 10;
	private static final double fontHeight = 5;
	private static final double heightMiniButtons = 4; // 0 is disabled
	private static final double textMargin = 0.5;

	public final int logicWidth;
	private final Pin outputPin;

	private final LogicObserver logicObs;
	private CoreManualSwitch manualSwitch;

	private final List<Consumer<Object>> hlsListeners;

	public ModelManualSwitch(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelManualSwitch(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, name, false);
		this.logicWidth = logicWidth;

		setSize(width, height);
		addPin(this.outputPin = new Pin(model, this, "", logicWidth, PinUsage.OUTPUT, width, height / 2));

		hlsListeners = new ArrayList<>();

		logicObs = i ->
		{
			model.requestRedraw();
			BitVector v = getOutValues();
			hlsListeners.forEach(l -> l.accept(v));
		};

		setHighLevelStateHandler(new HighLevelStateHandler()
		{
			@Override
			public Object get(String stateID)
			{
				switch (stateID)
				{
				case "out":
					if (manualSwitch != null)
						return getOutValues();
					return null;
				default:
					throw new IllegalArgumentException("No high level state with ID " + stateID);
				}
			}

			@Override
			public void set(String stateID, Object newState)
			{
				switch (stateID)
				{
				case "out":
					if (manualSwitch != null)
						manualSwitch.setState((BitVector) newState);
					break;
				default:
					throw new IllegalArgumentException("No high level state with ID " + stateID);
				}
			}

			@Override
			public void addListener(String stateID, Consumer<Object> stateChanged)
			{
				switch (stateID)
				{
				case "out":
					hlsListeners.add(stateChanged);
					break;
				default:
					throw new IllegalArgumentException("No high level state with ID " + stateID);
				}
			}

			@Override
			public void removeListener(String stateID, java.util.function.Consumer<Object> stateChanged)
			{
				switch (stateID)
				{
				case "out":
					hlsListeners.remove(stateChanged);
					break;
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
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		Color foreground = renderPrefs.getColor(FOREGROUND_COLOR);
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
		String label = BitVectorFormatter.formatAsString(manualSwitch == null ? null : getOutValues(), false);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Color textColor = renderPrefs.getColor(TEXT_COLOR);
		if (textColor != null)
			gc.setForeground(textColor);
		TextRenderingHelper.drawTextFitting(gc, label, getBounds(), textMargin, true);
		gc.setFont(oldFont);

		if (manualSwitch != null && logicWidth > 1 && heightMiniButtons > 0 && visibleRegion.y < getPosY() + heightMiniButtons)
		{
			double x = getPosX();
			double y = getPosY();
			gc.drawLine(x, y + heightMiniButtons, x + width, y + heightMiniButtons);
			Color c = gc.getBackground();
			gc.setBackground(gc.getForeground());
			BitVector bv = getOutValues();
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

	public void setCoreModelBinding(CoreManualSwitch logicSwitch)
	{
		if (this.manualSwitch != null)
			this.manualSwitch.deregisterObserver(logicObs);
		this.manualSwitch = logicSwitch;
		if (logicSwitch != null)
			logicSwitch.registerObserver(logicObs);
	}

	public boolean hasCoreModelBinding()
	{
		return manualSwitch != null;
	}

	@Override
	public boolean clicked(double x, double y)
	{
		if (manualSwitch != null)
		{
			if (heightMiniButtons > 0 && y - getPosY() < heightMiniButtons)
			{
				int part = (int) ((x - getPosX()) * logicWidth / width);
				manualSwitch.setState(getOutValues().withBitChanged(part, Bit::not));
			} else
			{
				manualSwitch.toggle();
			}
		}
		return true;
	}

	public CoreManualSwitch getManualSwitch()
	{
		return manualSwitch;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "ManualSwitch";
	}

	@Override
	public Integer getParamsForSerializing(IdentifyParams idParams)
	{
		return logicWidth;
	}

	private BitVector getOutValues()
	{
		return manualSwitch.getValues();
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new ManualSwitchAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelManualSwitch.class.getName(),
				(m, p, n) -> new ModelManualSwitch(m, p.getAsInt(), n));
	}
}