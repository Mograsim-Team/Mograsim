package net.mograsim.logic.ui.model.components.submodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonObject;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer;
import net.mograsim.logic.ui.serializing.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;
import net.mograsim.preferences.Preferences;

public class SimpleRectangularSubmodelComponent extends SubmodelComponent
{
	private static final double width = 35;
	private static final double pinDistance = 10;
	private static final double pinNameMargin = .5;
	private static final double labelFontHeight = 5;
	private static final double pinNameFontHeight = 3.5;

	private final String label;
	protected final int logicWidth;

	private final List<String> inputPinNames;
	private final List<String> inputPinNamesUnmodifiable;
	private final List<String> outputPinNames;
	private final List<String> outputPinNamesUnmodifiable;

	private Renderer symbolRenderer;

	public SimpleRectangularSubmodelComponent(ViewModelModifiable model, int logicWidth, String label)
	{
		super(model);
		this.label = label;
		this.logicWidth = logicWidth;
		this.inputPinNames = new ArrayList<>();
		this.inputPinNamesUnmodifiable = Collections.unmodifiableList(inputPinNames);
		this.outputPinNames = new ArrayList<>();
		this.outputPinNamesUnmodifiable = Collections.unmodifiableList(outputPinNames);

		SimpleRectangularLikeParams rendererParams = new SimpleRectangularLikeParams();
		rendererParams.centerText = label;
		rendererParams.centerTextHeight = labelFontHeight;
		rendererParams.horizontalComponentCenter = getWidth() / 2;
		rendererParams.pinLabelHeight = pinNameFontHeight;
		rendererParams.pinLabelMargin = pinNameMargin;
		symbolRenderer = new SimpleRectangularLikeSymbolRenderer(this, rendererParams);
	}

	protected void setInputPins(String... pinNames)
	{
		setIOPins(0, inputPinNames, outputPinNames, pinNames);
	}

	protected void setOutputPins(String... pinNames)
	{
		setIOPins(width, outputPinNames, inputPinNames, pinNames);
	}

	private void setIOPins(double relX, List<String> pinNamesListThisSide, List<String> pinNamesListOtherSide, String... newPinNames)
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
				super.addSubmodelInterface(new MovablePin(this, pinName, logicWidth, relX, pinDistance / 2 + i * pinDistance));
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
	protected void renderSymbol(GeneralGC gc, Rectangle visibleRegion)
	{
		symbolRenderer.render(gc, visibleRegion);
	}

	@Override
	protected void renderOutline(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.ui.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getBounds());
	}

	// serializing

	@Override
	public SubmodelComponentParams calculateParams(Function<GUIComponent, String> getIdentifier)
	{
		SubmodelComponentParams params = super.calculateParams(getIdentifier);
		JsonObject symbolRendererParams = new JsonObject();
		symbolRendererParams.addProperty("centerText", label);
		symbolRendererParams.addProperty("horizontalComponentCenter", getWidth() / 2);
		symbolRendererParams.addProperty("centerTextHeight", labelFontHeight);
		symbolRendererParams.addProperty("pinLabelHeight", pinNameFontHeight);
		symbolRendererParams.addProperty("pinLabelMargin", pinNameMargin);
		params.symbolRendererSnippetID = "SimpleRectangularLikeSymbolRenderer";
		params.symbolRendererParams = symbolRendererParams;
		return params;
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