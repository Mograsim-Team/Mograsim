package net.mograsim.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.ui.LogicExecuter;
import net.mograsim.logic.ui.LogicUICanvas;
import net.mograsim.logic.ui.examples.RSLatchExample;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;

public class LogicUIPart
{
	@Inject
	private MPart part;

	@PostConstruct
	public void create(Composite parent)
	{
		// setup view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
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