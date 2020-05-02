package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
	private BitVector minimalValue;
	private BigInteger minimalValueBigInteger;
	private BitVector maximalValue;
	private BigInteger maximalValueBigInteger;
	private int length;

	private final Map<Consumer<Object>, Consumer<Object>> targetListeners;

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

		this.targetListeners = new HashMap<>();

		if (params != null)
		{
			setVectorParts(params.vectorPartTargets, params.vectorPartLengthes);
			setRange(params.minimalValue, params.maximalValue);
		}
	}

	public void set(List<String> targets, List<Integer> lengthes, BitVector minimalValue, BitVector maximalValue)
	{
		setVectorParts(targets, lengthes);
		setRange(minimalValue, maximalValue);
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

	@SuppressWarnings("null") // explicit checks for null are in place
	public void setRange(BitVector minimalValue, BitVector maximalValue)
	{
		boolean minIsNull = minimalValue == null;
		if (minIsNull != (maximalValue == null))
			throw new IllegalArgumentException("minimalValue and maximalValue must either both be null or both non-null");
		if (!minIsNull)
		{
			if (!minimalValue.isBinary())
				throw new IllegalArgumentException("minimalValue is not binary");
			if (!maximalValue.isBinary())
				throw new IllegalArgumentException("maximalValue is not binary");
			this.minimalValueBigInteger = minimalValue.getUnsignedValue();
			this.maximalValueBigInteger = maximalValue.getUnsignedValue();
		}
		this.minimalValue = minimalValue;
		this.maximalValue = maximalValue;
	}

	public BitVector getMinimalValue()
	{
		return minimalValue;
	}

	public BitVector getMaximalValue()
	{
		return maximalValue;
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
		// TODO what for non-binary values?
		if (minimalValue != null && newStateCasted.isBinary())
		{
			BigInteger newStateBigInteger = newStateCasted.getUnsignedValue();
			if (newStateBigInteger.compareTo(minimalValueBigInteger) < 0 || newStateBigInteger.compareTo(maximalValueBigInteger) > 0)
				throw new IllegalArgumentException(
						"Value out of range: should be in " + minimalValue + " - " + maximalValue + "; was " + newState);
		}
		for (int partIndex = 0, bitIndex = 0; partIndex < vectorPartTargets.size(); partIndex++)
		{
			int vectorPartLength = vectorPartLengthes.get(partIndex);
			BitVector vectorPart = newStateCasted.subVector(bitIndex, bitIndex + vectorPartLength);
			component.setHighLevelState(vectorPartTargets.get(partIndex), vectorPart);
			bitIndex += vectorPartLength;
		}
	}

	@Override
	public void addListener(Consumer<Object> stateChanged)
	{
		if (targetListeners.get(stateChanged) != null)
			// this listener is/was already registered
			return;

		Consumer<Object> targetListener = o -> stateChanged.accept(getHighLevelState());
		targetListeners.put(stateChanged, targetListener);

		for (String target : vectorPartTargets)
			component.addHighLevelStateListener(target, targetListener);
	}

	@Override
	public void removeListener(Consumer<Object> stateChanged)
	{
		Consumer<Object> targetListener = targetListeners.get(stateChanged);
		if (targetListener == null)
			// this listener is/was not registered
			return;

		for (String target : vectorPartTargets)
			component.removeHighLevelStateListener(target, targetListener);
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
		params.minimalValue = minimalValue;
		params.maximalValue = maximalValue;
		return params;
	}

	public static class BitVectorSplittingAtomicHighLevelStateHandlerParams
	{
		public List<String> vectorPartTargets;
		public List<Integer> vectorPartLengthes;
		public BitVector minimalValue;
		public BitVector maximalValue;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier
				.setSnippetSupplier(BitVectorSplittingAtomicHighLevelStateHandler.class.getCanonicalName(), SnippetDefinintion.create(
						BitVectorSplittingAtomicHighLevelStateHandlerParams.class, BitVectorSplittingAtomicHighLevelStateHandler::new));
	}
}