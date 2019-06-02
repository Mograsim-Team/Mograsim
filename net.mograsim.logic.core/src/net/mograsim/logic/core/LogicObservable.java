package net.mograsim.logic.core;

public interface LogicObservable
{
	public void registerObserver(LogicObserver ob);

	public void deregisterObserver(LogicObserver ob);

	public void notifyObservers();

}
