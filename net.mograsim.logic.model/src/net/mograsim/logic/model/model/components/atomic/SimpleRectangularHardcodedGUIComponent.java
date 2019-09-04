package net.mograsim.logic.model.model.components.atomic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleRectangularHardcodedGUIComponentAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer.CenteredTextParams;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public abstract class SimpleRectangularHardcodedGUIComponent extends GUIComponent
{
	private static final double centerTextHeight = 5;
	private static final double pinNamesHeight = 3.5;
	private static final double pinNamesMargin = .5;

	private final String id;

	private final DefaultOutlineRenderer outlineRenderer;
	private final CenteredTextSymbolRenderer centerTextRenderer;
	private final PinNamesSymbolRenderer pinNamesRenderer;

	private AtomicReference<Object> state;
	private Runnable recalculate;

	// creation and destruction

	public SimpleRectangularHardcodedGUIComponent(ViewModelModifiable model, String id, String name, String centerText)
	{
		super(model, name);
		this.id = id;
		this.outlineRenderer = new DefaultOutlineRenderer(this);
		CenteredTextParams centeredTextParams = new CenteredTextParams();
		centeredTextParams.text = centerText;
		centeredTextParams.fontHeight = centerTextHeight;
		this.centerTextRenderer = new CenteredTextSymbolRenderer(this, centeredTextParams);
		PinNamesParams pinNamesParams = new PinNamesParams();
		pinNamesParams.pinLabelHeight = pinNamesHeight;
		pinNamesParams.pinLabelMargin = pinNamesMargin;
		this.pinNamesRenderer = new PinNamesSymbolRenderer(this, pinNamesParams);
		addPinRemovedListener(this::pinRemoved);
		setHighLevelStateHandler(new HighLevelStateHandler()
		{
			@Override
			public String getIDForSerializing(IdentifyParams idParams)
			{
				return null;// we don't need to serialize this; it's implicit since we are a SimpleRectangularHardcodedGUIComponent
			}

			@Override
			public Object getParamsForSerializing(IdentifyParams idParams)
			{
				return null;
			}

			@Override
			public Object getHighLevelState(String stateID)
			{
				return SimpleRectangularHardcodedGUIComponent.this.getHighLevelState(state.get(), stateID);
			}

			@Override
			public void setHighLevelState(String stateID, Object newState)
			{
				state.updateAndGet(s -> SimpleRectangularHardcodedGUIComponent.this.setHighLevelState(s, stateID, newState));
				recalculate.run();
			}
		});
	}

	// pins

	protected void addPin(Pin pin, Position namePosition)
	{
		super.addPin(pin); // do this first to catch errors
		pinNamesRenderer.setPinPosition(pin, namePosition);
	}

	private void pinRemoved(Pin pin)
	{
		pinNamesRenderer.setPinPosition(pin, null);
	}

	// high-level access

	@SuppressWarnings({ "static-method", "unused" }) // this method is intended to be overridden
	protected Object getHighLevelState(Object state, String stateID)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@SuppressWarnings({ "static-method", "unused" }) // this method is intended to be overridden
	protected Object setHighLevelState(Object lastState, String stateID, Object newHighLevelState)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	// logic

	public abstract Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds);

	// logic model binding

	public void setLogicModelBindingAndResetState(AtomicReference<Object> state, Runnable recalculate)
	{
		this.state = state;
		this.recalculate = recalculate;
	}

	// "graphical" operations

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		outlineRenderer.render(gc, visibleRegion);
		centerTextRenderer.render(gc, visibleRegion);
		pinNamesRenderer.render(gc, visibleRegion);
	}

	// serializing

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return id;
	}

	// operations no longer supported

	@Override
	protected void addPin(Pin pin)
	{
		throw new UnsupportedOperationException("Can't add pins without setting usage, call addPin(Pin [, Position]) instead");
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleRectangularHardcodedGUIComponentAdapter());
	}
}