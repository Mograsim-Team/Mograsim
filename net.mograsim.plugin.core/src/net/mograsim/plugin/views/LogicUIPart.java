package net.mograsim.plugin.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.plugin.ThemePreferences;
import net.mograsim.preferences.Preferences;

public class LogicUIPart extends ViewPart
{
	@Inject
	private MPart part;

	private LogicExecuter exec;
	private LogicUICanvas ui;

	@Override
	public void dispose()
	{
		if (exec != null)
			exec.stopLiveExecution();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		// set preferences
		Preferences.setPreferences(new ThemePreferences(PlatformUI.getWorkbench().getThemeManager().getCurrentTheme()));

		// setup view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
		createTestbench(viewModel);

		// convert to logic model
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		Timeline timeline = ViewLogicModelAdapter.convert(viewModel, params);

		// initialize UI
		ui = new LogicUICanvas(parent, SWT.NONE, viewModel);
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();

		// initialize executer
		exec = new LogicExecuter(timeline);

		// run it
		exec.startLiveExecution();
	}

	@Override
	public void setFocus()
	{
		ui.setFocus();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void createTestbench(ViewModelModifiable model)
	{
		GUIComponent comp = IndirectGUIComponentCreator.createComponent(model, "GUIAm2901");

		List<String> inputPinNames = new ArrayList<>();
		List<String> outputPinNames = new ArrayList<>();
		for (Pin p : comp.getPins().values())
			if (p.usage == PinUsage.INPUT)
				inputPinNames.add(p.name);
			else
				outputPinNames.add(p.name);

		inputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));
		outputPinNames.sort(Comparator.comparing(comp::getPin, Comparator.comparing(Pin::getRelY)));

		comp.moveTo(100, 0);
		for (int i = 0; i < inputPinNames.size(); i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model, 1);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPin(inputPinNames.get(i)), sw.getOutputPin());
		}
		for (int i = 0; i < outputPinNames.size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model, 1);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, comp.getPin(outputPinNames.get(i)), bd.getInputPin());
		}
	}
}