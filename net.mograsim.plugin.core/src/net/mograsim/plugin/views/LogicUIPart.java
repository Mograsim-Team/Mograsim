package net.mograsim.plugin.views;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;
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

		Optional<MachineDefinition> mdo = MachineRegistry.getinstalledMachines().stream().findFirst();

		Am2900Loader.setup();
		ViewModelModifiable viewModelModifiable = new ViewModelModifiable();
		IndirectGUIComponentCreator.createComponent(viewModelModifiable, "resource:Am2900Loader:/components/GUIAm2900.json");
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
//		timeline = ;

		// initialize UI
		ui = new LogicUICanvas(parent, SWT.NONE, viewModelModifiable);
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();

		// initialize executer
		exec = new LogicExecuter(ViewLogicModelAdapter.convert(viewModelModifiable, params));

		// run it
		exec.startLiveExecution();
	}

	@Override
	public void setFocus()
	{
		ui.setFocus();
	}
}