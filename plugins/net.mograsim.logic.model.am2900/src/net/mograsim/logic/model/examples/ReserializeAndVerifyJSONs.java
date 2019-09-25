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
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.LogicModelParams.ComponentParams;
import net.mograsim.logic.model.serializing.LogicModelParams.WireParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandler.StandardHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler.AtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.DelegatingAtomicHighLevelStateHandler.DelegatingAtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.WireForcingAtomicHighLevelStateHandler.WireForcingAtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.DelegatingSubcomponentHighLevelStateHandler.DelegatingSubcomponentHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler.SubcomponentHighLevelStateHandlerParams;
import net.mograsim.logic.model.util.JsonHandler;

public class ReserializeAndVerifyJSONs
{
	public static double GRIDSIZE = 2.5;
	public static boolean changePinUsages = false;
	public static boolean changeComponentNames = true;
	public static boolean forceDefaultComponentNames = true;
	public static boolean changeWireNames = true;
	public static boolean forceDefaultWireNames = true;
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
			SubmodelComponentParams oldComponentJSON = JsonHandler.readJson(componentPath.toString(), SubmodelComponentParams.class);
			DeserializedSubmodelComponent comp = (DeserializedSubmodelComponent) SubmodelComponentSerializer
					.deserialize(new LogicModelModifiable(), oldComponentJSON);
			System.out.println("Reserializing " + componentPath);
			LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
			Map<String, String> componentNameRemapping = new HashMap<>();
			Map<String, String> wireNameRemapping = new HashMap<>();

			if (changePinUsages)
				changePinUsages(sysin, comp);
			if (changeComponentNames)
				changeComponentNames(sysin, submodelModifiable, componentNameRemapping);
			if (changeWireNames)
				changeWireNames(sysin, submodelModifiable, wireNameRemapping);
			if (snapWCPs)
				snapWCPs(submodelModifiable);
			if (warnNonSnappedPoints)
				warnNonSnappedPoints(comp, submodelModifiable);
			if (warnNonVertHorizLines)
				warnNonVertHorizLines(submodelModifiable);
			if (warnRedundantWires)
				warnRedundantWires(submodelModifiable);

			SubmodelComponentParams newComponentJSON = SubmodelComponentSerializer.serialize(comp);

			if (changeComponentNames)
				changeComponentNames_AfterSerialization(newComponentJSON, componentNameRemapping);
			if (changeWireNames)
				changeWireNames_AfterSerialization(newComponentJSON, wireNameRemapping);
			sortAllJSONArrays(newComponentJSON);

			JsonHandler.writeJson(newComponentJSON, componentPath.toString());
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

