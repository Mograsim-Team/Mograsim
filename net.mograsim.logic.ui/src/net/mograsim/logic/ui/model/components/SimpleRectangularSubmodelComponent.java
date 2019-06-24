package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ModelVisitor;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.model.wires.Pin;

public class SimpleRectangularSubmodelComponent extends SubmodelComponent
{
	public static String kLabel = "label", kInCount = "input_count", kOutCount = "output_count", kLogicWidth = "logic_width";

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

	protected SimpleRectangularSubmodelComponent(ViewModelModifiable model, int logicWidth, String label)
	{
		super(model);
		this.label = label;
		this.logicWidth = logicWidth;
		this.inputPinNames = new ArrayList<>();
		this.inputPinNamesUnmodifiable = Collections.unmodifiableList(inputPinNames);
		this.outputPinNames = new ArrayList<>();
		this.outputPinNamesUnmodifiable = Collections.unmodifiableList(outputPinNames);
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
		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), labelFontHeight, oldFont.getStyle()));
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, getPosX() + (getWidth() - textExtent.x) / 2, getPosY() + (getHeight() - textExtent.y) / 2, true);
		gc.setFont(new Font(oldFont.getName(), pinNameFontHeight, oldFont.getStyle()));
		for (int i = 0; i < inputPinNames.size(); i++)
		{
			String pinName = inputPinNames.get(i);
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName, getPosX() + pinNameMargin, getPosY() + i * pinDistance + (pinDistance - textExtent.y) / 2, true);
		}
		for (int i = 0; i < outputPinNames.size(); i++)
		{
			String pinName = outputPinNames.get(i);
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName, getPosX() + width - textExtent.x - pinNameMargin,
					getPosY() + i * pinDistance + (pinDistance - textExtent.y) / 2, true);
		}
		gc.setFont(oldFont);
	}

	@Override
	protected void renderOutline(GeneralGC gc, Rectangle visibleRegion)
	{
		gc.drawRectangle(getBounds());
	}

	@Override
	public SubmodelComponentParams calculateParams()
	{
		SubmodelComponentParams ret = super.calculateParams();
		ret.type = SimpleRectangularSubmodelComponent.class.getSimpleName();
		Map<String, Object> m = new TreeMap<>();
		m.put(kLabel, label);
		m.put(kInCount, inputPinNames.toArray());
		m.put(kOutCount, outputPinNames.toArray());
		m.put(kLogicWidth, logicWidth);
		ret.specialized = m;
		return ret;
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

	@Override
	public void accept(ModelVisitor mv)
	{
		mv.visit(this);
	}
}