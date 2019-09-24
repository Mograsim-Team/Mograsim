package net.mograsim.plugin.nature;

@FunctionalInterface
public interface ProjectContextListener
{
	void onProjectContextChange(ProjectContextEvent projectContextEvent);
}