package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ModelVisitor;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.model.wires.Pin;

public class SimpleRectangularGUIGate extends GUIComponent
{
	private static final double width = 20;
	private static final double pinDistance = 10;
	private static final double fontHeight = 5;
	private static final double invertedCircleDiam = 3.5;

	private final String label;
	protected final int logicWidth;
	private final boolean isInverted;
	private final double rectWidth;

	private MovablePin outputPin;
	private final List<Pin> inputPins;

	public static final String kLogicWidth = "logicWidth";

	protected SimpleRectangularGUIGate(ViewModelModifiable model, int logicWidth, String label, boolean isInverted)
	{
		super(model);
		this.label = label;
		this.logicWidth = logicWidth;
		this.isInverted = isInverted;
		this.rectWidth = width - (isInverted ? invertedCircleDiam : 0);
		this.outputPin = new MovablePin(this, "Y", logicWidth, width, 0);
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
				Pin pin = new Pin(this, String.valueOf((char) ('A' + i)), logicWidth, 0, pinDistance / 2 + i * pinDistance);
				inputPins.add(pin);
				addPin(pin);
			}
		outputPin.setRelPos(width, inputCount * pinDistance / 2);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double height = (getPins().size() - 1) * pinDistance;
		gc.drawRectangle(getPosX(), getPosY(), rectWidth, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, getPosX() + (rectWidth - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
		if (isInverted)
			gc.drawOval(getPosX() + rectWidth, getPosY() + (height - invertedCircleDiam) / 2, invertedCircleDiam, invertedCircleDiam);
	}

	@Override
	public Map<String, Object> getInstantiationParameters()
	{
		Map<String, Object> m = super.getInstantiationParameters();
		m.put(kLogicWidth, logicWidth);
		return m;
	}

	@Override
	public void accept(ModelVisitor mv)
	{
		mv.visit(this);
	}
}