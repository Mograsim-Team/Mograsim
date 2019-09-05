package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;

public class ReserializeJSONsSettingUsages
{
	public static boolean changePinUsages = false;
	public static boolean changeComponentNames = true;

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

	public static void reserializeJSON(Path json, Scanner sysin)
	{
		try
		{
			DeserializedSubmodelComponent comp = (DeserializedSubmodelComponent) IndirectModelComponentCreator
					.createComponent(new LogicModelModifiable(), "jsonfile:" + json.toString());
			System.out.println("Reserializing " + json);
			if (changePinUsages)
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
			LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
			if (changeComponentNames)
			{
				Map<String, String> componentNameRemapping = new HashMap<>();
				componentNameRemapping.put(SubmodelComponent.SUBMODEL_INTERFACE_NAME, SubmodelComponent.SUBMODEL_INTERFACE_NAME);
				LogicModelModifiable tempModel = new LogicModelModifiable();
				IdentifyParams iP = new IdentifyParams();
				submodelModifiable.getComponentsByName().entrySet().stream()
						.filter(e -> !e.getKey().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
						.sorted(Comparator.comparing(Entry::getKey, ReserializeJSONsSettingUsages::compareStringsWithIntegers)).forEach(e ->
						{
							String oldName = e.getKey();
							ModelComponent subcomp = e.getValue();
							String defaultName = tempModel.getDefaultComponentName(subcomp);
							String newName = null;
							while (newName == null)
							{
								System.out.print("  New name for component " + oldName + " of type " + subcomp.getIDForSerializing(iP)
										+ " (empty: " + defaultName + ") >");
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
							IndirectModelComponentCreator.createComponent(tempModel, subcomp.getIDForSerializing(iP),
									subcomp.getParamsForSerializingJSON(iP), newName).moveTo(subcomp.getPosX(), subcomp.getPosY());
						});
				SubmodelInterface tempSubmodelInterface = new SubmodelInterface(tempModel);
				for (Pin p : submodelModifiable.getComponentsByName().get(SubmodelComponent.SUBMODEL_INTERFACE_NAME).getPins().values())
					tempSubmodelInterface
							.addPin(new Pin(tempModel, tempSubmodelInterface, p.name, p.logicWidth, p.usage, p.getRelX(), p.getRelY()));
				for (ModelWire w : submodelModifiable.getWiresByName().values())
					createWire(componentNameRemapping::get, tempModel, w);

				Optional<ModelComponent> o;
				while ((o = submodelModifiable.getComponentsByName().values().stream()
						.filter(c -> !c.name.equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME)).findAny()).isPresent())
					submodelModifiable.destroyComponent(o.get());

				tempModel.getComponentsByName().values().stream().filter(c -> !c.name.equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
						.forEach(c -> IndirectModelComponentCreator
								.createComponent(submodelModifiable, c.getIDForSerializing(iP), c.getParamsForSerializingJSON(iP), c.name)
								.moveTo(c.getPosX(), c.getPosY()));
				for (ModelWire w : tempModel.getWiresByName().values())
					createWire(Function.identity(), submodelModifiable, w);
			}
			SubmodelComponentSerializer.serialize(comp, json.toString());
		}
		catch (Exception e)
		{
			System.err.println("An error occurred visiting " + json + ":");
			e.printStackTrace();
		}
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
		return tempModelForDefaultNames.getComponentsByName().get(componentNameRemapping.apply(pin.component.name)).getPin(pin.name);
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