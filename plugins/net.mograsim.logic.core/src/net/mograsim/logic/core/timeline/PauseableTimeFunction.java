package net.mograsim.logic.core.timeline;

import java.util.function.LongSupplier;

public class PauseableTimeFunction implements LongSupplier
{
	private boolean paused = false;
	private long unpausedSysTime = 0, lastPausedInternalTime = 0;
	private int speedPercentage = 100;

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
			unpausedSysTime = System.currentTimeMillis();
		}
	}

	@Override
	public long getAsLong()
	{
		return paused ? lastPausedInternalTime
				: lastPausedInternalTime + ((System.currentTimeMillis() - unpausedSysTime) * speedPercentage) / 100;
	}

	public void setSpeedPercentage(int percentage)
	{
		if (!paused)
		{
			pause();
			unpause();
		}
		this.speedPercentage = Integer.min(100, Integer.max(percentage, 1));
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
