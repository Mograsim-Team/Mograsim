package net.mograsim.logic.model.model.components.atomic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.preferences.Preferences;

public class SimpleRectangularGUIGate extends GUIComponent
{
	private static final double width = 20;
	private static final double pinDistance = 10;
	private static final double fontHeight = 5;
	private static final double invertedCircleDiam = 3.5;

	private final String label;
	private final boolean isInverted;
	protected final int logicWidth;
	private final double rectWidth;

	private MovablePin outputPin;
	private final List<Pin> inputPins;

	protected SimpleRectangularGUIGate(ViewModelModifiable model, String label, boolean isInverted, int logicWidth, String name)
	{
		super(model, name);
		this.label = label;
		this.logicWidth = logicWidth;
		this.isInverted = isInverted;
		this.rectWidth = width - (isInverted ? invertedCircleDiam : 0);
		this.outputPin = new MovablePin(this, "Y", logicWidth, PinUsage.OUTPUT, width, 0);
		addPin(outputPin);
		this.inputPins = new ArrayList<>();
		setInputCount(1);
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
				Pin pin = new Pin(this, String.valueOf((char) ('A' + i)), logicWidth, PinUsage.INPUT, 0, pinDistance / 2 + i * pinDistance);
				inputPins.add(pin);
				addPin(pin);
			}
		outputPin.setRelPos(width, inputCount * pinDistance / 2);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
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
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (rectWidth - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	// serializing

	/**
	 * {@link SimpleRectangularGUIGate}s implementation returns a {@link JsonPrimitive} of type int containing the {@link #logicWidth} of
	 * this component.
	 * 
	 * @see GUIComponent#getParamsForSerializing()
	 */
	@Override
	public JsonElement getParamsForSerializing(IdentifierGetter idGetter)
	{
		return new JsonPrimitive(logicWidth);
	}
}