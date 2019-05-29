package mograsim.logic.core.timeline;

@FunctionalInterface
public interface TimelineEventHandler
{
	public void handle(TimelineEvent e);
}