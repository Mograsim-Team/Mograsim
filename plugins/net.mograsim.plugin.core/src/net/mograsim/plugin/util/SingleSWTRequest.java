package net.mograsim.plugin.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

/**
 * A utility class that requests the asynchronous execution of a Runnable in the SWT Thread. Making a new request before the old one is
 * processed will override the old request.
 */
public class SingleSWTRequest
{
	private AtomicBoolean waiting = new AtomicBoolean();
	private Runnable request;

	public void request(Runnable request)
	{
		synchronized (waiting)
		{
			this.request = request;
			if (!waiting.get())
			{
				waiting.set(true);
				Display.getDefault().asyncExec(() ->
				{
					synchronized (waiting)
					{
						waiting.set(false);
						this.request.run();
					}
				});
			}
		}
	}
}
