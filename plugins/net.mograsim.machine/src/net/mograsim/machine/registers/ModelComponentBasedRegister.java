package net.mograsim.machine.registers;

import java.util.function.Consumer;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.ModelComponent;

public interface ModelComponentBasedRegister
{
	public BitVector read(ModelComponent component);

	public void write(ModelComponent component, BitVector value);

	public void addListener(ModelComponent component, Consumer<BitVector> listener);

	public void removeListener(ModelComponent component, Consumer<BitVector> listener);
}