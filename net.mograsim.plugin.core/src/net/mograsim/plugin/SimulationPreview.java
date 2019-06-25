package net.mograsim.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemePreview;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.ui.LogicExecuter;
import net.mograsim.logic.ui.LogicUICanvas;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.GUINotGate;
import net.mograsim.logic.ui.model.components.GUIOrGate;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;

public class SimulationPreview implements IThemePreview
{

	private LogicUICanvas ui;
	private LogicExecuter exec;

	@Override
	@SuppressWarnings("unused")
	public void createControl(Composite parent, ITheme currentTheme)
	{
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
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();
		update(currentTheme);
		currentTheme.getColorRegistry().addListener(e -> update(currentTheme));

		ui.zoomSteps(12, 10, 10);
		exec.startLiveExecution();
	}

	private void update(ITheme currentTheme)
	{
		ui.setBackground(currentTheme.getColorRegistry().get("net.mograsim.plugin.sim_backgound"));
		ui.setForeground(currentTheme.getColorRegistry().get("net.mograsim.plugin.sim_text_color"));
	}

	@Override
	public void dispose()
	{
		exec.stopLiveExecution();
	}
}
