package net.mograsim.logic.model.model.components.atomic;

import static net.mograsim.logic.model.preferences.RenderPreferences.FOREGROUND_COLOR;
import static net.mograsim.logic.model.preferences.RenderPreferences.TEXT_COLOR;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.IdentifyParams;

public class SimpleRectangularModelGate extends ModelComponent
{
	private static final double width = 20;
	private static final double pinDistance = 10;
	private static final double fontHeight = 5;
	private static final double invertedCircleDiam = 3.5;

	private final String id;

	private final String label;
	private final boolean isInverted;
	protected final int logicWidth;
	private final double rectWidth;

	private MovablePin outputPin;
	private final List<Pin> inputPins;

	protected SimpleRectangularModelGate(LogicModelModifiable model, String id, String label, boolean isInverted, int logicWidth,
			String name)
	{
		this(model, id, label, isInverted, logicWidth, name, true);
	}

	protected SimpleRectangularModelGate(LogicModelModifiable model, String id, String label, boolean isInverted, int logicWidth,
			String name, boolean callInit)
	{
		super(model, name, false);
		this.id = id;
		this.label = label;
		this.logicWidth = logicWidth;
		this.isInverted = isInverted;
		this.rectWidth = width - (isInverted ? invertedCircleDiam : 0);
		this.outputPin = new MovablePin(model, this, "Y", logicWidth, PinUsage.OUTPUT, width, 0);
		addPin(outputPin);
		this.inputPins = new ArrayList<>();
		setInputCount(1);

		if (callInit)
			init();
	}

	protected void setInputCount(int inputCount)
	{
		int oldInputCount = inputPins.size();
		setSize(width, inputCount * pinDistance);
		if (oldInputCount > inputCount)
			while (inputPins.size() > inputCount)
				removePin(inputPins.remove(inputCount).name);
		else if (oldInputCount < inputCount)
			for (int i = oldInputCount; i < inputCount; i++)
			{
				// TODO what for more than 24 input pins?
				Pin pin = new Pin(model, this, String.valueOf((char) ('A' + i)), logicWidth, PinUsage.INPUT, 0,
						pinDistance / 2 + i * pinDistance);
				inputPins.add(pin);
				addPin(pin);
			}
		outputPin.setRelPos(width, inputCount * pinDistance / 2);
	}

	@Override
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion)
	{
		Color foreground = renderPrefs.getColor(FOREGROUND_COLOR);
		if (foreground != null)
			gc.setForeground(foreground);
		double height = (getPins().size() - 1) * pinDistance;
		gc.drawRectangle(getPosX(), getPosY(), rectWidth, height);
		if (isInverted)
			gc.drawOval(getPosX() + rectWidth, getPosY() + (height - invertedCircleDiam) / 2, invertedCircleDiam, invertedCircleDiam);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		Color textColor = renderPrefs.getColor(TEXT_COLOR);
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (rectWidth - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	// serializing

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return id;
	}

	/**
	 * {@link SimpleRectangularModelGate}s implementation returns a {@link JsonPrimitive} of type int containing the {@link #logicWidth} of
	 * this component.
	 * 
	 * @see ModelComponent#getParamsForSerializing()
	 */
	@Override
	public Integer getParamsForSerializing(IdentifyParams idParams)
	{
		return logicWidth;
	}
}