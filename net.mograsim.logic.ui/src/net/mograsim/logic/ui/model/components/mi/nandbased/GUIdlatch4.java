package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdlatch4 extends SubmodelComponent
{
	private final Pin pinD1;
	private final Pin pinD2;
	private final Pin pinD3;
	private final Pin pinD4;
	private final Pin pinC;
	private final Pin pinQ1;
	private final Pin pinQ2;
	private final Pin pinQ3;
	private final Pin pinQ4;

	public GUIdlatch4(ViewModelModifiable model)
	{
		super(model, "GUIdlatch4");
		setSize(35, 55);
		setSubmodelScale(.4);

		Pin D1 = addSubmodelInterface(1, 0, 5);
		Pin D2 = addSubmodelInterface(1, 0, 15);
		Pin D3 = addSubmodelInterface(1, 0, 25);
		Pin D4 = addSubmodelInterface(1, 0, 35);
		Pin C = addSubmodelInterface(1, 0, 50);
		Pin Q1 = addSubmodelInterface(1, 35, 5);
		Pin Q2 = addSubmodelInterface(1, 35, 15);
		Pin Q3 = addSubmodelInterface(1, 35, 25);
		Pin Q4 = addSubmodelInterface(1, 35, 35);

		this.pinC = getSupermodelPin(C);
		this.pinD1 = getSupermodelPin(D1);
		this.pinD2 = getSupermodelPin(D2);
		this.pinD3 = getSupermodelPin(D3);
		this.pinD4 = getSupermodelPin(D4);
		this.pinQ1 = getSupermodelPin(Q1);
		this.pinQ2 = getSupermodelPin(Q2);
		this.pinQ3 = getSupermodelPin(Q3);
		this.pinQ4 = getSupermodelPin(Q4);

		initSubmodelComponents(C, D1, D2, D3, D4, Q1, Q2, Q3, Q4);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents(Pin C, Pin D1, Pin D2, Pin D3, Pin D4, Pin Q1, Pin Q2, Pin Q3, Pin Q4)
	{
		GUIdlatch dlatch1 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch2 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch3 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch4 = new GUIdlatch(submodelModifiable);

		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp4 = new WireCrossPoint(submodelModifiable, 1);

		dlatch1.moveTo(30, 7.5);
		dlatch2.moveTo(30, 32.5);
		dlatch3.moveTo(30, 57.5);
		dlatch4.moveTo(30, 82.5);
		cp2.moveTo(15, 52.5);
		cp3.moveTo(15, 77.5);
		cp4.moveTo(15, 102.5);

		new GUIWire(submodelModifiable, C, cp4.getPin(), new Point(15, 125));
		new GUIWire(submodelModifiable, cp4.getPin(), dlatch4.getPinE());
		new GUIWire(submodelModifiable, cp4.getPin(), cp3.getPin());
		new GUIWire(submodelModifiable, cp3.getPin(), dlatch3.getPinE());
		new GUIWire(submodelModifiable, cp3.getPin(), cp2.getPin());
		new GUIWire(submodelModifiable, cp2.getPin(), dlatch2.getPinE());
		new GUIWire(submodelModifiable, cp2.getPin(), dlatch1.getPinE(), new Point(15, 27.5));
		new GUIWire(submodelModifiable, D1, dlatch1.getPinD());
		new GUIWire(submodelModifiable, D2, dlatch2.getPinD());
		new GUIWire(submodelModifiable, D3, dlatch3.getPinD());
		new GUIWire(submodelModifiable, D4, dlatch4.getPinD());
		new GUIWire(submodelModifiable, dlatch1.getPinQ(), Q1);
		new GUIWire(submodelModifiable, dlatch2.getPinQ(), Q2);
		new GUIWire(submodelModifiable, dlatch3.getPinQ(), Q3);
		new GUIWire(submodelModifiable, dlatch4.getPinQ(), Q4);
	}

	public Pin getPinC()
	{
		return pinC;
	}

	public Pin getPinD1()
	{
		return pinD1;
	}

	public Pin getPinD2()
	{
		return pinD2;
	}

	public Pin getPinD3()
	{
		return pinD3;
	}

	public Pin getPinD4()
	{
		return pinD4;
	}

	public Pin getPinQ1()
	{
		return pinQ1;
	}

	public Pin getPinQ2()
	{
		return pinQ2;
	}

	public Pin getPinQ3()
	{
		return pinQ3;
	}

	public Pin getPinQ4()
	{
		return pinQ4;
	}
}