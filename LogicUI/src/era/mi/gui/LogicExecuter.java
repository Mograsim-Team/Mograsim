package era.mi.gui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import era.mi.logic.timeline.Timeline;

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
				catch (InterruptedException e)
				{// do nothing; it is normal execution flow to be interrupted
				}
			}
			isRunningLive.set(false);
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
		while (!isRunningLive.get())
			;
	}

	public synchronized void stopLiveExecution()
	{
		if (!shouldBeRunningLive.get())
			return;
		shouldBeRunningLive.set(false);
		simulationThread.interrupt();
		while (isRunningLive.get())
			;
	}
}