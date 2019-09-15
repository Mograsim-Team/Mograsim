package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.DefaultHighLevelStateHandler;

public class ReserializeAndVerifyJSONs
{
	public static double GRIDSIZE = 2.5;
	public static boolean changePinUsages = false;
	public static boolean changeComponentNames = false;
	public static boolean snapWCPs = true;
	public static boolean warnNonSnappedPoints = true;
	public static boolean warnNonVertHorizLines = true;
	public static boolean warnRedundantWires = true;

	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();
		try (Scanner sysin = new Scanner(System.in))
		{
			System.out.print("Directory to search for JSONs in / JSON file to reserialize >");
			Path root = Paths.get(sysin.nextLine());
			if (!Files.exists(root))
				throw new IllegalArgumentException("Path doesn't exist");
			if (Files.isRegularFile(root))
				reserializeJSON(root, sysin);
			else
			{
				System.out.print("Recursive? >");
				boolean recursive = Boolean.valueOf(sysin.nextLine());
				try (Stream<Path> jsons = recursive ? Files.walk(root) : Files.list(root))
				{
					jsons.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".json"))
							.forEach(j -> reserializeJSON(j, sysin));
				}
			}
		}
	}

	public static void reserializeJSON(Path componentPath, Scanner sysin)
	{
		try
		{
			DeserializedSubmodelComponent comp = (DeserializedSubmodelComponent) IndirectModelComponentCreator
					.createComponent(new LogicModelModifiable(), "jsonfile:" + componentPath.toString());
			System.out.println("Reserializing " + componentPath);
			LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
			Map<String, String> componentNameRemapping = new HashMap<>();

			if (changePinUsages)
				changePinUsages(sysin, comp);
			if (changeComponentNames)
				changeComponentNames(sysin, submodelModifiable, componentNameRemapping);
			if (snapWCPs)
				snapWCPs(submodelModifiable);
			if (warnNonSnappedPoints)
				warnNonSnappedPoints(comp, submodelModifiable);
			if (warnNonVertHorizLines)
				warnNonVertHorizLines(submodelModifiable);
			if (warnRedundantWires)
				warnRedundantWires(submodelModifiable);

			SubmodelComponentSerializer.serialize(comp, componentPath.toString());

			if (changeComponentNames)
				changeComponentNames_AfterSerialization(sysin, comp, componentNameRemapping);
		}
		catch (Exception e)
		{
			System.err.println("An error occurred visiting " + componentPath + ":");
			e.printStackTrace();
		}
	}

	private static void warnRedundantWires(LogicModelModifiable submodelModifiable)
	{
		Map<Pin, Set<Pin>> connectedPinGroups = new HashMap<>();
		submodelModifiable.getComponentsByName().values().stream().map(ModelComponent::getPins).map(Map::values).flatMap(Collection::stream)
				.forEach(p -> connectedPinGroups.put(p, new HashSet<>(Arrays.asList(p))));
		submodelModifiable.getWiresByName().values().forEach(w ->
		{
			Pin pin1 = w.getPin1();
			Pin pin2 = w.getPin2();
			Set<Pin> pin1Group = connectedPinGroups.get(pin1);
			Set<Pin> pin2Group = connectedPinGroups.get(pin2);
			if (pin1Group == pin2Group)
				System.out.println("  Wire " + w.name + " connecting " + pin1 + " and " + pin2 + " is redundant");
			else
			{
				pin1Group.addAll(pin2Group);
				pin2Group.forEach(p -> connectedPinGroups.put(p, pin1Group));
			}
		});
	}

	private static void changePinUsages(Scanner sysin, DeserializedSubmodelComponent comp)
	{
		comp.getSupermodelPins().entrySet().stream().sorted(Comparator.comparing(Entry::getKey)).map(Entry::getValue).forEach(pin ->
		{
			PinUsage usage = null;
			while (usage == null)
				try
				{
					System.out.print("  Usage for interface pin " + pin.name + " (empty: " + pin.usage + ") >");
					String usageStr = sysin.nextLine().toUpperCase();
					usage = usageStr.equals("") ? pin.usage
							: usageStr.equals("I") ? PinUsage.INPUT
									: usageStr.equals("O") ? PinUsage.OUTPUT
											: usageStr.equals("T") ? PinUsage.TRISTATE : PinUsage.valueOf(usageStr);
				}
				catch (@SuppressWarnings("unused") IllegalArgumentException e)
				{
					System.err.println("  Illegal usage");
				}
			setInterfacePinUsage(comp, pin, usage);
		});
	}

	private static void changeComponentNames(Scanner sysin, LogicModelModifiable submodelModifiable,
			Map<String, String> componentNameRemapping)
	{
		componentNameRemapping.put(SubmodelComponent.SUBMODEL_INTERFACE_NAME, SubmodelComponent.SUBMODEL_INTERFACE_NAME);
		LogicModelModifiable tempModel = new LogicModelModifiable();
		IdentifyParams iP = new IdentifyParams();
		submodelModifiable.getComponentsByName().entrySet().stream()
				.filter(e -> !e.getKey().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				.sorted(Comparator.comparing(Entry::getKey, ReserializeAndVerifyJSONs::compareStringsWithIntegers)).forEach(e ->
				{
					String oldName = e.getKey();
					ModelComponent subcomp = e.getValue();
					String defaultName = tempModel.getDefaultComponentName(subcomp);
					String newName = null;
					while (newName == null)
					{
						System.out.print("  New name for component " + oldName + " of type " + subcomp.getIDForSerializing(iP) + " (empty: "
								+ defaultName + ") >");
						newName = sysin.nextLine();
						if (newName.equals(""))
							newName = defaultName;
						if (tempModel.getComponentsByName().containsKey(newName))
						{
							System.err.println("  There already is a component with that name");
							newName = null;
						}
					}
					componentNameRemapping.put(oldName, newName);
					IndirectModelComponentCreator
							.createComponent(tempModel, subcomp.getIDForSerializing(iP), subcomp.getParamsForSerializingJSON(iP), newName)
							.moveTo(subcomp.getPosX(), subcomp.getPosY());
				});
		SubmodelInterface tempSubmodelInterface = new SubmodelInterface(tempModel);
		for (Pin p : submodelModifiable.getComponentsByName().get(SubmodelComponent.SUBMODEL_INTERFACE_NAME).getPins().values())
			tempSubmodelInterface
					.addPin(new Pin(tempModel, tempSubmodelInterface, p.name, p.logicWidth, p.usage, p.getRelX(), p.getRelY()));
		for (ModelWire w : submodelModifiable.getWiresByName().values())
			createWire(componentNameRemapping::get, tempModel, w);

		Optional<ModelComponent> o;
		while ((o = submodelModifiable.getComponentsByName().values().stream()
				.filter(c -> !c.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME)).findAny()).isPresent())
			submodelModifiable.destroyComponent(o.get());

		tempModel.getComponentsByName().values().stream().filter(c -> !c.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				.forEach(c -> IndirectModelComponentCreator
						.createComponent(submodelModifiable, c.getIDForSerializing(iP), c.getParamsForSerializingJSON(iP), c.getName())
						.moveTo(c.getPosX(), c.getPosY()));
		for (ModelWire w : tempModel.getWiresByName().values())
			createWire(Function.identity(), submodelModifiable, w);
	}

	private static void changeComponentNames_AfterSerialization(Scanner sysin, DeserializedSubmodelComponent comp,
			Map<String, String> componentNameRemapping)
	{
		if (comp.getHighLevelStateHandler() == null || !(comp.getHighLevelStateHandler() instanceof DefaultHighLevelStateHandler))
		{
			System.out.println("  A non-default HighLevelStateHandler was detected. Check for changes there manually.");
			System.out.print("  Empty line to continue to next component, old component name to get new component name >");
			for (String line = sysin.nextLine(); !line.equals(""); line = sysin.nextLine())
				System.out.println("  " + line + "->" + componentNameRemapping.get(line) + " >");
		}
	}

	private static void snapWCPs(LogicModelModifiable submodelModifiable)
	{
		submodelModifiable.getComponentsByName().values().stream().filter(c -> c instanceof ModelWireCrossPoint).forEach(c ->
		{
			double x = c.getPosX();
			double y = c.getPosY();
			double newX = x % GRIDSIZE == 0 ? x - 1 : x;
			double newY = y % GRIDSIZE == 0 ? y - 1 : y;
			if (x != newX || y != newY)
			{
				c.moveTo(newX, newY);
				System.out.println("  Snapping WCP " + c.getName());
			}
		});
	}

	private static void warnNonSnappedPoints(DeserializedSubmodelComponent comp, LogicModelModifiable submodelModifiable)
	{
		if (comp.getWidth() % GRIDSIZE != 0 || comp.getHeight() % GRIDSIZE != 0)
			System.out.println("  Size is not snapped to grid: " + comp.getWidth() + "," + comp.getHeight());
		submodelModifiable.getComponentsByName().values().forEach(c ->
		{
			double x = c.getPosX();
			double y = c.getPosY();
			if (c instanceof ModelWireCrossPoint)
			{
				x++;
				y++;
			}
			if (x % GRIDSIZE != 0 || y % GRIDSIZE != 0)
				System.out.println("  Component " + c.getName() + " (type " + c.getIDForSerializing(new IdentifyParams())
						+ ") is not snapped to grid: " + x + "," + y);
		});
		submodelModifiable.getWiresByName().values().forEach(w ->
		{
			Point[] p = w.getPath();
			if (p != null)
				for (int i = 0; i < p.length; i++)
					if (p[i].x % GRIDSIZE != 0 || p[i].y % GRIDSIZE != 0)
						System.out.println("  Wire " + w.name + " path point #" + i + " is not snapped to grid: " + p[i].x + "," + p[i].y);
		});
		comp.getPins().values().forEach(p ->
		{
			if (p.getRelX() % GRIDSIZE != 0 || p.getRelY() % GRIDSIZE != 0)
				System.out.println("  Interface point " + p.name + " is not snapped to grid: " + p.getRelX() + "," + p.getRelY());
		});
	}

	private static void warnNonVertHorizLines(LogicModelModifiable submodelModifiable)
	{
		submodelModifiable.getWiresByName().values().forEach(w ->
		{
			double[] p = w.getEffectivePath();
			for (int i = 1; i < p.length / 2; i++)
			{
				double x1 = p[2 * i - 2];
				double y1 = p[2 * i - 1];
				double x2 = p[2 * i + 0];
				double y2 = p[2 * i + 1];
				if (x1 != x2 && y1 != y2)
					System.out.println("  Wire " + w.name + " part #" + (i - 1) + " is neither vertical nor horizontal");
			}
		});
	}

	private static ModelWire createWire(Function<String, String> componentNameRemapping, LogicModelModifiable tempModelForDefaultNames,
			ModelWire w)
	{
		return new ModelWire(tempModelForDefaultNames, w.name,
				getRemappedPin(componentNameRemapping, tempModelForDefaultNames, w.getPin1()),
				getRemappedPin(componentNameRemapping, tempModelForDefaultNames, w.getPin2()), w.getPath());
	}

	private static Pin getRemappedPin(Function<String, String> componentNameRemapping, LogicModelModifiable tempModelForDefaultNames,
			Pin pin)
	{
		return tempModelForDefaultNames.getComponentsByName().get(componentNameRemapping.apply(pin.component.getName())).getPin(pin.name);
	}

	private static int compareStringsWithIntegers(String a, String b)
	{
		int aLoc = 0;
		int bLoc = 0;
		for (;;)
		{
			if (aLoc == a.length())
			{
				if (bLoc == b.length())
					return 0;
				return -1;
			}
			if (bLoc == b.length())
				return 1;
			int aInt = 0;
			int aIntLen = 0;
			char nextCharA;
			for (;;)
			{
				nextCharA = a.charAt(aLoc++);
				if (nextCharA < '0' || nextCharA > '9')
					break;
				aIntLen++;
				aInt = aInt * 10 + nextCharA - '0';
				if (aLoc == a.length())
					break;
			}
			int bInt = 0;
			int bIntLen = 0;
			char nextCharB;
			for (;;)
			{
				nextCharB = b.charAt(bLoc++);
				if (nextCharB < '0' || nextCharB > '9')
					break;
				bIntLen++;
				bInt = bInt * 10 + nextCharB - '0';
				if (bLoc == b.length())
					break;
			}
			if (aIntLen != 0)
			{
				if (bIntLen == 0)
					return -1;
				int comp = Integer.compare(aInt, bInt);
				if (comp != 0)
					return comp;
			} else
			{
				if (bIntLen != 0)
					return 1;
				int comp = Character.compare(nextCharA, nextCharB);
				if (comp != 0)
					return comp;
			}
		}
	}

	private static void setInterfacePinUsage(DeserializedSubmodelComponent comp, Pin interfacePin, PinUsage usage)
	{
		Set<ModelWire> wiresConnectedToPin = comp.submodel.getWiresByName().values().stream()
				.filter(w -> w.getPin1() == interfacePin || w.getPin2() == interfacePin).collect(Collectors.toSet());
		LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		wiresConnectedToPin.forEach(submodelModifiable::destroyWire);
		comp.removeSubmodelInterface(interfacePin.name);
		comp.addSubmodelInterface(new MovablePin(submodelModifiable, comp, interfacePin.name, interfacePin.logicWidth, usage,
				interfacePin.getRelX(), interfacePin.getRelY()));
		wiresConnectedToPin.forEach(w -> new ModelWire(submodelModifiable, w.getPin1(), w.getPin2()));
	}
}