package net.mograsim.logic.model;

import java.util.function.Consumer;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.CoreModelParameters.CoreModelParametersBuilder;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.preferences.DefaultRenderPreferences;

public class SimpleLogicUIStandalone
{
	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel)
	{
		executeVisualisation(setupLogicModel, (Consumer<VisualisationObjects>) null);
	}

	public static void executeVisualisation(Consumer<LogicModelModifiable> setupLogicModel, Consumer<VisualisationObjects> beforeRun)
	{
		CoreModelParametersBuilder paramsBuilder = new CoreModelParametersBuilder();
		paramsBuilder.gateProcessTime = 50;
		paramsBuilder.hardcodedComponentProcessTime = paramsBuilder.gateProcessTime * 5;
		paramsBuilder.wireTravelTime = 10;
		executeVisualisation(setupLogicModel, paramsBuilder.build(), beforeRun);
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
		LogicUIStandaloneGUI ui = new LogicUIStandaloneGUI(logicModel, new DefaultRenderPreferences());
		LogicExecuter exec = new LogicExecuter(timeline);

		if (beforeRun != null)
			beforeRun.accept(new VisualisationObjects(logicModel, timeline, ui, exec));

		// run it
		exec.setSpeedFactor(1);
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