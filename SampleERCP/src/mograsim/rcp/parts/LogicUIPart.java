package mograsim.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.ui.LogicExecuter;
import mograsim.logic.ui.LogicUICanvas;
import mograsim.logic.ui.examples.RSLatchExample;
import mograsim.logic.ui.model.ViewModel;
import mograsim.logic.ui.modeladapter.LogicModelParameters;
import mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;

public class LogicUIPart
{
	@Inject
	private MPart part;

	@PostConstruct
	public void create(Composite parent)
	{
		// setup view model
		ViewModel viewModel = new ViewModel();
		RSLatchExample.createRSLatchExample(viewModel);

		// convert to logic model
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		Timeline timeline = ViewLogicModelAdapter.convert(viewModel, params);

		// initialize UI
		LogicUICanvas ui = new LogicUICanvas(parent, SWT.NONE, viewModel);
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();

		// initialize executer
		LogicExecuter exec = new LogicExecuter(timeline);

		// run it
		exec.startLiveExecution();

		// TODO find a better condition when to stop
		ui.addDisposeListener(e -> exec.stopLiveExecution());
	}
}