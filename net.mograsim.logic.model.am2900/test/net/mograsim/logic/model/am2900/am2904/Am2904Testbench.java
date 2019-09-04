package net.mograsim.logic.model.am2900.am2904;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.SimpleLogicUIStandalone.VisualisationObjects;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.Renderer;

public class Am2904Testbench
{
	public static void main(String[] args)
	{
		Am2900Loader.setup();
		SimpleLogicUIStandalone.executeVisualisation(Am2904Testbench::create, Am2904Testbench::beforeRun);
	}

	public static void create(ViewModelModifiable model)
	{
		// TODO replace with proper ViewModel deserialization
		DeserializedSubmodelComponent testbench = (DeserializedSubmodelComponent) IndirectGUIComponentCreator.createComponent(model,
				"jsonfile:GUIAm2904Testbench.json", "testbench");
		testbench.setSize(1000, 1000);
		testbench.setOutlineRenderer(new Renderer()
		{
			@Override
			public String getIDForSerializing(IdentifyParams idParams)
			{
				return null;
			}

			@Override
			public Void getParamsForSerializing(IdentifyParams idParams)
			{
				return null;
			}

			@Override
			public void render(GeneralGC gc, Rectangle visibleRegion)
			{
				// do nothing
			}
		});
	}

	public static void beforeRun(VisualisationObjects vis)
	{
		((SubmodelComponent) vis.model.getComponentsByName().get("testbench")).submodel.getComponentsByName().values().forEach(c ->
		{
			if (c instanceof GUIManualSwitch)
			{
				GUIManualSwitch cCasted = (GUIManualSwitch) c;
				cCasted.setHighLevelState("out", BitVector.of(Bit.ZERO, cCasted.logicWidth));
			}
		});
	}
}