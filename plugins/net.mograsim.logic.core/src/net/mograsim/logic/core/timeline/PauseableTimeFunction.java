package net.mograsim.logic.core.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

public class PauseableTimeFunction implements LongSupplier
{
	private boolean paused = false;
	private long unpausedSysTime = 0, lastPausedInternalTime = 0;
	private double speedFactor = 1;

	private final List<Consumer<Double>> simulTimeToRealTimeFactorChangedListeners = new ArrayList<>();

	public void pause()
	{
		if (!paused)
		{
			lastPausedInternalTime = getAsLong();
			paused = true;
		}
	}

	public void unpause()
	{
		if (paused)
		{
			paused = false;
			unpausedSysTime = System.nanoTime() / 1000;
		}
	}

	@Override
	public long getAsLong()
	{
		return (long) (paused ? lastPausedInternalTime
				: lastPausedInternalTime + (System.nanoTime() / 1000 - unpausedSysTime) * speedFactor);
	}

	public double getSimulTimeToRealTimeFactor()
	{
		return 1 / 1000d / speedFactor;
	}

	public void setSpeedFactor(double factor)
	{
		if (factor <= 0)
			throw new IllegalArgumentException("time factor can't be less than or equal to 0");
		if (!paused)
		{
			pause();
			unpause();
		}
		this.speedFactor = factor;
		callSimulTimeToRealTimeFactorChangedListeners(getSimulTimeToRealTimeFactor());
	}

	//@formatter:off
	public void    addSimulTimeToRealTimeFactorChangedListener(Consumer<Double> listener)
		{simulTimeToRealTimeFactorChangedListeners.add   (listener);}
	public void removeSimulTimeToRealTimeFactorChangedListener(Consumer<Double> listener)
		{simulTimeToRealTimeFactorChangedListeners.remove(listener);}
	private void  callSimulTimeToRealTimeFactorChangedListeners(double f)
		{simulTimeToRealTimeFactorChangedListeners.forEach(l -> l.accept(f));}
	//@formatter:on

	public boolean isPaused()
	{
		return paused;
	}

	public void toggle()
	{
		if (paused)
			unpause();
		else
			pause();
	}
}
