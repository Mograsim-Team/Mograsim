package net.mograsim.logic.model.examples;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.serializing.LogicModelParams.ComponentParams;
import net.mograsim.logic.model.serializing.LogicModelParams.WireParams;
import net.mograsim.logic.model.serializing.LogicModelParams.WireParams.PinParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;

//TODO clean this mess
public class VerilogExporter
{
	private static final String COMPONENT_PREFIX = "mgs_";

	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();
		try (Scanner sysin = new Scanner(System.in))
		{
			System.out.print("Directory to export Verilog into >");
			Path target = Paths.get(sysin.nextLine());
			if (!Files.exists(target))
				Files.createDirectories(target);
			else if (!Files.isDirectory(target))
				throw new IllegalArgumentException("Target exists and is not a directory");

			System.out.print("Component ID to serialize recursively >");
			String rootComponentID = sysin.nextLine();
			{
				if (!Files.exists(target))
					Files.createDirectories(target);
				else if (!Files.isDirectory(target))
					throw new IllegalArgumentException("Target exists and is not a directory");
				Map<String, SubmodelComponentParams> componentsById = readComponentIncludingDependencies(rootComponentID);
				Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID = new HashMap<>();
				Tuple2<Map<String, Map<String, String>>, Map<String, Map<String, Set<String>>>> combinedPinNames = generateCombinedPinNames(
						componentsById, pinIdentifierGeneratorsPerComponentID);
				Map<String, Tuple2<List<String>, List<Integer>>> sortedInterfacePinNamesAndWidthsPerComponentID = generateSortedInterfacePinNamesAndWidthesPerComponentID(
						componentsById, combinedPinNames.e1);
				writeComponentsVerilog(target, componentsById, pinIdentifierGeneratorsPerComponentID,
						sortedInterfacePinNamesAndWidthsPerComponentID, combinedPinNames);
			}
		}
	}

	private static Map<String, SubmodelComponentParams> readComponentIncludingDependencies(String rootComponentID)
	{
		Map<String, SubmodelComponentParams> result = new HashMap<>();
		readComponentIncludingDependenciesRecursively(rootComponentID, null, result);
		return result;
	}

	private static void readComponentIncludingDependenciesRecursively(String id, JsonElement params,
			Map<String, SubmodelComponentParams> result)
	{
		if (result.containsKey(id))
			return;

		ModelComponent component = IndirectModelComponentCreator.createComponent(new LogicModelModifiable(), id, params);
		if (component instanceof SubmodelComponent)
		{
			SubmodelComponentParams componentJson = SubmodelComponentSerializer.serialize((SubmodelComponent) component);
			result.put(id, componentJson);
			for (ComponentParams subcomponentParams : componentJson.submodel.components)
				readComponentIncludingDependenciesRecursively(subcomponentParams.id, subcomponentParams.params, result);
		}
	}

	private static Tuple2<Map<String, Map<String, String>>, Map<String, Map<String, Set<String>>>> generateCombinedPinNames(
			Map<String, SubmodelComponentParams> componentsById, Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID)
	{
		Map<String, Map<String, Set<String>>> connectedInnerPinNamesPerComponentID = new HashMap<>();

		generateConnectedInnerPins(componentsById, pinIdentifierGeneratorsPerComponentID, connectedInnerPinNamesPerComponentID);

		Map<String, Map<String, String>> combinedInterfacePinNamesPerComponentID = new HashMap<>();

		for (boolean anyChange = true; anyChange;)
		{
			anyChange = false;
			for (Entry<String, SubmodelComponentParams> e : componentsById.entrySet())
				anyChange |= checkForConnectedPins(e.getKey(), e.getValue(), componentsById, pinIdentifierGeneratorsPerComponentID,
						connectedInnerPinNamesPerComponentID, combinedInterfacePinNamesPerComponentID);
		}

		return new Tuple2<>(combinedInterfacePinNamesPerComponentID, connectedInnerPinNamesPerComponentID);
	}

	private static boolean checkForConnectedPins(String componentID, SubmodelComponentParams componentJson,
			Map<String, SubmodelComponentParams> componentsById, Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID,
			Map<String, Map<String, Set<String>>> connectedInnerPinNamesPerComponentID,
			Map<String, Map<String, String>> combinedPinNamesPerComponentID)
	{
		PinIdentifierGenerator pinIdentifierGenerator = pinIdentifierGeneratorsPerComponentID.get(componentID);
		Map<String, Set<String>> connectedInnerPinNames = connectedInnerPinNamesPerComponentID.get(componentID);
		Map<String, String> pinNameRemapping = combinedPinNamesPerComponentID.computeIfAbsent(componentID, k ->
		{
			Map<String, String> result = new HashMap<>();
			for (InterfacePinParams pinParams : componentJson.interfacePins)
				result.put(pinParams.name, pinParams.name);
			return result;
		});

		for (InterfacePinParams pin1Params : componentJson.interfacePins)
		{
			String pin1Name = pinNameRemapping.get(pin1Params.name);
			Set<String> connectedInnerPinNamesPin1 = connectedInnerPinNames
					.get(pinIdentifierGenerator.getPinID(SubmodelComponent.SUBMODEL_INTERFACE_NAME, pin1Name));
			if (connectedInnerPinNamesPin1 != null)
				for (InterfacePinParams pin2Params : componentJson.interfacePins)
				{
					String pin2Name = pinNameRemapping.get(pin2Params.name);
					String pin2InnerPinName = pinIdentifierGenerator.getPinID(SubmodelComponent.SUBMODEL_INTERFACE_NAME, pin2Name);
					if (connectedInnerPinNamesPin1.contains(pin2InnerPinName) && !pin1Name.equals(pin2Name))
					{
						System.out.println("These pins of component " + componentID + " are connected: " + pin1Name + " and " + pin2Name);
						for (Entry<String, String> e : pinNameRemapping.entrySet())
							if (e.getValue().equals(pin2Name))
								e.setValue(pin1Name);
						connectPinsInSupercomponents(componentID, pin1Name, pin2Name, componentsById, pinIdentifierGeneratorsPerComponentID,
								connectedInnerPinNamesPerComponentID);
						return true;
					}
				}
		}
		return false;
	}

	private static void connectPinsInSupercomponents(String componentID, String pin1Name, String pin2Name,
			Map<String, SubmodelComponentParams> componentsById, Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID,
			Map<String, Map<String, Set<String>>> connectedInnerPinNamesPerComponentID)
	{
		for (Entry<String, SubmodelComponentParams> e : componentsById.entrySet())
		{
			String superComponentID = e.getKey();
			SubmodelComponentParams superComponentJson = e.getValue();

			PinIdentifierGenerator pinIdentifierGenerator = pinIdentifierGeneratorsPerComponentID.get(superComponentID);
			Map<String, Set<String>> connectedPinNames = connectedInnerPinNamesPerComponentID.get(superComponentID);

			for (ComponentParams subcomponentParams : superComponentJson.submodel.components)
				if (subcomponentParams.id.equals(componentID))
				{
					String pin1ID = pinIdentifierGenerator.getPinID(subcomponentParams.name, pin1Name);
					String pin2ID = pinIdentifierGenerator.getPinID(subcomponentParams.name, pin2Name);

					Set<String> connectedPinNamesPin1 = connectedPinNames.get(pin1ID);
					Set<String> connectedPinNamesPin2 = connectedPinNames.get(pin2ID);

					if (connectedPinNamesPin2 != null)
					{
						connectedPinNamesPin2.remove(pin2ID);
						if (connectedPinNamesPin1 != null)
						{
							connectedPinNamesPin2.addAll(connectedPinNamesPin1);
							for (String pinNameToRewriteMapping : connectedPinNamesPin1)
								connectedPinNames.put(pinNameToRewriteMapping, connectedPinNamesPin2);
						}
					}
				}
		}
	}

	private static void generateConnectedInnerPins(Map<String, SubmodelComponentParams> componentsById,
			Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID,
			Map<String, Map<String, Set<String>>> connectedInnerPinNamesPerComponentID)
	{
		for (Entry<String, SubmodelComponentParams> e : componentsById.entrySet())
		{
			String componentID = e.getKey();
			SubmodelComponentParams componentJson = e.getValue();

			PinIdentifierGenerator pinIdentifierGenerator = new PinIdentifierGenerator();
			Map<String, Set<String>> connectedInnerPinNames = new HashMap<>();
			pinIdentifierGeneratorsPerComponentID.put(componentID, pinIdentifierGenerator);
			connectedInnerPinNamesPerComponentID.put(componentID, connectedInnerPinNames);

			for (WireParams wireJson : componentJson.submodel.wires)
			{
				String pin1Name = pinIdentifierGenerator.getPinID(wireJson.pin1);
				String pin2Name = pinIdentifierGenerator.getPinID(wireJson.pin2);

				Set<String> oldConnectedPins1 = connectedInnerPinNames.get(pin1Name);
				Set<String> oldConnectedPins2 = connectedInnerPinNames.get(pin2Name);

				if (oldConnectedPins1 == null)
					oldConnectedPins1 = Set.of(pin1Name);
				if (oldConnectedPins2 == null)
				{
					oldConnectedPins2 = new HashSet<>(Arrays.asList(pin2Name));
					connectedInnerPinNames.put(pin2Name, oldConnectedPins2);
				}

				oldConnectedPins2.addAll(oldConnectedPins1);
				for (String pinNameToRewriteMapping : oldConnectedPins1)
					connectedInnerPinNames.put(pinNameToRewriteMapping, oldConnectedPins2);
			}
		}
	}

	private static Map<String, Tuple2<List<String>, List<Integer>>> generateSortedInterfacePinNamesAndWidthesPerComponentID(
			Map<String, SubmodelComponentParams> componentsById, Map<String, Map<String, String>> combinedInterfacePinsPerComponentID)
	{
		return combinedInterfacePinsPerComponentID.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e ->
		{
			List<String> names = e.getValue().values().stream().distinct().collect(Collectors.toList());
			Map<String, Integer> widthesPerName = Arrays.stream(componentsById.get(e.getKey()).interfacePins)
					.collect(Collectors.toMap(p -> p.name, p -> p.logicWidth));
			List<Integer> widthes = names.stream().map(widthesPerName::get).collect(Collectors.toList());
			return new Tuple2<>(names, widthes);
		}));
	}

	private static void writeComponentsVerilog(Path target, Map<String, SubmodelComponentParams> componentsById,
			Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID,
			Map<String, Tuple2<List<String>, List<Integer>>> sortedInterfacePinNamesAndWidthsPerComponentID,
			Tuple2<Map<String, Map<String, String>>, Map<String, Map<String, Set<String>>>> combinedPinNames)
	{
		componentsById.forEach((componentID, componentJson) ->
		{
			try
			{
				String componentPathStr = componentID.replace(File.separator, "_").replace('.', '_');
				Path componentPathWithoutPrefix = target.resolve(componentPathStr + ".v");
				Path componentParent = componentPathWithoutPrefix.getParent();
				String componentName = componentPathWithoutPrefix.getFileName().toString();
				Files.createDirectories(componentParent);
				Files.writeString(componentParent.resolve(COMPONENT_PREFIX + componentName),
						new VerilogExporter(componentID, componentJson, pinIdentifierGeneratorsPerComponentID,
								sortedInterfacePinNamesAndWidthsPerComponentID, combinedPinNames).generateVerilog());
			}
			catch (IOException e)
			{
				throw new UncheckedIOException(e);
			}
		});
	}

	private final String componentID;
	private final SubmodelComponentParams componentJson;

	private final PinIdentifierGenerator pinIdentifierGenerator;
	private final Map<String, Set<String>> combinedInnerPinNames;
	private final List<String> sortedInterfacePinNames;
	private final Map<String, Tuple2<List<String>, List<Integer>>> sortedInterfacePinNamesAndWidthsPerComponentID;

	public VerilogExporter(String componentID, SubmodelComponentParams componentJson,
			Map<String, PinIdentifierGenerator> pinIdentifierGeneratorsPerComponentID,
			Map<String, Tuple2<List<String>, List<Integer>>> sortedInterfacePinNamesAndWidthsPerComponentID,
			Tuple2<Map<String, Map<String, String>>, Map<String, Map<String, Set<String>>>> combinedPinNames)
	{
		this.componentID = componentID;
		this.componentJson = componentJson;

		this.pinIdentifierGenerator = pinIdentifierGeneratorsPerComponentID.get(componentID);
		this.combinedInnerPinNames = combinedPinNames.e2.get(componentID);
		this.sortedInterfacePinNames = sortedInterfacePinNamesAndWidthsPerComponentID.get(componentID).e1;
		this.sortedInterfacePinNamesAndWidthsPerComponentID = sortedInterfacePinNamesAndWidthsPerComponentID;
	}

	public String generateVerilog()
	{
		StringBuilder result = new StringBuilder();

		result.append("module ");
		result.append(COMPONENT_PREFIX);
		result.append(sanitizeVerilog(componentID));

		result.append(" (");
		appendInterface(result);
		result.append(");\n\n");

		appendComponents(result);

		result.append("endmodule\n");

		return result.toString();
	}

	private void appendInterface(StringBuilder result)
	{
		if (!sortedInterfacePinNames.isEmpty())
		{
			Map<String, Integer> logicWidthsPerInterfacePinName = Arrays.stream(componentJson.interfacePins)
					.collect(Collectors.toMap(p -> p.name, p -> p.logicWidth));
			result.append('\n');
			for (int i = 0; i < sortedInterfacePinNames.size(); i++)
			{
				String interfacePinName = sortedInterfacePinNames.get(i);
				int logicWidth = logicWidthsPerInterfacePinName.get(interfacePinName);

				result.append("  input ");
				appendLogicWidth(result, logicWidth);
				result.append(sanitizeVerilog(interfacePinName));
				result.append("_pre, output ");
				appendLogicWidth(result, logicWidth);
				result.append(sanitizeVerilog(interfacePinName));
				result.append("_out, input ");
				appendLogicWidth(result, logicWidth);
				result.append(sanitizeVerilog(interfacePinName));
				result.append("_res");
				if (i != sortedInterfacePinNames.size() - 1)
					result.append(',');
				result.append('\n');
			}
		}
	}

	private void appendComponents(StringBuilder result)
	{
		Map<Set<String>, String> currentWireNamePerCombinedInnerPinNames = new HashMap<>();
		Map<Set<String>, String> resultWireNamePerCombinedInnerPinNames = new HashMap<>();
		for (Set<String> s : combinedInnerPinNames.values())
		{
			currentWireNamePerCombinedInnerPinNames.put(s, "2'b00");

			String anyInnerPinName = s.iterator().next();
			// abuse the pinIdentifierGenerator for generating an unique wire name
			String uniqueWireName = pinIdentifierGenerator.getPinID(anyInnerPinName, "res");
			resultWireNamePerCombinedInnerPinNames.put(s, uniqueWireName);
		}
		for (String interfacePinName : sortedInterfacePinNames)
		{
			String innerPinID = pinIdentifierGenerator.getPinID(SubmodelComponent.SUBMODEL_INTERFACE_NAME, interfacePinName);
			Set<String> connectedPins = combinedInnerPinNames.get(innerPinID);
			currentWireNamePerCombinedInnerPinNames.put(connectedPins, interfacePinName + "_pre");
			resultWireNamePerCombinedInnerPinNames.put(connectedPins, interfacePinName + "_res");
		}

		for (ComponentParams subcomponentParams : componentJson.submodel.components)
			appendComponent(result, currentWireNamePerCombinedInnerPinNames, resultWireNamePerCombinedInnerPinNames, subcomponentParams);

		for (String interfacePinName : sortedInterfacePinNames)
		{
			String innerPinID = pinIdentifierGenerator.getPinID(SubmodelComponent.SUBMODEL_INTERFACE_NAME, interfacePinName);
			Set<String> connectedPins = combinedInnerPinNames.get(innerPinID);
			String lastWireName = currentWireNamePerCombinedInnerPinNames.remove(connectedPins);

			result.append("assign ");
			result.append(sanitizeVerilog(interfacePinName));
			result.append("_out");
			result.append(" = ");
			result.append(sanitizeVerilog(lastWireName));
			result.append(";\n");
		}
		for (Set<String> s : currentWireNamePerCombinedInnerPinNames.keySet())
		{
			String lastWireName = currentWireNamePerCombinedInnerPinNames.get(s);
			String resultWireName = resultWireNamePerCombinedInnerPinNames.get(s);

			result.append("wire ");
			int logicWidth = -1;
			outer: for (ComponentParams subcomponentJson : componentJson.submodel.components)
			{
				Tuple2<List<String>, List<Integer>> subcomponentInterfacePinNamesAndWidths = getSubcomponentInterfacePinNamesAndWidths(
						subcomponentJson.id, subcomponentJson.params);
				List<String> subcomponentInterfacePinNames = subcomponentInterfacePinNamesAndWidths.e1;
				List<Integer> subcomponentInterfacePinWidths = subcomponentInterfacePinNamesAndWidths.e2;
				for (int i = 0; i < subcomponentInterfacePinNames.size(); i++)
					if (s.contains(pinIdentifierGenerator.getPinID(subcomponentJson.name, subcomponentInterfacePinNames.get(i))))
					{
						logicWidth = subcomponentInterfacePinWidths.get(i);
						break outer;
					}
			}
			appendLogicWidth(result, logicWidth);
			result.append(sanitizeVerilog(resultWireName));
			result.append(";\n");

			result.append("assign ");
			result.append(sanitizeVerilog(resultWireName));
			result.append(" = ");
			result.append(sanitizeVerilog(lastWireName));
			result.append(";\n");
		}
	}

	private void appendComponent(StringBuilder result, Map<Set<String>, String> currentWireNamePerCombinedInnerPinNames,
			Map<Set<String>, String> resultWireNamePerCombinedInnerPinNames, ComponentParams subcomponentParams)
	{
		{
			String subcomponentID = subcomponentParams.id;
			String subcomponentName = subcomponentParams.name;

			Tuple2<List<String>, List<Integer>> subcomponentInterfacePinNamesAndWidths = getSubcomponentInterfacePinNamesAndWidths(
					subcomponentID, subcomponentParams.params);
			List<String> subcomponentInterfacePinNames = subcomponentInterfacePinNamesAndWidths.e1;
			List<Integer> subcomponentInterfacePinWidths = subcomponentInterfacePinNamesAndWidths.e2;
			for (int i = 0; i < subcomponentInterfacePinNames.size(); i++)
			{
				result.append("wire ");
				appendLogicWidth(result, subcomponentInterfacePinWidths.get(i));
				result.append(pinIdentifierGenerator.getPinID(subcomponentName, subcomponentInterfacePinNames.get(i)));
				result.append(";\n");
			}

			result.append(COMPONENT_PREFIX);
			String paramsString = subcomponentParams.params == JsonNull.INSTANCE ? "" : subcomponentParams.params.toString();
			result.append(sanitizeVerilog(subcomponentID + paramsString));
			result.append(" (");
			for (int i = 0; i < subcomponentInterfacePinNames.size(); i++)
			{
				String innerPinID = pinIdentifierGenerator.getPinID(subcomponentName, subcomponentInterfacePinNames.get(i));

				String lastWireName;
				String nextWireName = innerPinID;
				String resultWireName;
				Set<String> combinedInnerPinsGroup = combinedInnerPinNames.get(innerPinID);
				if (combinedInnerPinsGroup != null)
				{
					lastWireName = currentWireNamePerCombinedInnerPinNames.get(combinedInnerPinsGroup);
					resultWireName = resultWireNamePerCombinedInnerPinNames.get(combinedInnerPinsGroup);

					currentWireNamePerCombinedInnerPinNames.put(combinedInnerPinsGroup, nextWireName);
				} else
				{
					lastWireName = "2'b00";
					resultWireName = nextWireName;
				}

				result.append(sanitizeVerilog(lastWireName));
				result.append(", ");
				result.append(sanitizeVerilog(nextWireName));
				result.append(", ");
				result.append(sanitizeVerilog(resultWireName));
				if (i != subcomponentInterfacePinNames.size() - 1)
					result.append(", \n  ");
			}
			result.append(");\n\n");
		}
	}

	private Tuple2<List<String>, List<Integer>> getSubcomponentInterfacePinNamesAndWidths(String subcomponentID,
			JsonElement subcomponentParams)
	{
		Tuple2<List<String>, List<Integer>> result = sortedInterfacePinNamesAndWidthsPerComponentID.get(subcomponentID);
		if (result != null)
			return result;

		Map<String, Pin> pins = IndirectModelComponentCreator
				.createComponent(new LogicModelModifiable(), subcomponentID, subcomponentParams).getPins();
		List<String> names = pins.keySet().stream().sorted().collect(Collectors.toList());
		List<Integer> widthes = pins.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey())).map(Entry::getValue)
				.map(p -> p.logicWidth).collect(Collectors.toList());
		System.out.println("Assuming following order for interface pins of " + subcomponentID + ": " + names);
		return new Tuple2<>(names, widthes);
	}

	private static void appendLogicWidth(StringBuilder result, int logicWidth)
	{
		result.append('[');
		String logicWidthStr = Integer.toString(logicWidth * 2 - 1);
		for (int spaces = logicWidthStr.length(); spaces < 3; spaces++)
			result.append(' ');
		result.append(logicWidthStr);
		result.append(":0] ");
	}

	private static class PinIdentifierGenerator
	{
		private final Map<String, Map<String, String>> wireNamesPerPinAndComponentName;
		private final Set<String> usedWireNames;

		public PinIdentifierGenerator()
		{
			wireNamesPerPinAndComponentName = new HashMap<>();
			usedWireNames = new HashSet<>();
		}

		private String getPinID(PinParams pin)
		{
			return getPinID(pin.compName, pin.pinName);
		}

		private String getPinID(String component, String pin)
		{
			String componentSan = sanitizeVerilog(component);
			String pinSan = sanitizeVerilog(pin);

			Map<String, String> wireNamesPerPinName = wireNamesPerPinAndComponentName.computeIfAbsent(componentSan, k -> new HashMap<>());

			if (wireNamesPerPinName.containsKey(pinSan))
				return wireNamesPerPinName.get(pinSan);

			String baseName = componentSan + '_' + pinSan;
			String combinedName;
			if (usedWireNames.add(baseName))
				combinedName = baseName;
			else
			{
				int i = 0;
				do
					combinedName = baseName + "#" + i++;
				while (!usedWireNames.add(combinedName));
			}
			wireNamesPerPinName.put(pinSan, combinedName);
			return combinedName;
		}
	}

	private static class Tuple2<E1, E2>
	{
		public final E1 e1;
		public final E2 e2;

		public Tuple2(E1 e1, E2 e2)
		{
			this.e1 = e1;
			this.e2 = e2;
		}
	}

	private static String sanitizeVerilog(String str)
	{
		return str.replace('#', '_').replace('+', '_').replace('-', '_').replace('=', '_').replace('{', '_').replace('}', '_')
				.replace(':', '_').replace('"', '_').replace(',', '_').replace('[', '_').replace(']', '_').replace(' ', '_');
	}
}