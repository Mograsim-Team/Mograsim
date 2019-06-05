package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.Pin;

public class SimpleRectangularSubmodelComponent extends SubmodelComponent
{
	private static final double width = 35;
	private static final double pinDistance = 10;
	private static final double fontHeight = 5;

	private final String label;
	protected final int logicWidth;

	private final List<Pin> inputSupermodelPins;
	private final List<Pin> inputSupermodelPinsUnmodifiable;
	private final List<Pin> outputSupermodelPins;
	private final List<Pin> outputSupermodelPinsUnmodifiable;
	private final List<Pin> inputSubmodelPins;
	private final List<Pin> inputSubmodelPinsUnmodifiable;
	private final List<Pin> outputSubmodelPins;
	private final List<Pin> outputSubmodelPinsUnmodifiable;

	protected SimpleRectangularSubmodelComponent(ViewModelModifiable model, int logicWidth, String label)
	{
		super(model);
		this.label = label;
		this.logicWidth = logicWidth;
		this.inputSupermodelPins = new ArrayList<>();
		this.inputSupermodelPinsUnmodifiable = Collections.unmodifiableList(inputSupermodelPins);
		this.outputSupermodelPins = new ArrayList<>();
		this.outputSupermodelPinsUnmodifiable = Collections.unmodifiableList(outputSupermodelPins);
		this.inputSubmodelPins = new ArrayList<>();
		this.inputSubmodelPinsUnmodifiable = Collections.unmodifiableList(inputSubmodelPins);
		this.outputSubmodelPins = new ArrayList<>();
		this.outputSubmodelPinsUnmodifiable = Collections.unmodifiableList(outputSubmodelPins);
	}

	protected void setInputCount(int inputCount)
	{
		int oldInputCount = inputSupermodelPins.size();
		double height = Math.max(inputCount, outputSupermodelPins.size()) * pinDistance;
		setSize(width, height);
		if (oldInputCount > inputCount)
			while (inputSupermodelPins.size() > inputCount)
			{
				inputSubmodelPins.remove(inputCount);
				removePin(inputSupermodelPins.remove(inputCount));
			}
		else if (oldInputCount < inputCount)
			for (int i = oldInputCount; i < inputCount; i++)
			{
				Pin submodelPin = addSubmodelInterface(logicWidth, 0, pinDistance / 2 + i * pinDistance);
				inputSubmodelPins.add(submodelPin);
				inputSupermodelPins.add(getSupermodelPin(submodelPin));
			}
	}

	protected void setOutputCount(int outputCount)
	{
		int oldOutputCount = outputSupermodelPins.size();
		setSize(width, Math.max(inputSupermodelPins.size(), outputCount) * pinDistance);
		if (oldOutputCount > outputCount)
			while (outputSupermodelPins.size() > outputCount)
			{
				outputSubmodelPins.remove(outputCount);
				removePin(outputSupermodelPins.get(outputCount));
			}
		else if (oldOutputCount < outputCount)
			for (int i = oldOutputCount; i < outputCount; i++)
			{
				Pin submodelPin = addSubmodelInterface(logicWidth, width, pinDistance / 2 + i * pinDistance);
				outputSubmodelPins.add(submodelPin);
				outputSupermodelPins.add(getSupermodelPin(submodelPin));
			}
	}

	public List<Pin> getInputPins()
	{
		return inputSupermodelPinsUnmodifiable;
	}

	public List<Pin> getOutputPins()
	{
		return outputSupermodelPinsUnmodifiable;
	}

	protected List<Pin> getInputSubmodelPins()
	{
		return inputSubmodelPinsUnmodifiable;
	}

	protected List<Pin> getOutputSubmodelPins()
	{
		return outputSubmodelPinsUnmodifiable;
	}

	@Override
	protected void renderSymbol(GeneralGC gc)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
		gc.setFont(labelFont);
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (getBounds().width - textExtent.x) / 2, posY + (getBounds().height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	@Override
	protected void renderOutline(GeneralGC gc)
	{
		gc.drawRectangle(getBounds());
	}
}