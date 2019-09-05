package net.mograsim.logic.model.model.components.submodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;

public class SimpleRectangularSubmodelComponent extends SubmodelComponent
{
	public static final double width = 35;
	public static final double pinDistance = 10;
	public static final double pinNameMargin = .5;
	public static final double labelFontHeight = 5;
	public static final double pinNameFontHeight = 3.5;

	public final String label;
	protected final int logicWidth;

	private final List<String> inputPinNames;
	private final List<String> inputPinNamesUnmodifiable;
	private final List<String> outputPinNames;
	private final List<String> outputPinNamesUnmodifiable;

	public SimpleRectangularSubmodelComponent(LogicModelModifiable model, int logicWidth, String label)
	{
		this(model, logicWidth, label, null);
	}

	public SimpleRectangularSubmodelComponent(LogicModelModifiable model, int logicWidth, String label, String name)
	{
		super(model, name);
		this.label = label;
		this.logicWidth = logicWidth;
		this.inputPinNames = new ArrayList<>();
		this.inputPinNamesUnmodifiable = Collections.unmodifiableList(inputPinNames);
		this.outputPinNames = new ArrayList<>();
		this.outputPinNamesUnmodifiable = Collections.unmodifiableList(outputPinNames);

		SimpleRectangularLikeParams rendererParams = new SimpleRectangularLikeParams();
		rendererParams.centerText = label;
		rendererParams.centerTextHeight = labelFontHeight;
		rendererParams.horizontalComponentCenter = width / 2;
		rendererParams.pinLabelHeight = pinNameFontHeight;
		rendererParams.pinLabelMargin = pinNameMargin;
		setSymbolRenderer(new SimpleRectangularLikeSymbolRenderer(this, rendererParams));
		setOutlineRenderer(new DefaultOutlineRenderer(this));
	}

	protected void setInputPins(String... pinNames)
	{
		setIOPins(0, inputPinNames, outputPinNames, PinUsage.INPUT, pinNames);
	}

	protected void setOutputPins(String... pinNames)
	{
		setIOPins(width, outputPinNames, inputPinNames, PinUsage.OUTPUT, pinNames);
	}

	private void setIOPins(double relX, List<String> pinNamesListThisSide, List<String> pinNamesListOtherSide, PinUsage usage,
			String... newPinNames)
	{
		int inputCount = newPinNames.length;
		List<String> newPinNamesList = Arrays.asList(newPinNames);
		if (new HashSet<>(newPinNamesList).size() != inputCount)
			throw new IllegalArgumentException("Pin names contain duplicates");
		for (String pinName : newPinNamesList)
			if (pinNamesListOtherSide.contains(pinName))
				throw new IllegalArgumentException("Can't add pin. There is a pin on the other side with the same name: " + pinName);
		super.setSize(width, Math.max(inputCount, pinNamesListOtherSide.size()) * pinDistance);
		for (int i = 0; i < inputCount; i++)
		{
			String pinName = newPinNames[i];
			int oldPinIndex = pinNamesListThisSide.indexOf(pinName);
			if (oldPinIndex == -1)
				super.addSubmodelInterface(
						new MovablePin(model, this, pinName, logicWidth, usage, relX, pinDistance / 2 + i * pinDistance));
			else
				getSupermodelMovablePin(pinName).setRelPos(relX, pinDistance / 2 + i * pinDistance);
		}
		for (String pinName : pinNamesListThisSide)
			if (!newPinNamesList.contains(pinName))
				super.removeSubmodelInterface(pinName);
		pinNamesListThisSide.clear();
		pinNamesListThisSide.addAll(newPinNamesList);
	}

	public List<String> getInputPinNames()
	{
		return inputPinNamesUnmodifiable;
	}

	public List<String> getOutputPinNames()
	{
		return outputPinNamesUnmodifiable;
	}

	@Override
	protected Pin addSubmodelInterface(MovablePin supermodelPin)
	{
		throw new UnsupportedOperationException(
				"Can't add submodel interfaces to a SimpleRectangularSubmodelComponent directly, call setInputPins / setOutputPins instead");
	}

	@Override
	protected void removeSubmodelInterface(String name)
	{
		throw new UnsupportedOperationException(
				"Can't remove submodel interfaces of a SimpleRectangularSubmodelComponent directly, call setInputPins / setOutputPins instead");
	}

	@Override
	protected void setSize(double width, double height)
	{
		throw new UnsupportedOperationException(
				"Can't set the size of a SimpleRectangularSubmodelComponent directly, call setInputPins / setOutputPins instead");
	}
}