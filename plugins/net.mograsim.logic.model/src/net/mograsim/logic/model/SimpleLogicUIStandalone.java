package net.mograsim.logic.model;

import java.util.function.Consumer;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;

public class SimpleLogicUIStandalone
{
	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel)
	{
		executeVisualisation(setupLogicModel, (Consumer<VisualisationObjects>) null);
	}

	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel, Consumer<VisualisationObjects> beforeRun)
	{
		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		executeVisualisation(setupLogicModel, params, beforeRun);
	}

	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel, CoreModelParameters params)
	{
		executeVisualisation(setupLogicModel, params, null);
	}

	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel, CoreModelParameters params,
			Consumer<VisualisationObjects> beforeRun)
	{
		// setup logic model
		LogicModelModifiable logicModel = new LogicModelModifiable();
		setupLogicModel.accept(logicModel);

		// convert to core model
		Timeline timeline = LogicCoreAdapter.convert(logicModel, params);

		// initialize UI and executer
		LogicUIStandaloneGUI ui = new LogicUIStandaloneGUI(logicModel);
		LogicExecuter exec = new LogicExecuter(timeline);

		if (beforeRun != null)
			beforeRun.accept(new VisualisationObjects(logicModel, timeline, ui, exec));

		// run it
		exec.startLiveExecution();
		ui.run();
		exec.stopLiveExecution();
	}

	public static class VisualisationObjects
	{
		public final LogicModelModifiable model;
		public final Timeline timeline;
		public final LogicUIStandaloneGUI gui;
		public final LogicExecuter executer;

		public VisualisationObjects(LogicModelModifiable model, Timeline timeline, LogicUIStandaloneGUI gui, LogicExecuter executer)
		{
			this.model = model;
			this.timeline = timeline;
			this.gui = gui;
			this.executer = executer;
		}
	}

	private SimpleLogicUIStandalone()
	{
		throw new UnsupportedOperationException("No SimpleLogicUIStandalone instances");
	}
}