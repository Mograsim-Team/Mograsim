package net.mograsim.logic.model.examples;

import java.util.concurrent.atomic.AtomicReference;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.SimpleLogicUIStandalone.VisualisationObjects;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.Renderer;

public class FusionNotWorkingTest
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(FusionNotWorkingTest::create, FusionNotWorkingTest::beforeRun);
	}

	public static void create(ViewModelModifiable model)
	{
		// TODO use _SE in GUIAm2904.json
		// TODO replace with proper ViewModel deserialization
		DeserializedSubmodelComponent testbench = (DeserializedSubmodelComponent) IndirectGUIComponentCreator.createComponent(model,
				"file:FusionNotWorkingTest.json", "testbench");
		testbench.setSize(1000, 1000);
		testbench.setOutlineRenderer(new Renderer()
		{
			@Override
			public Object getParamsForSerializing(IdentifierGetter idGetter)
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
		// TODO workaround for rendering bug
		AtomicReference<TimelineEventHandler> functionRef = new AtomicReference<>();
		LogicUICanvas logicUICanvas = vis.gui.getLogicUICanvas();
		TimelineEventHandler function = e ->
		{
			logicUICanvas.redrawThreadsafe();
			vis.timeline.addEvent(functionRef.get(), 50);
		};
		functionRef.set(function);
		vis.timeline.addEvent(function, 0);
	}
}