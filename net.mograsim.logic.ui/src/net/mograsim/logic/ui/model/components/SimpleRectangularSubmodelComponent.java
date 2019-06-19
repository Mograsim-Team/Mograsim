package net.mograsim.logic.ui.model.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
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

	protected void setInputPins(String... pinNames)
	{
		int inputCount = pinNames.length;
		int oldInputCount = inputSupermodelPins.size();
		double height = Math.max(inputCount, outputSupermodelPins.size()) * pinDistance;
		super.setSize(width, height);
		if (oldInputCount > inputCount)
			while (inputSupermodelPins.size() > inputCount)
			{
				inputSubmodelPins.remove(inputCount);
				super.removeSubmodelInterface(inputSupermodelPins.remove(inputCount));
			}
		else if (oldInputCount < inputCount)
			for (int i = oldInputCount; i < inputCount; i++)
			{
				Pin submodelPin = super.addSubmodelInterface(pinNames[i], logicWidth, 0, pinDistance / 2 + i * pinDistance);
				inputSubmodelPins.add(submodelPin);
				inputSupermodelPins.add(getSupermodelPin(submodelPin));
			}
		for (int i = 0; i < Math.min(oldInputCount, inputCount); i++)
		{
			if (!inputSubmodelPins.get(i).name.equals(pinNames[i]))
			{
				super.removeSubmodelInterface(inputSupermodelPins.get(i));
				Pin submodelPin = super.addSubmodelInterface(pinNames[i], logicWidth, 0, pinDistance / 2 + i * pinDistance);
				inputSubmodelPins.set(i, submodelPin);
				inputSupermodelPins.set(i, getSupermodelPin(submodelPin));
			}
		}
	}

	protected void setOutputPins(String... pinNames)
	{
		int outputCount = pinNames.length;
		int oldOutputCount = outputSupermodelPins.size();
		super.setSize(width, Math.max(inputSupermodelPins.size(), outputCount) * pinDistance);
		if (oldOutputCount > outputCount)
			while (outputSupermodelPins.size() > outputCount)
			{
				outputSubmodelPins.remove(outputCount);
				super.removeSubmodelInterface(outputSupermodelPins.get(outputCount));
			}
		else if (oldOutputCount < outputCount)
			for (int i = oldOutputCount; i < outputCount; i++)
			{
				Pin submodelPin = super.addSubmodelInterface(pinNames[i], logicWidth, width, pinDistance / 2 + i * pinDistance);
				outputSubmodelPins.add(submodelPin);
				outputSupermodelPins.add(getSupermodelPin(submodelPin));
			}
		for (int i = 0; i < Math.min(oldOutputCount, outputCount); i++)
		{
			if (!outputSubmodelPins.get(i).name.equals(pinNames[i]))
			{
				super.removeSubmodelInterface(outputSupermodelPins.get(i));
				Pin submodelPin = super.addSubmodelInterface(pinNames[i], logicWidth, width, pinDistance / 2 + i * pinDistance);
				outputSubmodelPins.set(i, submodelPin);
				outputSupermodelPins.set(i, getSupermodelPin(submodelPin));
			}
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
	protected void renderSymbol(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		Font oldFont = gc.getFont();
		gc.setFont(new Font(oldFont.getName(), labelFontHeight, oldFont.getStyle()));
		Point textExtent = gc.textExtent(label);
		gc.drawText(label, posX + (getBounds().width - textExtent.x) / 2, posY + (getBounds().height - textExtent.y) / 2, true);
		gc.setFont(new Font(oldFont.getName(), pinNameFontHeight, oldFont.getStyle()));
		for (int i = 0; i < inputSupermodelPins.size(); i++)
		{
			String pinName = inputSupermodelPins.get(i).name;
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName, posX + pinNameMargin, posY + i * pinDistance + (pinDistance - textExtent.y) / 2, true);
		}
		for (int i = 0; i < outputSupermodelPins.size(); i++)
		{
			String pinName = outputSupermodelPins.get(i).name;
			textExtent = gc.textExtent(pinName);
			gc.drawText(pinName, posX + width - textExtent.x - pinNameMargin, posY + i * pinDistance + (pinDistance - textExtent.y) / 2,
					true);
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
		m.put(kInCount, inputSupermodelPins.size());
		m.put(kOutCount, outputSupermodelPins.size());
		m.put(kLogicWidth, logicWidth);
		ret.specialized = m;
		return ret;
	}

	@Override
	protected Pin addSubmodelInterface(String name, int logicWidth, double relX, double relY)
	{
		throw new UnsupportedOperationException(
				"Can't add submodel interfaces to a SimpleRectangularSubmodelComponent directly, call setInputPins / setOutputPins instead");
	}

	@Override
	protected void removeSubmodelInterface(Pin supermodelPin)
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