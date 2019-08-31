package net.mograsim.logic.model;

import java.util.function.Consumer;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;

public class SimpleLogicUIStandalone
{
	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel)
	{
		executeVisualisation(setupViewModel, (Consumer<VisualisationObjects>) null);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, Consumer<VisualisationObjects> beforeRun)
	{
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		executeVisualisation(setupViewModel, params, beforeRun);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, LogicModelParameters params)
	{
		executeVisualisation(setupViewModel, params, null);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, LogicModelParameters params,
			Consumer<VisualisationObjects> beforeRun)
	{
		// setup view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
		setupViewModel.accept(viewModel);

		// convert to logic model
		Timeline timeline = ViewLogicModelAdapter.convert(viewModel, params);

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