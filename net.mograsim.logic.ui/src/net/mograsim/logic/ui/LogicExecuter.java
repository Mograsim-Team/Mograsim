package net.mograsim.logic.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import net.mograsim.logic.core.timeline.Timeline;

//TODO maybe move to logic core?
public class LogicExecuter
{
	// TODO replace with LogicModel when it exists
	private final Timeline timeline;

	private final AtomicBoolean shouldBeRunningLive;
	private final AtomicBoolean isRunningLive;
	private final AtomicLong nextExecSimulTime;
	private final Thread simulationThread;

	public LogicExecuter(Timeline timeline)
	{
		this.timeline = timeline;

		timeline.setTimeFunction(System::currentTimeMillis);
		shouldBeRunningLive = new AtomicBoolean();
		isRunningLive = new AtomicBoolean();
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
					long current = System.currentTimeMillis();
					timeline.executeUntil(timeline.laterThan(current), current + 10);
					long sleepTime;
					if (timeline.hasNext())
						sleepTime = timeline.nextEventTime() - current;
					else
						sleepTime = 10000;
					try
					{
						nextExecSimulTime.set(current + sleepTime);
						if (sleepTime > 0)
							Thread.sleep(sleepTime);
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