package net.mograsim.logic.model.am2900.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.model.LogicUIStandaloneGUI;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.am2900.TestableCircuit;
import net.mograsim.logic.model.am2900.TestableCircuit.Result;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.ModellingTool;

public class TestEnvironmentHelper
{
	private final TestableCircuit testEnvInstance;
	private final Class<?> testEnvClass;
	private final String modelId;
	private Field componentField;
	private Optional<Field> timelineField = Optional.empty();

	private ModelComponent component;
	private Timeline timeline;
	private ViewModelModifiable viewModel;
	private ModellingTool modellingTool;
	private HashMap<String, ModelManualSwitch> idSwitchMap = new HashMap<>();
	private HashMap<String, ModelBitDisplay> idDisplayMap = new HashMap<>();

	private DebugState debug = DebugState.NO_DEBUG;
	private Set<String> wireDebugChangeSet;
	private boolean debugWires = false;
	public int debugEventThreshold = 10_000;
	public int debugEventCount = 500;
	private int eventCounter;

	public TestEnvironmentHelper(TestableCircuit testEnv, String modelId)
	{
		this.testEnvInstance = testEnv;
		this.modelId = modelId;
		this.testEnvClass = testEnvInstance.getClass();
		for (Field f : testEnvClass.getDeclaredFields())
		{
			if (ModelComponent.class.isAssignableFrom(f.getType()))
			{
				componentField = f;
				componentField.setAccessible(true);
			} else if (Timeline.class.isAssignableFrom(f.getType()))
			{
				f.setAccessible(true);
				timelineField = Optional.of(f);
			}
		}
		if (componentField == null)
			throw new IllegalStateException("No component or timeline field found!");
	}

	public void setup(DebugState debug)
	{
		this.debug = debug;
		// Create view model
		viewModel = new ViewModelModifiable();
		modellingTool = ModellingTool.createFor(viewModel);
		Am2900Loader.setup();
		component = IndirectModelComponentCreator.createComponent(viewModel, modelId);
		setField(componentField, component);

		component.getPins().values().forEach(this::extendModelPin);

		// Create logic model
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		timeline = ViewLogicModelAdapter.convert(viewModel, params);
		timelineField.ifPresent(f -> setField(f, timeline));

		// Bind switches/displays to this test class
		component.getPins().values().forEach(this::bindModelPin);

		if (debug == DebugState.DEBUG_AT_PERFORMANCE_COST)
		{
			setupDebugging();
		}
		timeline.addEventAddedListener(te -> eventCounter++);
	}

	private void extendModelPin(Pin p)
	{
		String javaIdentId = idToJavaIdentifier(p.name);
		try
		{
			Field f = testEnvClass.getDeclaredField(javaIdentId);
			Class<?> type = f.getType();
			if (CoreManualSwitch.class.isAssignableFrom(type))
			{
				ModelManualSwitch gms = new ModelManualSwitch(viewModel, p.logicWidth);
				modellingTool.connect(p, gms.getOutputPin());
				idSwitchMap.put(p.name, gms);
			} else if (CoreBitDisplay.class.isAssignableFrom(type))
			{
				ModelBitDisplay gbd = new ModelBitDisplay(viewModel, p.logicWidth);
				modellingTool.connect(p, gbd.getInputPin());
				idDisplayMap.put(p.name, gbd);
			} else if (SwitchWithDisplay.class.isAssignableFrom(type))
			{
				SwitchWithDisplay swd = new SwitchWithDisplay(viewModel, p);
				setField(f, swd);
			} else
			{
				fail("unkown field type " + type);
			}
		}
		catch (NoSuchFieldException | SecurityException e)
		{
			fail(e);
		}
	}

	private void bindModelPin(Pin p)
	{
		String javaIdentId = idToJavaIdentifier(p.name);
		if (idDisplayMap.containsKey(p.name))
			setField(javaIdentId, idDisplayMap.get(p.name).getBitDisplay());
		if (idSwitchMap.containsKey(p.name))
			setField(javaIdentId, idSwitchMap.get(p.name).getManualSwitch());
	}

	private void setupDebugging()
	{
		// Debug code
		HashSet<ModelWire> wiresIncludingSubmodels = new HashSet<>();
		Queue<ViewModel> modelsToIterate = new LinkedList<>();
		modelsToIterate.add(viewModel);
		while (modelsToIterate.size() > 0)
		{
			ViewModel model = modelsToIterate.poll();
			wiresIncludingSubmodels.addAll(model.getWiresByName().values());
			for (ModelComponent comp : model.getComponentsByName().values())
				if (comp instanceof SubmodelComponent)
					modelsToIterate.offer(((SubmodelComponent) comp).submodel);
		}
		System.out.println(wiresIncludingSubmodels.size());
		viewModel.setRedrawHandler(() -> wiresIncludingSubmodels.forEach(w ->
		{
			if (debugWires)
			{
				wireDebugChangeSet.add(w.toString());
			}
		}));
	}

	public Result run()
	{
		// Normal execution until completion or eventLimit
		int eventLimit = debugEventThreshold;
		eventCounter = 0;
		debugWires = false;
		while (eventCounter < eventLimit)
		{
			timeline.executeNext();
			if (!timeline.hasNext())
				return Result.SUCCESS;
		}

		// Start debugging if event limit is reached (if debug is active)
		if (debug == DebugState.DEBUG_AT_PERFORMANCE_COST)
			return debugThisRun();

		return Result.OUT_OF_TIME;
	}

	private Result debugThisRun()
	{
		int eventLimit = debugEventThreshold;
		debugWires = true;
		wireDebugChangeSet = new TreeSet<>();
		Set<String> oldChangeSet;
		// observe wire changes to detect, if we are really stuck in an endless loop
		do
		{
			eventLimit += debugEventCount;
			oldChangeSet = wireDebugChangeSet;
			wireDebugChangeSet = new TreeSet<>();
			while (eventCounter < eventLimit)
			{
				timeline.executeNext();
				if (!timeline.hasNext())
				{
					// no endless loop, but more events needed than expected
					System.out.println("run() took longer than expected: " + eventCounter);
					return Result.SUCCESS;
				}
			}
		} while (!oldChangeSet.equals(wireDebugChangeSet));
		// if stuck, abort execution and print wires
		System.err.print("Problematic Wire updates:");
		wireDebugChangeSet.forEach(System.out::println);
		System.err.println("run() failed: " + eventCounter);
		return Result.OUT_OF_TIME;
	}

	private static String idToJavaIdentifier(String s)
	{
		StringBuilder sb = new StringBuilder(s.length());
		char c = s.charAt(0);
		sb.append(Character.isJavaIdentifierStart(c) ? c : '_');
		for (int i = 1; i < s.length(); i++)
			sb.append(Character.isJavaIdentifierPart(c = s.charAt(i)) ? c : '_');
		return sb.toString();
	}

	private <S> void setField(Field f, S value)
	{
		try
		{
			f.setAccessible(true);
			f.set(testEnvInstance, Objects.requireNonNull(value));
		}
		catch (Exception e)
		{
			fail(e);
		}
	}

	private <S> void setField(String name, S value)
	{
		try
		{
			Field f = testEnvClass.getDeclaredField(name);
			f.setAccessible(true);
			f.set(testEnvInstance, Objects.requireNonNull(value));
		}
		catch (Exception e)
		{
			fail(e);
		}
	}

	public void displayState()
	{
		try
		{
			new LogicUIStandaloneGUI(viewModel).run();
			viewModel.setRedrawHandler(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public enum DebugState
	{
		NO_DEBUG, DEBUG_AT_PERFORMANCE_COST;
	}
}
