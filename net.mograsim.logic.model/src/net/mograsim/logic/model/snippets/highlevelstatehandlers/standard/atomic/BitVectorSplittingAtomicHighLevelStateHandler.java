package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.HighLevelStateHandlerContext;

public class BitVectorSplittingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private SubmodelComponent component;
	private final List<String> vectorPartTargets;
	private final List<Integer> vectorPartLengthes;

	public BitVectorSplittingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context)
	{
		this(context, null);
	}

	public BitVectorSplittingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context,
			BitVectorSplittingAtomicHighLevelStateHandlerParams params)
	{
		this.component = context.component;
		this.vectorPartTargets = new ArrayList<>();
		this.vectorPartLengthes = new ArrayList<>();
		if (params != null)
			setVectorParts(params.vectorPartTargets, params.vectorPartLengthes);
	}

	public void set(List<String> targets, List<Integer> lengthes)
	{
		setVectorParts(targets, lengthes);
	}

	public void addVectorPart(String target, int length)
	{
		vectorPartTargets.add(target);
		vectorPartLengthes.add(length);
	}

	public void clearVectorParts()
	{
		vectorPartTargets.clear();
		vectorPartLengthes.clear();
	}

	private void setVectorParts(List<String> targets, List<Integer> lengthes)
	{
		clearVectorParts();
		if (targets.size() != lengthes.size())
			throw new IllegalArgumentException("Targets list and lenghtes list have different sizes");
		vectorPartTargets.addAll(targets);
		vectorPartLengthes.addAll(lengthes);
	}

	@Override
	public Object getHighLevelState()
	{
		BitVector result = BitVector.of();
		for (int partIndex = 0; partIndex < vectorPartTargets.size(); partIndex++)
		{
			BitVector vectorPart = (BitVector) component.getHighLevelState(vectorPartTargets.get(partIndex));
			if (vectorPart.length() != vectorPartLengthes.get(partIndex))
				throw new IllegalArgumentException(
						"Illegal vector part length: " + vectorPart.length() + "; expected " + vectorPartLengthes.get(partIndex));
			result = result.concat(vectorPart);// TODO is the bit order correct?
		}
		return result;
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		BitVector newStateCasted = (BitVector) newState;
		for (int partIndex = 0, bitIndex = 0; partIndex < vectorPartTargets.size(); partIndex++)
		{
			int vectorPartLength = vectorPartLengthes.get(partIndex);
			BitVector vectorPart = newStateCasted.subVector(bitIndex, vectorPartLength);// TODO is the bit order correct?
			component.setHighLevelState(vectorPartTargets.get(partIndex), vectorPart);
			bitIndex += vectorPartLength;
		}
	}

	public static class BitVectorSplittingAtomicHighLevelStateHandlerParams
	{
		public List<String> vectorPartTargets;
		public List<Integer> vectorPartLengthes;
	}
}