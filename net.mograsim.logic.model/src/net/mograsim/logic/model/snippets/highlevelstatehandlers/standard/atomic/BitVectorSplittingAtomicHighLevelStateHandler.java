package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.HighLevelStateHandlerContext;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class BitVectorSplittingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private SubmodelComponent component;
	private final List<String> vectorPartTargets;
	private final List<Integer> vectorPartWidths;
	private int width;

	public BitVectorSplittingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context)
	{
		this(context, null);
	}

	public BitVectorSplittingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context,
			BitVectorSplittingAtomicHighLevelStateHandlerParams params)
	{
		this.component = context.component;
		this.vectorPartTargets = new ArrayList<>();
		this.vectorPartWidths = new ArrayList<>();
		if (params != null)
			setVectorParts(params.vectorPartTargets, params.vectorPartWidths);
	}

	public void set(List<String> targets, List<Integer> widths)
	{
		setVectorParts(targets, widths);
	}

	public void addVectorPart(String target, int width)
	{
		vectorPartTargets.add(target);
		vectorPartWidths.add(width);
		this.width += width;
	}

	public void clearVectorParts()
	{
		vectorPartTargets.clear();
		vectorPartWidths.clear();
		width = 0;
	}

	private void setVectorParts(List<String> targets, List<Integer> widths)
	{
		clearVectorParts();
		if (targets.size() != widths.size())
			throw new IllegalArgumentException("Targets list and widths list have different sizes");
		vectorPartTargets.addAll(targets);
		vectorPartWidths.addAll(widths);
		width += widths.stream().mapToInt(Integer::intValue).sum();
	}

	@Override
	public Object getHighLevelState()
	{
		BitVector result = BitVector.of();
		for (int partIndex = 0; partIndex < vectorPartTargets.size(); partIndex++)
		{
			Object subStateUncasted = component.getHighLevelState(vectorPartTargets.get(partIndex));
			BitVector vectorPart;
			if (subStateUncasted instanceof Bit)
				vectorPart = BitVector.of((Bit) subStateUncasted);
			else
				vectorPart = (BitVector) subStateUncasted;
			if (vectorPart.width() != vectorPartWidths.get(partIndex))
				throw new IllegalArgumentException(
						"Incorrect vector part width: " + vectorPart.width() + "; expected " + vectorPartWidths.get(partIndex));
			result = vectorPart.concat(result);
		}
		return result;
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		BitVector newStateCasted = (BitVector) newState;
		if (newStateCasted.width() != width)
			throw new IllegalArgumentException("Incorrect vector width: " + newStateCasted.width() + "; expected " + width);
		for (int partIndex = vectorPartTargets.size() - 1, bitIndex = 0; partIndex >= 0; partIndex--)
		{
			int vectorPartWidth = vectorPartWidths.get(partIndex);
			BitVector vectorPart = newStateCasted.subVector(bitIndex, bitIndex + vectorPartWidth);
			component.setHighLevelState(vectorPartTargets.get(partIndex), vectorPart);
			bitIndex += vectorPartWidth;
		}
	}

	@Override
	public BitVectorSplittingAtomicHighLevelStateHandlerParams getParamsForSerializing(IdentifierGetter idGetter)
	{
		BitVectorSplittingAtomicHighLevelStateHandlerParams params = new BitVectorSplittingAtomicHighLevelStateHandlerParams();
		params.vectorPartTargets = new ArrayList<>(vectorPartTargets);
		params.vectorPartWidths = new ArrayList<>(vectorPartWidths);
		return params;
	}

	public static class BitVectorSplittingAtomicHighLevelStateHandlerParams
	{
		public List<String> vectorPartTargets;
		public List<Integer> vectorPartWidths;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier
				.setSnippetSupplier(BitVectorSplittingAtomicHighLevelStateHandler.class.getCanonicalName(), SnippetDefinintion.create(
						BitVectorSplittingAtomicHighLevelStateHandlerParams.class, BitVectorSplittingAtomicHighLevelStateHandler::new));
	}
}