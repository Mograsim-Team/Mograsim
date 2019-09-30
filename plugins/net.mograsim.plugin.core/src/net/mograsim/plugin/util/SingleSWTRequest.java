package net.mograsim.plugin.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

public class SingleSWTRequest
{
	private AtomicBoolean waiting = new AtomicBoolean();

	public void request(Runnable request)
	{
		synchronized (waiting)
		{
			if (!waiting.get())
			{
				waiting.set(true);
				Display.getDefault().asyncExec(() ->
				{
					synchronized (waiting)
					{
						waiting.set(false);
						request.run();
					}
				});
			}
		}
	}
}
