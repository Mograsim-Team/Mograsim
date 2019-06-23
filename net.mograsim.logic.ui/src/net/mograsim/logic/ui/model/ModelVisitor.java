package net.mograsim.logic.ui.model;

import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SimpleRectangularGUIGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.SubmodelInterface;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public interface ModelVisitor
{
	void visit(GUIWire w);

	void visit(SimpleRectangularGUIGate simpleRectangularGUIGate);

	void visit(SimpleRectangularSubmodelComponent simpleRectangularSubmodelComponent);

	void visit(WireCrossPoint wireCrossPoint);

	void visit(GUIBitDisplay guiBitDisplay);

	void visit(GUIManualSwitch guiManualSwitch);

	void visit(SubmodelInterface submodelInterface);

	void visit(ViewModel viewModel);
}
