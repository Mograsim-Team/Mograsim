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
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		executeVisualisation(setupViewModel, params);
	}

	public static void executeVisualisation(Consumer<ViewModelModifiable> setupViewModel, LogicModelParameters params)
	{
		// setup view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
		setupViewModel.accept(viewModel);

		// convert to logic model
		Timeline timeline = ViewLogicModelAdapter.convert(viewModel, params);

		// initialize UI and executer
		LogicUIStandaloneGUI ui = new LogicUIStandaloneGUI(viewModel);
		LogicExecuter exec = new LogicExecuter(timeline);

		// run it
		exec.startLiveExecution();
		ui.run();
		exec.stopLiveExecution();
	}

	private SimpleLogicUIStandalone()
	{
		throw new UnsupportedOperationException("No SimpleLogicUIStandalone instances");
	}
}