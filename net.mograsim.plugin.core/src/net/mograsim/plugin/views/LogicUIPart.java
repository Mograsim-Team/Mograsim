package net.mograsim.plugin.views;

import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.ui.LogicExecuter;
import net.mograsim.logic.ui.LogicUICanvas;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.am2901.GUIAm2901;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.preferences.ColorDefinition;
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
		Preferences.setPreferences(new Preferences()
		{
			@Override
			public ColorDefinition getColorDefinition(String name)
			{
				RGB rgb = getColorRegistry().getRGB(name);
				if (rgb == null)
				{
					StatusManager.getManager().handle(new Status(IStatus.ERROR, "net.mograsim.plugin.core", "No color for name " + name));
					return null;
				}
				return new ColorDefinition(rgb.red, rgb.green, rgb.blue);
			}

			@Override
			public Color getColor(String name)
			{
				return getColorRegistry().get(name);
			}

			private ColorRegistry getColorRegistry()
			{
				return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
			}
		});

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
		SimpleRectangularSubmodelComponent comp = new GUIAm2901(model);

		comp.moveTo(100, 0);
		for (int i = 0; i < comp.getInputPinNames().size(); i++)
		{
			GUIManualSwitch sw = new GUIManualSwitch(model);
			sw.moveTo(0, 20 * i);
			new GUIWire(model, comp.getPin(comp.getInputPinNames().get(i)), sw.getOutputPin());
		}
		for (int i = 0; i < comp.getOutputPinNames().size(); i++)
		{
			GUIBitDisplay bd = new GUIBitDisplay(model);
			bd.moveTo(200, 20 * i);
			new GUIWire(model, comp.getPin(comp.getOutputPinNames().get(i)), bd.getInputPin());
		}
	}
}