package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class BitVectorSplittingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private final SubmodelComponent component;
	private final List<String> vectorPartTargets;
	private final List<String> vectorPartTargetsUnmodifiable;
	private final List<Integer> vectorPartLengthes;
	private final List<Integer> vectorPartLengthesUnmodifiable;
	private int length;

	public BitVectorSplittingAtomicHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public BitVectorSplittingAtomicHighLevelStateHandler(SubmodelComponent component,
			BitVectorSplittingAtomicHighLevelStateHandlerParams params)
	{
		this.component = component;
		this.vectorPartTargets = new ArrayList<>();
		this.vectorPartTargetsUnmodifiable = Collections.unmodifiableList(vectorPartTargets);
		this.vectorPartLengthes = new ArrayList<>();
		this.vectorPartLengthesUnmodifiable = Collections.unmodifiableList(vectorPartLengthes);
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
		this.length += length;
	}

	public void clearVectorParts()
	{
		vectorPartTargets.clear();
		vectorPartLengthes.clear();
		length = 0;
	}

	private void setVectorParts(List<String> targets, List<Integer> lengthes)
	{
		clearVectorParts();
		if (targets.size() != lengthes.size())
			throw new IllegalArgumentException("Targets list and lengthes list have different sizes");
		vectorPartTargets.addAll(targets);
		vectorPartLengthes.addAll(lengthes);
		length += lengthes.stream().mapToInt(Integer::intValue).sum();
	}

	public List<String> getVectorPartTargets()
	{
		return vectorPartTargetsUnmodifiable;
	}

	public List<Integer> getVectorPartLenghtes()
	{
		return vectorPartLengthesUnmodifiable;
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
			if (vectorPart.length() != vectorPartLengthes.get(partIndex))
				throw new IllegalArgumentException(
						"Incorrect vector part length: " + vectorPart.length() + "; expected " + vectorPartLengthes.get(partIndex));
			result = result.concat(vectorPart);
		}
		return result;
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		BitVector newStateCasted = (BitVector) newState;
		if (newStateCasted.length() != length)
			throw new IllegalArgumentException("Incorrect vector length: " + newStateCasted.length() + "; expected " + length);
		for (int partIndex = 0, bitIndex = 0; partIndex < vectorPartTargets.size(); partIndex++)
		{
			int vectorPartLength = vectorPartLengthes.get(partIndex);
			BitVector vectorPart = newStateCasted.subVector(bitIndex, bitIndex + vectorPartLength);
			component.setHighLevelState(vectorPartTargets.get(partIndex), vectorPart);
			bitIndex += vectorPartLength;
		}
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "bitVectorSplitting";
	}

	@Override
	public BitVectorSplittingAtomicHighLevelStateHandlerParams getParamsForSerializing(IdentifyParams idParams)
	{
		BitVectorSplittingAtomicHighLevelStateHandlerParams params = new BitVectorSplittingAtomicHighLevelStateHandlerParams();
		params.vectorPartTargets = new ArrayList<>(vectorPartTargets);
		params.vectorPartLengthes = new ArrayList<>(vectorPartLengthes);
		return params;
	}

	public static class BitVectorSplittingAtomicHighLevelStateHandlerParams
	{
		public List<String> vectorPartTargets;
		public List<Integer> vectorPartLengthes;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier
				.setSnippetSupplier(BitVectorSplittingAtomicHighLevelStateHandler.class.getCanonicalName(), SnippetDefinintion.create(
						BitVectorSplittingAtomicHighLevelStateHandlerParams.class, BitVectorSplittingAtomicHighLevelStateHandler::new));
	}
}