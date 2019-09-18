package net.mograsim.plugin.views;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.machine.Machine;
import net.mograsim.plugin.EclipsePreferences;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.MograsimActivator;
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
		Preferences.setPreferences(new EclipsePreferences(PlatformUI.getWorkbench().getThemeManager().getCurrentTheme(),
				MograsimActivator.instance().getPreferenceStore()));

		Machine m = MachineContext.getInstance().getMachine();

		// initialize UI
		ui = new LogicUICanvas(parent, SWT.NONE, m.getModel());
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();

		// initialize executer
		exec = new LogicExecuter(m.getTimeline());

		// run it
		exec.startLiveExecution();
	}

	@Override
	public void setFocus()
	{
		ui.setFocus();
	}
}