package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUInand3;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIAm2901ALUFuncDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUFuncDecode(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIAm2901ALUFuncDecode(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIAm2901ALUFuncDecode", name);
		setSubmodelScale(.25);
		setInputPins("I5", "I4", "I3");
		setOutputPins("CinE", "L", "SN", "SBE", "FN", "RN");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I5 = getSubmodelPin("I5");
		Pin I4 = getSubmodelPin("I4");
		Pin I3 = getSubmodelPin("I3");
		Pin CinE = getSubmodelPin("CinE");
		Pin L = getSubmodelPin("L");
		Pin SN = getSubmodelPin("SN");
		Pin SBE = getSubmodelPin("SBE");
		Pin FN = getSubmodelPin("FN");
		Pin RN = getSubmodelPin("RN");

		GUINandGate notI5 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI4 = new GUINandGate(submodelModifiable, 1);
		GUInand3 nandI4I3NotI5 = new GUInand3(submodelModifiable);
		GUINandGate nandI5NotI4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI3I4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandL = new GUINandGate(submodelModifiable, 1);
		GUIand andSBE = new GUIand(submodelModifiable);

		WireCrossPoint cpI51 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI52 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI53 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI41 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI42 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI43 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI44 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI31 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI32 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI51 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI52 = new WireCrossPoint(submodelModifiable, 1);

		notI5.moveTo(15, 10);
		notI4.moveTo(15, 50);
		nandI4I3NotI5.moveTo(55, 10);
		nandI5NotI4.moveTo(55, 45);
		nandI3I4.moveTo(55, 70);
		nandL.moveTo(100, 50);
		andSBE.moveTo(100, 135);
		cpI51.moveCenterTo(5, 20);
		cpI52.moveCenterTo(5, 25);
		cpI53.moveCenterTo(5, 45);
		cpI41.moveCenterTo(10, 60);
		cpI42.moveCenterTo(10, 55);
		cpI43.moveCenterTo(10, 65);
		cpI44.moveCenterTo(10, 85);
		cpI31.moveCenterTo(50, 100);
		cpI32.moveCenterTo(50, 75);
		cpNotI51.moveCenterTo(40, 20);
		cpNotI52.moveCenterTo(40, 35);

		new GUIWire(submodelModifiable, I5, cpI51, new Point[0]);
		new GUIWire(submodelModifiable, cpI51, notI5.getPin("A"), new Point(5, 15));
		new GUIWire(submodelModifiable, cpI51, cpI52, new Point[0]);
		new GUIWire(submodelModifiable, cpI52, notI5.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI52, cpI53, new Point[0]);
		new GUIWire(submodelModifiable, cpI53, nandI5NotI4.getPin("A"), new Point(45, 45), new Point(45, 50));
		new GUIWire(submodelModifiable, cpI53, FN, new Point(5, 180));
		new GUIWire(submodelModifiable, I4, cpI41, new Point[0]);
		new GUIWire(submodelModifiable, cpI41, cpI42, new Point[0]);
		new GUIWire(submodelModifiable, cpI42, nandI4I3NotI5.getPin("A"), new Point(10, 40), new Point(45, 40), new Point(45, 15));
		new GUIWire(submodelModifiable, cpI42, notI4.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cpI41, cpI43, new Point[0]);
		new GUIWire(submodelModifiable, cpI43, notI4.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI43, cpI44, new Point[0]);
		new GUIWire(submodelModifiable, cpI44, nandI3I4.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpI44, SN, new Point(10, 105), new Point(135, 105), new Point(135, 100));
		new GUIWire(submodelModifiable, I3, cpI31, new Point(50, 100));
		new GUIWire(submodelModifiable, cpI31, RN, new Point(50, 220));
		new GUIWire(submodelModifiable, cpI31, cpI32, new Point[0]);
		new GUIWire(submodelModifiable, cpI32, nandI4I3NotI5.getPin("B"), new Point(50, 25));
		new GUIWire(submodelModifiable, cpI32, nandI3I4.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, notI5.getPin("Y"), cpNotI51, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI51, CinE, new Point(40, 5), new Point(115, 5), new Point(115, 20));
		new GUIWire(submodelModifiable, cpNotI51, cpNotI52, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI52, nandI4I3NotI5.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI52, andSBE.getPin("B"), new Point(40, 150));
		new GUIWire(submodelModifiable, notI4.getPin("Y"), nandI5NotI4.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nandI4I3NotI5.getPin("Y"), nandL.getPin("A"));
		new GUIWire(submodelModifiable, nandI5NotI4.getPin("Y"), nandL.getPin("B"));
		new GUIWire(submodelModifiable, nandI3I4.getPin("Y"), andSBE.getPin("A"));
		new GUIWire(submodelModifiable, nandL.getPin("Y"), L, new Point[0]);
		new GUIWire(submodelModifiable, andSBE.getPin("Y"), SBE, new Point[0]);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901ALUFuncDecode.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2901ALUFuncDecode(m, n));
	}
}