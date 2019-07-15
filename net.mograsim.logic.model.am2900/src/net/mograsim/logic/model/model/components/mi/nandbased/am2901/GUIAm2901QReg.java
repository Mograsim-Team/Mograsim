package net.mograsim.logic.model.model.components.mi.nandbased.am2901;

import java.util.Arrays;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdff;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.BitVectorSplittingAtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.DelegatingAtomicHighLevelStateHandler;

public class GUIAm2901QReg extends SimpleRectangularSubmodelComponent
{
	private StandardHighLevelStateHandler highLevelStateHandler;

	public GUIAm2901QReg(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIAm2901QReg(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIAm2901QReg", name);
		setSubmodelScale(.4);
		setInputPins("C", "WE", "D1", "D2", "D3", "D4");
		setOutputPins("Q1", "Q2", "Q3", "Q4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin C = getSubmodelPin("C");
		Pin WE = getSubmodelPin("WE");
		Pin D1 = getSubmodelPin("D1");
		Pin D2 = getSubmodelPin("D2");
		Pin D3 = getSubmodelPin("D3");
		Pin D4 = getSubmodelPin("D4");
		Pin Q1 = getSubmodelPin("Q1");
		Pin Q2 = getSubmodelPin("Q2");
		Pin Q3 = getSubmodelPin("Q3");
		Pin Q4 = getSubmodelPin("Q4");

		GUIand and = new GUIand(submodelModifiable);
		GUIdff dff1 = new GUIdff(submodelModifiable);
		GUIdff dff2 = new GUIdff(submodelModifiable);
		GUIdff dff3 = new GUIdff(submodelModifiable);
		GUIdff dff4 = new GUIdff(submodelModifiable);

		WireCrossPoint cpC1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC3 = new WireCrossPoint(submodelModifiable, 1);

		and.moveTo(5, 15);
		dff1.moveTo(50, 7.5);
		dff2.moveTo(50, 32.5);
		dff3.moveTo(50, 57.5);
		dff4.moveTo(50, 82.5);
		cpC1.moveCenterTo(42.5, 20);
		cpC2.moveCenterTo(42.5, 37.5);
		cpC3.moveCenterTo(42.5, 62.5);

		new GUIWire(submodelModifiable, C, and.getPin("A"));
		new GUIWire(submodelModifiable, WE, and.getPin("B"));
		new GUIWire(submodelModifiable, and.getPin("Y"), cpC1, new Point[0]);
		new GUIWire(submodelModifiable, cpC1, dff1.getPin("C"), new Point(42.5, 12.5));
		new GUIWire(submodelModifiable, cpC1, cpC2, new Point[0]);
		new GUIWire(submodelModifiable, cpC2, dff2.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, cpC2, cpC3, new Point[0]);
		new GUIWire(submodelModifiable, cpC3, dff3.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, cpC3, dff4.getPin("C"), new Point(42.5, 87.5));
		new GUIWire(submodelModifiable, D1, dff1.getPin("D"), new Point(17.5, 62.5), new Point(17.5, 42.5), new Point(45, 42.5),
				new Point(45, 22.5));
		new GUIWire(submodelModifiable, D2, dff2.getPin("D"), new Point(22.5, 87.5), new Point(22.5, 47.5));
		new GUIWire(submodelModifiable, D3, dff3.getPin("D"), new Point(27.5, 112.5), new Point(27.5, 72.5));
		new GUIWire(submodelModifiable, D4, dff4.getPin("D"), new Point(32.5, 137.5), new Point(32.5, 97.5));
		new GUIWire(submodelModifiable, dff1.getPin("Q"), Q1, new Point[0]);
		new GUIWire(submodelModifiable, dff2.getPin("Q"), Q2, new Point[0]);
		new GUIWire(submodelModifiable, dff3.getPin("Q"), Q3, new Point[0]);
		new GUIWire(submodelModifiable, dff4.getPin("Q"), Q4, new Point[0]);

		this.highLevelStateHandler = new StandardHighLevelStateHandler(this);
		highLevelStateHandler.addAtomicHighLevelState("q1", DelegatingAtomicHighLevelStateHandler::new).set(dff1, "q");
		highLevelStateHandler.addAtomicHighLevelState("q2", DelegatingAtomicHighLevelStateHandler::new).set(dff2, "q");
		highLevelStateHandler.addAtomicHighLevelState("q3", DelegatingAtomicHighLevelStateHandler::new).set(dff3, "q");
		highLevelStateHandler.addAtomicHighLevelState("q4", DelegatingAtomicHighLevelStateHandler::new).set(dff4, "q");
		highLevelStateHandler.addAtomicHighLevelState("q", BitVectorSplittingAtomicHighLevelStateHandler::new)
				.set(Arrays.asList("q1", "q2", "q3", "q4"), Arrays.asList(1, 1, 1, 1));
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		return highLevelStateHandler.getHighLevelState(stateID);
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		highLevelStateHandler.setHighLevelState(stateID, newState);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901QReg.class.getCanonicalName(), (m, p, n) -> new GUIAm2901QReg(m, n));
	}
}