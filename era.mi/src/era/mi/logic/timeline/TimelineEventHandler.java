package era.mi.logic.timeline;

@FunctionalInterface
public interface TimelineEventHandler
{
	public void handle(TimelineEvent e);
}