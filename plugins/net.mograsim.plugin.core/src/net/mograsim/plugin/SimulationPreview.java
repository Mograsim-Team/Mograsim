package net.mograsim.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemePreview;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.atomic.ModelNotGate;
import net.mograsim.logic.model.model.components.atomic.ModelOrGate;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.CoreModelParameters.CoreModelParametersBuilder;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.plugin.preferences.EclipseRenderPreferences;

public class SimulationPreview implements IThemePreview
{
	private LogicUICanvas ui;
	private LogicExecuter exec;

	@Override
	@SuppressWarnings("unused")
	public void createControl(Composite parent, ITheme currentTheme)
	{
		RenderPreferences currentThemeRenderPrefs = new EclipseRenderPreferences(currentTheme,
				MograsimActivator.instance().getPreferenceStore());

		LogicModelModifiable model = new LogicModelModifiable();

		CoreModelParametersBuilder paramsBuilder = CoreModelParameters.builder();
		paramsBuilder.gateProcessTime = 50;
		paramsBuilder.hardcodedComponentProcessTime = paramsBuilder.gateProcessTime * 5;
		paramsBuilder.wireTravelTime = 10;
		CoreModelParameters params = paramsBuilder.build();

		ModelManualSwitch rIn = new ModelManualSwitch(model, 1);
		rIn.moveTo(10, 10);
		ModelManualSwitch sIn = new ModelManualSwitch(model, 1);
		sIn.moveTo(10, 70);

		ModelOrGate or1 = new ModelOrGate(model, 1);
		or1.moveTo(70, 12.5);
		new ModelWire(model, rIn.getOutputPin(), or1.getPin("A"));

		ModelOrGate or2 = new ModelOrGate(model, 1);
		or2.moveTo(70, 62.5);
		new ModelWire(model, sIn.getOutputPin(), or2.getPin("B"));

		ModelNotGate not1 = new ModelNotGate(model, 1);
		not1.moveTo(110, 17.5);
		new ModelWire(model, or1.getPin("Y"), not1.getPin("A"));

		ModelNotGate not2 = new ModelNotGate(model, 1);
		not2.moveTo(110, 67.5);
		new ModelWire(model, or2.getPin("Y"), not2.getPin("A"));

		ModelWireCrossPoint p1 = new ModelWireCrossPoint(model, 1);
		p1.moveCenterTo(140, 22.5);
		new ModelWire(model, not1.getPin("Y"), p1);
		new ModelWire(model, p1, or2.getPin("A"), new Point(140, 35), new Point(50, 60), new Point(50, 67.5));

		ModelWireCrossPoint p2 = new ModelWireCrossPoint(model, 1);
		p2.moveCenterTo(140, 72.5);
		new ModelWire(model, not2.getPin("Y"), p2);
		new ModelWire(model, p2, or1.getPin("B"), new Point(140, 60), new Point(50, 35), new Point(50, 27.5));

		ModelWireCrossPoint o1 = new ModelWireCrossPoint(model, 1);
		o1.moveCenterTo(150, 22.5);
		new ModelWire(model, p1, o1);

		ModelWireCrossPoint o2 = new ModelWireCrossPoint(model, 1);
		o2.moveCenterTo(150, 72.5);
		new ModelWire(model, p2, o2);

		Timeline t = LogicCoreAdapter.convert(model, params);
		exec = new LogicExecuter(t);

		rIn.clicked(0, 0);

		ui = new LogicUICanvas(parent, SWT.NONE, model, currentThemeRenderPrefs);

		ui.zoom(3.5, 10, 10);
		exec.startLiveExecution();

		currentTheme.addPropertyChangeListener(e -> ui.redraw());
	}

	@Override
	public void dispose()
	{
		exec.stopLiveExecution();
	}
}
