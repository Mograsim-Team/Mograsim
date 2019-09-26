package net.mograsim.logic.core.timeline;

import java.util.function.LongSupplier;

public class PauseableTimeFunction implements LongSupplier
{
	private boolean paused = false;
	private long unpausedSysTime = 0, lastPausedInternalTime = 0;
	private double speedFactor = 1;

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

	public long simulTimeDeltaToRealTimeMillis(long simulTime)
	{
		return paused ? -1 : (long) (simulTime / speedFactor / 1000);
	}

	public void setSpeedFactor(double factor)
	{
		if (factor <= 0)
			throw new IllegalArgumentException("time factor can't be smaller than 1");
		if (!paused)
		{
			pause();
			unpause();
		}
		this.speedFactor = factor;
	}

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
