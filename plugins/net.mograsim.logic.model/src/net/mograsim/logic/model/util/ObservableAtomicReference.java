package net.mograsim.logic.model.util;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ObservableAtomicReference<V>
{
	private final AtomicReference<V> ref;

	private final List<Consumer<ObservableAtomicReference<V>>> observers;

	public ObservableAtomicReference()
	{
		ref = new AtomicReference<>();
		observers = new ArrayList<>();
	}

	public ObservableAtomicReference(V initialValue)
	{
		ref = new AtomicReference<>(initialValue);
		observers = new ArrayList<>();
	}

	/**
	 * Returns the current value, with memory effects as specified by {@link VarHandle#getVolatile}.
	 *
	 * @return the current value
	 */
	public V get()
	{
		return ref.get();
	}

	/**
	 * Sets the value to {@code newValue}, with memory effects as specified by {@link VarHandle#setVolatile}.
	 *
	 * @param newValue the new value
	 */
	public void set(V newValue)
	{
		ref.set(newValue);
		callObservers();
	}

	/**
	 * Atomically sets the value to {@code newValue} and returns the old value, with memory effects as specified by
	 * {@link VarHandle#getAndSet}.
	 *
	 * @param newValue the new value
	 * @return the previous value
	 */
	public V getAndSet(V newValue)
	{
		V oldValue = ref.getAndSet(newValue);
		callObservers();
		return oldValue;
	}

	/**
	 * Atomically updates (with memory effects as specified by {@link VarHandle#compareAndSet}) the current value with the results of
	 * applying the given function, returning the updated value. The function should be side-effect-free, since it may be re-applied when
	 * attempted updates fail due to contention among threads.
	 *
	 * @param updateFunction a side-effect-free function
	 * @return the updated value
	 */
	public V updateAndGet(UnaryOperator<V> updateFunction)
	{
		V updatedValue = ref.updateAndGet(updateFunction);
		callObservers();
		return updatedValue;
	}

	public void addObserver(Consumer<ObservableAtomicReference<V>> obs)
	{
		observers.add(obs);
	}

	public void removeObserver(Consumer<ObservableAtomicReference<V>> obs)
	{
		observers.add(obs);
	}

	private void callObservers()
	{
		observers.forEach(o -> o.accept(this));
	}
}