package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.SWT;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SplitterAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class GUISplitter extends GUIComponent
{
	private static final double width = 10;
	private static final double heightPerPin = 10;

	public final int logicWidth;
	private final Pin inputPin;

	private ReadEnd inputEnd;
	private final ReadEnd[] outputEnds;

	public GUISplitter(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUISplitter(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, name);
		this.logicWidth = logicWidth;
		setSize(width, (logicWidth - 1) * heightPerPin);
		addPin(this.inputPin = new Pin(this, "I", logicWidth, PinUsage.TRISTATE, 0, (logicWidth - 1) * heightPerPin / 2));
		double outputHeight = (logicWidth - 1) * heightPerPin;
		for (int i = 0; i < logicWidth; i++, outputHeight -= 10)
			addPin(new Pin(this, "O" + i, 1, PinUsage.TRISTATE, width, outputHeight));
		outputEnds = new ReadEnd[logicWidth];
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getPosX();
		double posY = getPosY();

		ColorDefinition c = BitVectorFormatter.formatAsColor(inputEnd);
		if (c != null)
			gc.setForeground(ColorManager.current().toColor(c));
		double inLineY = posY + (logicWidth - 1) * heightPerPin / 2;
		gc.drawLine(posX, inLineY, posX + width / 2, inLineY);
		double outputHeight = posY + (logicWidth - 1) * heightPerPin;
		for (int i = 0; i < logicWidth; i++, outputHeight -= 10)
		{
			c = BitVectorFormatter.formatAsColor(outputEnds[i]);
			if (c != null)
				gc.setForeground(ColorManager.current().toColor(c));
			gc.drawLine(posX + width / 2, outputHeight, posX + width, outputHeight);
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
	public Integer getParamsForSerializing(IdentifierGetter idGetter)
	{
		return logicWidth;
	}

	public void setLogicModelBinding(ReadEnd inputEnd, ReadEnd[] outputEnds)
	{
		this.inputEnd = inputEnd;
		System.arraycopy(outputEnds, 0, this.outputEnds, 0, logicWidth);
	}

	public Pin getInputPin()
	{
		return inputPin;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SplitterAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUISplitter.class.getCanonicalName(),
				(m, p, n) -> new GUISplitter(m, p.getAsInt(), n));
	}
}