package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.OrientationCalculator;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SplitterAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class ModelSplitter extends ModelComponent
{
	private static final double width = 10;
	private static final double heightPerPin = 10;

	private final double heightWithoutOC;
	public final int logicWidth;
	private final OrientationCalculator oc;
	private final Pin inputPin;

	private ReadEnd inputEnd;
	private final ReadEnd[] outputEnds;

	public ModelSplitter(LogicModelModifiable model, SplitterParams params)
	{
		this(model, params, null);
	}

	public ModelSplitter(LogicModelModifiable model, SplitterParams params, String name)
	{
		super(model, name, false);
		this.logicWidth = params.logicWidth;
		this.oc = new OrientationCalculator(toggleLeftDownAlt(params.orientation), width,
				this.heightWithoutOC = (logicWidth - 1) * heightPerPin);
		setSize(oc.width(), oc.height());
		double inLineY = (logicWidth - 1) * heightPerPin / 2;
		addPin(this.inputPin = new Pin(model, this, "I", logicWidth, PinUsage.TRISTATE, oc.newX(0, inLineY), oc.newY(0, inLineY)));
		double outputHeight = (logicWidth - 1) * heightPerPin;
		for (int i = 0; i < logicWidth; i++, outputHeight -= 10)
			addPin(new Pin(model, this, "O" + i, 1, PinUsage.TRISTATE, oc.newX(width, outputHeight), oc.newY(width, outputHeight)));
		outputEnds = new ReadEnd[logicWidth];

		init();
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getPosX();
		double posY = getPosY();

		ColorDefinition c = BitVectorFormatter.formatAsColor(inputEnd);
		if (c != null)
			gc.setForeground(ColorManager.current().toColor(c));
		gc.setLineWidth(
				Preferences.current().getDouble("net.mograsim.logic.model.linewidth.wire." + (logicWidth == 1 ? "singlebit" : "multibit")));
		double inLineY = heightWithoutOC / 2;
		gc.drawLine(posX + oc.newX(0, inLineY), posY + oc.newY(0, inLineY), posX + oc.newX(width / 2, inLineY),
				posY + oc.newY(width / 2, inLineY));
		gc.setLineWidth(Preferences.current().getDouble("net.mograsim.logic.model.linewidth.wire.singlebit"));
		double outputHeight = 0;
		for (int i = 0; i < logicWidth; i++, outputHeight += 10)
		{
			c = BitVectorFormatter.formatAsColor(outputEnds[i]);
			if (c != null)
				gc.setForeground(ColorManager.current().toColor(c));
			gc.drawLine(posX + oc.newX(width / 2, outputHeight), posY + oc.newY(width / 2, outputHeight),
					posX + oc.newX(width, outputHeight), posY + oc.newY(width, outputHeight));
		}
		gc.setForeground(Preferences.current().getColor("net.mograsim.logic.model.color.foreground"));
		int oldLineCap = gc.getLineCap();
		int lineJoin = gc.getLineJoin();
		// TODO find better "replacement" for JOIN_BEVEL
		// TODO it looks weird that the vertical line is thinner than the single multibit wire.
		gc.setLineCap(lineJoin == SWT.JOIN_MITER ? SWT.CAP_SQUARE : lineJoin == SWT.JOIN_ROUND ? SWT.CAP_ROUND : SWT.CAP_SQUARE);
		gc.drawLine(posX + oc.newX(width / 2, 0), posY + oc.newY(width / 2, 0), posX + oc.newX(width / 2, heightWithoutOC),
				posY + oc.newY(width / 2, heightWithoutOC));
		gc.setLineWidth(Preferences.current().getDouble("net.mograsim.logic.model.linewidth.default"));
		gc.setLineCap(oldLineCap);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Splitter";
	}

	@Override
	public SplitterParams getParamsForSerializing(IdentifyParams idParams)
	{
		SplitterParams splitterParams = new SplitterParams();
		splitterParams.logicWidth = logicWidth;
		splitterParams.orientation = toggleLeftDownAlt(oc.getOrientation());
		return splitterParams;
	}

	public void setCoreModelBinding(ReadEnd inputEnd, ReadEnd[] outputEnds)
	{
		this.inputEnd = inputEnd;
		System.arraycopy(outputEnds, 0, this.outputEnds, 0, logicWidth);
	}

	public Pin getInputPin()
	{
		return inputPin;
	}

	/**
	 * Used to leave bit order intuitive (MSB left or on top)
	 */
	private static Orientation toggleLeftDownAlt(Orientation orientation)
	{
		// TODO if we upgrade to Java 12, replace with switch-expression
		switch (orientation)
		{
		case LEFT:
			return Orientation.LEFT_ALT;
		case LEFT_ALT:
			return Orientation.LEFT;
		case DOWN:
			return Orientation.DOWN_ALT;
		case DOWN_ALT:
			return Orientation.DOWN;
		default:
			return orientation;
		}
	}

	public static class SplitterParams
	{
		public int logicWidth;
		public Orientation orientation;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new SplitterAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelSplitter.class.getCanonicalName(), (m, p, n) ->
		{
			// TODO remove legacy params parsing
			SplitterParams params;
			if (p.isJsonPrimitive())
			{
				params = new SplitterParams();
				params.logicWidth = p.getAsInt();
				params.orientation = Orientation.RIGHT;
			} else
				params = JsonHandler.fromJsonTree(p, SplitterParams.class);
			return new ModelSplitter(m, params, n);
		});
	}
}