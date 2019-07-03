package net.mograsim.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemePreview;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.ui.LogicExecuter;
import net.mograsim.logic.ui.LogicUICanvas;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.atomic.GUINotGate;
import net.mograsim.logic.ui.model.components.atomic.GUIOrGate;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.preferences.Preferences;

public class SimulationPreview implements IThemePreview
{
	private LogicUICanvas ui;
	private LogicExecuter exec;
	private Preferences oldPreferences;
	private Preferences currentThemePreferences;

	@Override
	@SuppressWarnings("unused")
	public void createControl(Composite parent, ITheme currentTheme)
	{
		oldPreferences = Preferences.current();

		currentThemePreferences = new ThemePreferences(currentTheme);
		// TODO this will change the global preferences; so if another LogicUICanvas redraws, it will use the "new" colors too.
		Preferences.setPreferences(currentThemePreferences);

		ViewModelModifiable model = new ViewModelModifiable();
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;

		GUIManualSwitch rIn = new GUIManualSwitch(model);
		rIn.moveTo(10, 10);
		GUIManualSwitch sIn = new GUIManualSwitch(model);
		sIn.moveTo(10, 70);

		GUIOrGate or1 = new GUIOrGate(model, 1);
		or1.moveTo(70, 12.5);
		new GUIWire(model, rIn.getOutputPin(), or1.getPin("A"));

		GUIOrGate or2 = new GUIOrGate(model, 1);
		or2.moveTo(70, 62.5);
		new GUIWire(model, sIn.getOutputPin(), or2.getPin("B"));

		GUINotGate not1 = new GUINotGate(model, 1);
		not1.moveTo(110, 17.5);
		new GUIWire(model, or1.getPin("Y"), not1.getPin("A"));

		GUINotGate not2 = new GUINotGate(model, 1);
		not2.moveTo(110, 67.5);
		new GUIWire(model, or2.getPin("Y"), not2.getPin("A"));

		WireCrossPoint p1 = new WireCrossPoint(model, 1);
		p1.moveCenterTo(140, 22.5);
		new GUIWire(model, not1.getPin("Y"), p1);
		new GUIWire(model, p1, or2.getPin("A"), new Point(140, 35), new Point(50, 60), new Point(50, 67.5));

		WireCrossPoint p2 = new WireCrossPoint(model, 1);
		p2.moveCenterTo(140, 72.5);
		new GUIWire(model, not2.getPin("Y"), p2);
		new GUIWire(model, p2, or1.getPin("B"), new Point(140, 60), new Point(50, 35), new Point(50, 27.5));

		WireCrossPoint o1 = new WireCrossPoint(model, 1);
		o1.moveCenterTo(150, 22.5);
		new GUIWire(model, p1, o1);

		WireCrossPoint o2 = new WireCrossPoint(model, 1);
		o2.moveCenterTo(150, 72.5);
		new GUIWire(model, p2, o2);

		Timeline t = ViewLogicModelAdapter.convert(model, params);
		exec = new LogicExecuter(t);

		rIn.clicked(0, 0);

		ui = new LogicUICanvas(parent, SWT.NONE, model);

		ui.zoom(3.5, 10, 10);
		exec.startLiveExecution();

		currentTheme.addPropertyChangeListener(e -> ui.redraw());
	}

	@Override
	public void dispose()
	{
		exec.stopLiveExecution();
		if (Preferences.current() == currentThemePreferences)
			Preferences.setPreferences(oldPreferences);
	}
}
