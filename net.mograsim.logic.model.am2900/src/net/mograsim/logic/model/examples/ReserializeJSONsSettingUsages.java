package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;

public class ReserializeJSONsSettingUsages
{
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
			comp.getSupermodelPins().entrySet().stream().sorted(Comparator.comparing(Entry::getKey)).map(Entry::getValue).forEach(pin ->
			{
				System.out.print("  Usage for interface pin " + pin.name + " (empty: " + pin.usage + ") >");
				String usageStr = sysin.nextLine().toUpperCase();
				PinUsage usage = usageStr.equals("") ? pin.usage
						: usageStr.equals("I") ? PinUsage.INPUT
								: usageStr.equals("O") ? PinUsage.OUTPUT
										: usageStr.equals("T") ? PinUsage.TRISTATE : PinUsage.valueOf(usageStr);
				setInterfacePinUsage(comp, pin, usage);
			});
			SubmodelComponentSerializer.serialize(comp, json.toString());
		}
		catch (Exception e)
		{
			System.err.println("An error occurred visiting " + json + ":");
			e.printStackTrace();
		}
	}

	private static void setInterfacePinUsage(DeserializedSubmodelComponent comp, Pin interfacePin, PinUsage usage)
	{
		Set<ModelWire> wiresConnectedToPin = comp.submodel.getWiresByName().values().stream()
				.filter(w -> w.getPin1() == interfacePin || w.getPin2() == interfacePin).collect(Collectors.toSet());
		wiresConnectedToPin.forEach(comp.getSubmodelModifiable()::destroyWire);
		comp.removeSubmodelInterface(interfacePin.name);
		comp.addSubmodelInterface(
				new MovablePin(comp, interfacePin.name, interfacePin.logicWidth, usage, interfacePin.getRelX(), interfacePin.getRelY()));
		LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		wiresConnectedToPin.forEach(w -> new ModelWire(submodelModifiable, w.getPin1(), w.getPin2()));
	}
}