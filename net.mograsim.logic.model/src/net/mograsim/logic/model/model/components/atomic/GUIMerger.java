package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.SWT;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.MergerAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class GUIMerger extends GUIComponent
{
	private static final double width = 10;
	private static final double heightPerPin = 10;

	public final int logicWidth;
	private final Pin outputPin;

	private final ReadEnd[] inputEnds;
	private ReadEnd outputEnd;

	public GUIMerger(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUIMerger(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, name);
		this.logicWidth = logicWidth;
		setSize(width, (logicWidth - 1) * heightPerPin);
		double inputHeight = (logicWidth - 1) * heightPerPin;
		for (int i = 0; i < logicWidth; i++, inputHeight -= 10)
			addPin(new Pin(this, "I" + i, 1, PinUsage.TRISTATE, 0, inputHeight));
		addPin(this.outputPin = new Pin(this, "O", logicWidth, PinUsage.TRISTATE, width, (logicWidth - 1) * heightPerPin / 2));
		inputEnds = new ReadEnd[logicWidth];
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getPosX();
		double posY = getPosY();

		ColorDefinition c = BitVectorFormatter.formatAsColor(outputEnd);
		if (c != null)
			gc.setForeground(ColorManager.current().toColor(c));
		double outLineY = posY + (logicWidth - 1) * heightPerPin / 2;
		gc.drawLine(posX + width / 2, outLineY, posX + width, outLineY);
		double inputHeight = posY + (logicWidth - 1) * heightPerPin;
		for (int i = 0; i < logicWidth; i++, inputHeight -= 10)
		{
			c = BitVectorFormatter.formatAsColor(inputEnds[i]);
			if (c != null)
				gc.setForeground(ColorManager.current().toColor(c));
			gc.drawLine(posX, inputHeight, posX + width / 2, inputHeight);
		}
		gc.setForeground(Preferences.current().getColor("net.mograsim.logic.model.color.foreground"));
		int oldLineCap = gc.getLineCap();
		int lineJoin = gc.getLineJoin();
		// TODO find better "replacement" for JOIN_BEVEL
		gc.setLineCap(lineJoin == SWT.JOIN_MITER ? SWT.CAP_SQUARE : lineJoin == SWT.JOIN_ROUND ? SWT.CAP_ROUND : SWT.CAP_SQUARE);
		gc.drawLine(posX + width / 2, posY, posX + width / 2, posY + heightPerPin * (logicWidth - 1));
		gc.setLineCap(oldLineCap);
	}

	@Override
	public JsonElement getParamsForSerializing(IdentifierGetter idGetter)
	{
		return new JsonPrimitive(logicWidth);
	}

	public void setLogicModelBinding(ReadEnd[] inputEnds, ReadEnd outputEnd)
	{
		System.arraycopy(inputEnds, 0, this.inputEnds, 0, logicWidth);
		this.outputEnd = outputEnd;
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new MergerAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUIMerger.class.getCanonicalName(),
				(m, p, n) -> new GUIMerger(m, p.getAsInt(), n));
	}
}