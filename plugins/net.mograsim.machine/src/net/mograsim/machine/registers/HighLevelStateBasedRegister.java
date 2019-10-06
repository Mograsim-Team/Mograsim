package net.mograsim.machine.registers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.ModelComponent;

public class HighLevelStateBasedRegister extends SimpleRegister implements ModelComponentBasedRegister
{
	private final String[] hlsIDsToConcat;
	private final int[] hlsWidthes;

	private final Map<Consumer<BitVector>, Consumer<Object>> modelListenersPerRegisterListener;

	public HighLevelStateBasedRegister(String id, String hlsID, int logicWidth)
	{
		this(id, new int[] { logicWidth }, hlsID);
	}

	public HighLevelStateBasedRegister(String id, int[] hlsWidthes, String... hlsIDsToConcat)
	{
		super(id, Arrays.stream(hlsWidthes).sum());
		if (hlsIDsToConcat.length != hlsWidthes.length)
			throw new IllegalArgumentException();
		this.hlsIDsToConcat = Arrays.copyOf(hlsIDsToConcat, hlsIDsToConcat.length);
		this.hlsWidthes = Arrays.copyOf(hlsWidthes, hlsIDsToConcat.length);

		this.modelListenersPerRegisterListener = new HashMap<>();
	}

	@Override
	public BitVector read(ModelComponent component)
	{
		BitVector result = BitVector.of();
		for (int i = 0; i < hlsIDsToConcat.length; i++)
		{
			BitVector hls = (BitVector) component.getHighLevelState(hlsIDsToConcat[i]);
			if (hls.length() != hlsWidthes[i])
				throw new IllegalArgumentException();
			result = result.concat(hls);
		}
		return result;
	}

	@Override
	public void write(ModelComponent component, BitVector value)
	{
		if (value.length() != getWidth())
			throw new IllegalArgumentException();
		for (int i = 0, off = 0; i < hlsIDsToConcat.length; i++)
		{
			int oldOff = off;
			off += hlsWidthes[i];
			component.setHighLevelState(hlsIDsToConcat[i], value.subVector(oldOff, off));
		}
	}

	@Override
	public void addListener(ModelComponent component, Consumer<BitVector> listener)
	{
		if (modelListenersPerRegisterListener.containsKey(listener))
			return;

		Consumer<Object> modelListener = v -> listener.accept(read(component));
		for (String hlsID : hlsIDsToConcat)
			component.addHighLevelStateListener(hlsID, modelListener);
	}

	@Override
	public void removeListener(ModelComponent component, Consumer<BitVector> listener)
	{
		Consumer<Object> modelListener = modelListenersPerRegisterListener.get(listener);
		if (modelListener == null)
			return;

		for (String hlsID : hlsIDsToConcat)
			component.removeHighLevelStateListener(hlsID, modelListener);
	}
}