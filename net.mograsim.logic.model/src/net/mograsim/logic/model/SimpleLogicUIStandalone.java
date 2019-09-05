package net.mograsim.logic.model;

import java.util.function.Consumer;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;

public class SimpleLogicUIStandalone
{
	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel)
	{
		executeVisualisation(setupViewModel, (Consumer<VisualisationObjects>) null);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, Consumer<VisualisationObjects> beforeRun)
	{
		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		executeVisualisation(setupViewModel, params, beforeRun);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, CoreModelParameters params)
	{
		executeVisualisation(setupViewModel, params, null);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, CoreModelParameters params,
			Consumer<VisualisationObjects> beforeRun)
	{
		// setup view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
		setupViewModel.accept(viewModel);

		// convert to core model
		Timeline timeline = LogicCoreAdapter.convert(viewModel, params);

		// initialize UI and executer
		LogicUIStandaloneGUI ui = new LogicUIStandaloneGUI(viewModel);
		LogicExecuter exec = new LogicExecuter(timeline);

		if (beforeRun != null)
			beforeRun.accept(new VisualisationObjects(viewModel, timeline, ui, exec));

		// run it
		exec.startLiveExecution();
		ui.run();
		exec.stopLiveExecution();
	}

	public static class VisualisationObjects
	{
		public final ViewModelModifiable model;
		public final Timeline timeline;
		public final LogicUIStandaloneGUI gui;
		public final LogicExecuter executer;

		public VisualisationObjects(ViewModelModifiable model, Timeline timeline, LogicUIStandaloneGUI gui, LogicExecuter executer)
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