	@SuppressWarnings("unused") // TextComponent
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
					String newName = forceDefaultComponentNames ? defaultName : null;
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
					new ModelTextComponent(tempModel, "", newName);
				});
	}

	private static void changeComponentNames_AfterSerialization(SubmodelComponentParams newComponentJSON,
			Map<String, String> componentNameRemapping)
	{
		for (ComponentParams cParams : newComponentJSON.submodel.components)
			cParams.name = componentNameRemapping.get(cParams.name);
		for (WireParams wParams : newComponentJSON.submodel.wires)
		{
			wParams.pin1.compName = componentNameRemapping.get(wParams.pin1.compName);
			wParams.pin2.compName = componentNameRemapping.get(wParams.pin2.compName);
		}
		if ("standard".equals(newComponentJSON.highLevelStateHandlerSnippetID))
		{
			StandardHighLevelStateHandlerParams hlshParams = JsonHandler.fromJsonTree(newComponentJSON.highLevelStateHandlerParams,
					StandardHighLevelStateHandlerParams.class);
			for (AtomicHighLevelStateHandlerParams ahlshParams : hlshParams.atomicHighLevelStates.values())
				if ("delegating".equals(ahlshParams.id))
				{
					DelegatingAtomicHighLevelStateHandlerParams dhlshParams = JsonHandler.fromJsonTree(ahlshParams.params,
							DelegatingAtomicHighLevelStateHandlerParams.class);
					dhlshParams.delegateTarget = componentNameRemapping.get(dhlshParams.delegateTarget);
					ahlshParams.params = JsonHandler.toJsonTree(dhlshParams);
				}
			for (SubcomponentHighLevelStateHandlerParams shlshParams : hlshParams.subcomponentHighLevelStates.values())
				if ("delegating".equals(shlshParams.id))
				{
					DelegatingSubcomponentHighLevelStateHandlerParams dhlshParams = JsonHandler.fromJsonTree(shlshParams.params,
							DelegatingSubcomponentHighLevelStateHandlerParams.class);
					dhlshParams.delegateTarget = componentNameRemapping.get(dhlshParams.delegateTarget);
					shlshParams.params = JsonHandler.toJsonTree(dhlshParams);
				}
			newComponentJSON.highLevelStateHandlerParams = JsonHandler.toJsonTree(hlshParams);
		}
	}

	@SuppressWarnings("unused") // Wire
	private static void changeWireNames(Scanner sysin, LogicModelModifiable submodelModifiable, Map<String, String> wireNameRemapping)
	{
		LogicModelModifiable tempModel = new LogicModelModifiable();
		Pin p = new ModelWireCrossPoint(tempModel, 1).getPin();
		IdentifyParams iP = new IdentifyParams();
		submodelModifiable.getWiresByName().entrySet().stream()
				.sorted(Comparator.comparing(Entry::getKey, ReserializeAndVerifyJSONs::compareStringsWithIntegers)).forEach(e ->
				{
					String oldName = e.getKey();
					String defaultName = tempModel.getDefaultWireName();
					String newName = forceDefaultWireNames ? defaultName : null;
					while (newName == null)
					{
						System.out.print("  New name for wire " + oldName + " (empty: " + defaultName + ") >");
						newName = sysin.nextLine();
						if (newName.equals(""))
							newName = defaultName;
						if (tempModel.getComponentsByName().containsKey(newName))
						{
							System.err.println("  There already is a component with that name");
							newName = null;
						}
					}
					wireNameRemapping.put(oldName, newName);
					new ModelWire(tempModel, newName, p, p);
				});
	}

	private static void changeWireNames_AfterSerialization(SubmodelComponentParams newComponentJSON, Map<String, String> wireNameRemapping)
	{
		for (WireParams wParams : newComponentJSON.submodel.wires)
			wParams.name = wireNameRemapping.get(wParams.name);
		if ("standard".equals(newComponentJSON.highLevelStateHandlerSnippetID))
		{
			StandardHighLevelStateHandlerParams hlshParams = JsonHandler.fromJsonTree(newComponentJSON.highLevelStateHandlerParams,
					StandardHighLevelStateHandlerParams.class);
			for (AtomicHighLevelStateHandlerParams ahlshParams : hlshParams.atomicHighLevelStates.values())
				if ("wireForcing".equals(ahlshParams.id))
				{
					WireForcingAtomicHighLevelStateHandlerParams whlshParams = JsonHandler.fromJsonTree(ahlshParams.params,
							WireForcingAtomicHighLevelStateHandlerParams.class);
					whlshParams.wiresToForce = whlshParams.wiresToForce.stream().map(wireNameRemapping::get).collect(Collectors.toList());
					whlshParams.wiresToForceInverted = whlshParams.wiresToForceInverted.stream().map(wireNameRemapping::get)
							.collect(Collectors.toList());
					ahlshParams.params = JsonHandler.toJsonTree(whlshParams);
				}
			newComponentJSON.highLevelStateHandlerParams = JsonHandler.toJsonTree(hlshParams);
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

	private static void sortAllJSONArrays(SubmodelComponentParams newComponentJSON)
	{
		Comparator<String> c = ReserializeAndVerifyJSONs::compareStringsWithIntegers;
		Arrays.sort(newComponentJSON.interfacePins, Comparator.comparing(p -> p.name, c));
		Arrays.sort(newComponentJSON.submodel.components, Comparator.comparing(p -> p.name, c));
		Arrays.sort(newComponentJSON.submodel.wires, Comparator.comparing(p -> p.name, c));
		if ("standard".equals(newComponentJSON.highLevelStateHandlerSnippetID))
		{
			StandardHighLevelStateHandlerParams hlshP = JsonHandler.fromJsonTree(newComponentJSON.highLevelStateHandlerParams,
					StandardHighLevelStateHandlerParams.class);
			TreeMap<String, AtomicHighLevelStateHandlerParams> tmp1 = new TreeMap<>(c);
			tmp1.putAll(hlshP.atomicHighLevelStates);
			hlshP.atomicHighLevelStates = tmp1;
			TreeMap<String, SubcomponentHighLevelStateHandlerParams> tmp2 = new TreeMap<>(c);
			tmp2.putAll(hlshP.subcomponentHighLevelStates);
			hlshP.subcomponentHighLevelStates = tmp2;
			newComponentJSON.highLevelStateHandlerParams = JsonHandler.toJsonTree(hlshP);
		}
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
			int aInt = 1, bInt = 1;// 1 so a longer number is always greater (makes a difference for leading zeroes)
			boolean aHasNumber = false, bHasNumber = false;
			char nextCharA, nextCharB;
			for (;;)
			{
				nextCharA = a.charAt(aLoc++);
				if (nextCharA < '0' || nextCharA > '9')
					break;
				aHasNumber = true;
				aInt = aInt * 10 + nextCharA - '0';
				if (aLoc == a.length())
					break;
			}
			for (;;)
			{
				nextCharB = b.charAt(bLoc++);
				if (nextCharB < '0' || nextCharB > '9')
					break;
				bHasNumber = true;
				bInt = bInt * 10 + nextCharB - '0';
				if (bLoc == b.length())
					break;
			}
			if (aHasNumber)
			{
				if (!bHasNumber)
					return -1;
				int comp = Integer.compare(aInt, bInt);
				if (comp != 0)
					return comp;
			} else
			{
				if (bHasNumber)
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