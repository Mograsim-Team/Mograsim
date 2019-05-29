package mograsim.logic.ui;

import java.util.function.Consumer;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.ui.model.ViewModel;
import mograsim.logic.ui.modeladapter.LogicModelParameters;
import mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;

public class SimpleLogicUIStandalone
{
	public static void executeVisualisation(Consumer<ViewModel> setupViewModel)
	{
		// setup view model
		ViewModel viewModel = new ViewModel();
		setupViewModel.accept(viewModel);

		// convert to logic model
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
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