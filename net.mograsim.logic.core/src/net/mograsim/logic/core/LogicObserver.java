package net.mograsim.logic.core;

@FunctionalInterface
public interface LogicObserver
{
	public void update(LogicObservable initiator);
}
