package net.mograsim.logic.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import net.mograsim.logic.core.timeline.PauseableTimeFunction;
import net.mograsim.logic.core.timeline.Timeline;

//TODO maybe move to logic core?
public class LogicExecuter
{
	// TODO replace with CoreModel when it exists
	private final Timeline timeline;

	private final AtomicBoolean shouldBeRunningLive;
	private final AtomicBoolean isRunningLive;
	private final AtomicBoolean isPaused;
	private final AtomicLong nextExecSimulTime;
	private final Thread simulationThread;

	PauseableTimeFunction tf;

	public LogicExecuter(Timeline timeline)
	{
		this.timeline = timeline;

		tf = new PauseableTimeFunction();
		timeline.setTimeFunction(tf);
		shouldBeRunningLive = new AtomicBoolean();
		isRunningLive = new AtomicBoolean();
		isPaused = new AtomicBoolean();
		nextExecSimulTime = new AtomicLong();
		simulationThread = new Thread(() ->
		{
			isRunningLive.set(true);
			synchronized (isRunningLive)
			{
				isRunningLive.notify();
			}
			try
			{
				while (shouldBeRunningLive.get())
				{
					// always execute to keep timeline from "hanging behind" for too long
					long current = tf.getAsLong();
					timeline.executeUntil(timeline.laterThan(current), System.currentTimeMillis() + 10);
					long nextEventTime = timeline.nextEventTime();
					long sleepTime;
					if (timeline.hasNext())
						sleepTime = tf.simulTimeDeltaToRealTimeMillis(nextEventTime - current);
					else
						sleepTime = 10000;
					try
					{
						nextExecSimulTime.set(nextEventTime);
						if (sleepTime > 0)
							Thread.sleep(sleepTime);

						synchronized (isPaused)
						{
							while (isPaused.get())
								isPaused.wait();
						}
					}
					catch (@SuppressWarnings("unused") InterruptedException e)
					{// do nothing; it is normal execution flow to be interrupted
					}
				}
			}
			finally
			{
				isRunningLive.set(false);
				synchronized (isRunningLive)
				{
					isRunningLive.notify();
				}
			}
		});
		timeline.addEventAddedListener(event ->
		{
			if (isRunningLive.get())
				if (Timeline.timeCmp(event.getTiming(), nextExecSimulTime.get()) < 0)
					simulationThread.interrupt();
		});
	}

	public void executeNextStep()
	{
		timeline.executeNext();
	}

	public synchronized void startLiveExecution()
	{
		if (shouldBeRunningLive.get())
			return;
		shouldBeRunningLive.set(true);
		simulationThread.start();
		waitForIsRunning(true);
	}

	public synchronized void stopLiveExecution()
	{
		if (!shouldBeRunningLive.get())
			return;
		shouldBeRunningLive.set(false);
		simulationThread.interrupt();
		waitForIsRunning(false);
	}

	public void unpauseLiveExecution()
	{
		synchronized (isPaused)
		{
			tf.unpause();
			isPaused.set(false);
			isPaused.notify();
		}
	}

	public void pauseLiveExecution()
	{
		synchronized (isPaused)
		{
			tf.pause();
			isPaused.set(true);
		}
	}

	public boolean isPaused()
	{
		return isPaused.get();
	}

	public void setSpeedFactor(double factor)
	{
		tf.setSpeedFactor(factor);
	}

	private void waitForIsRunning(boolean expectedState)
	{
		while (isRunningLive.get() ^ expectedState)
			try
			{
				synchronized (isRunningLive)
				{
					isRunningLive.wait();
				}
			}
			catch (@SuppressWarnings("unused") InterruptedException e)
			{// no need to do anything
			}
	}
}