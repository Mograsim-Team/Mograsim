package era.mi.gui.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.wires.MovablePin;
import era.mi.gui.model.wires.Pin;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

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
	private final List<Pin> inputPinsUnmodifiable;

	protected SimpleRectangularGUIGate(ViewModel model, int logicWidth, String label, boolean isInverted)
	{
		super(model);
		this.label = label;
		this.logicWidth = logicWidth;
		this.isInverted = isInverted;
		this.rectWidth = width - (isInverted ? invertedCircleDiam : 0);
		this.outputPin = new MovablePin(this, logicWidth, width, 0);
		addPin(outputPin);
		this.inputPins = new ArrayList<>();
		this.inputPinsUnmodifiable = Collections.unmodifiableList(inputPins);
		setInputCount(1);
	}

	protected void setInputCount(int inputCount)
	{
		int oldInputCount = inputPins.size();
		setSize(width, inputCount * pinDistance);
		if (oldInputCount > inputCount)
			while (inputPins.size() > inputCount)
				removePin(inputPins.get(inputCount));
		else if (oldInputCount < inputCount)
			for (int i = oldInputCount; i < inputCount; i++)
			{
				Pin pin = new Pin(this, logicWidth, 0, pinDistance / 2 + i * pinDistance);
				inputPins.add(pin);
				addPin(pin);
			}
		outputPin.setRelPos(width, inputCount * pinDistance / 2);
	}

	public Pin getOutputPin()
	{
		return outputPin;
	}

	public List<Pin> getInputPins()
	{
		return inputPinsUnmodifiable;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		double height = inputPins.size() * pinDistance;
		gc.drawRectangle(posX, posY, rectWidth, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (rectWidth - textExtent.x) / 2, posY + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
		if (isInverted)
			gc.drawOval(posX + rectWidth, posY + (height - invertedCircleDiam) / 2, invertedCircleDiam, invertedCircleDiam);
	}
}