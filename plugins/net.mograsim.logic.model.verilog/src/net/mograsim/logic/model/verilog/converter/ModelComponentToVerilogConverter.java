package net.mograsim.logic.model.verilog.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelSplitter;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.verilog.converter.VerilogEmulatedModelPin.Type;
import net.mograsim.logic.model.verilog.converter.components.SubmodelComponentConverter;
import net.mograsim.logic.model.verilog.converter.components.TriStateBufferConverter;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.signals.IOPort;
import net.mograsim.logic.model.verilog.model.signals.Input;
import net.mograsim.logic.model.verilog.model.signals.Output;
import net.mograsim.logic.model.verilog.utils.IdentifierGenerator;
import net.mograsim.logic.model.verilog.utils.UnionFind;

public class ModelComponentToVerilogConverter
{
	private final String verilogComponentIDPrefix;
	private final Map<String, Map<JsonElement, ModelComponentToVerilogComponentDeclarationMapping>> componentMappingsPerModelIDPerParams;
	private final Set<VerilogComponentImplementation> verilogComponents;
	private final IdentifierGenerator verilogComponentIDGen;

	public ModelComponentToVerilogConverter(String verilogComponentIDPrefix,
			Set<ModelComponentToVerilogComponentDeclarationMapping> atomicComponentMappings)
	{
		this.verilogComponentIDPrefix = verilogComponentIDPrefix;
		this.componentMappingsPerModelIDPerParams = new HashMap<>(atomicComponentMappings.stream().collect(Collectors
				.groupingBy(m -> m.getModelComponentID(), Collectors.toMap(m -> m.getModelComponentParams(), Function.identity()))));
		this.verilogComponents = new HashSet<>();
		this.verilogComponentIDGen = new IdentifierGenerator(
				componentMappingsPerModelIDPerParams.values().stream().map(Map::values).flatMap(Collection::stream)
						.map(ModelComponentToVerilogComponentDeclarationMapping::getVerilogComponentDeclaration)
						.map(VerilogComponentDeclaration::getID).collect(Collectors.toSet()),
				ModelComponentToVerilogConverter::sanitizeVerilogID);
	}

	public void convert(ModelComponent modelComponent)
	{
		// these are handled elsewhere
		if (modelComponent instanceof ModelSplitter || modelComponent instanceof ModelWireCrossPoint)
			return;

		String modelID = modelComponent.getIDForSerializing(new IdentifyParams());
		JsonElement params = modelComponent.getParamsForSerializingJSON(new IdentifyParams());
		if (componentMappingsPerModelIDPerParams.getOrDefault(modelID, Map.of()).containsKey(params))
			// we already converted that component, or it was specified externally
			return;

		String verilogID = verilogComponentIDGen.generateID(verilogComponentIDPrefix + modelID + (params.isJsonNull() ? "" : "_" + params));

		ComponentConversionResult result;
		// TODO don't rely on instanceof
		// TODO improve!
		if (modelComponent instanceof SubmodelComponent)
			result = new SubmodelComponentConverter(this).convert((SubmodelComponent) modelComponent, modelID, params, verilogID);
		else if (modelComponent instanceof ModelTriStateBuffer)
			result = new TriStateBufferConverter().convert((ModelTriStateBuffer) modelComponent, modelID, params, verilogID);
		else
			throw new IllegalArgumentException(
					"Can only convert SubmodelComponents, tried to convert " + modelID + " with params " + params);

		componentMappingsPerModelIDPerParams.computeIfAbsent(modelID, i -> new HashMap<>()).put(params, result.getMapping());
		verilogComponents.add(result.getImplementation());
	}

	public static ModelComponentToVerilogComponentDeclarationMapping generateCanonicalDeclarationMapping(ModelComponent modelComponent,
			UnionFind<PinNameBit> connectedPins, String modelID, JsonElement params, String verilogID)
	{
		IdentifierGenerator ioPortIDGen = new IdentifierGenerator(ModelComponentToVerilogConverter::sanitizeVerilogID);
		List<IOPort> ioPorts = new ArrayList<>();
		Map<Type, Map<PinNameBit, VerilogEmulatedModelPinBuilder>> pinMapping = new HashMap<>();
		for (Type t : Type.values())
			pinMapping.put(t, new HashMap<>());
		for (Pin modelPin : modelComponent.getPins().values())
			for (int bit = 0; bit < modelPin.logicWidth; bit++)
			{
				PinNameBit pinbit = new PinNameBit(modelPin.name, bit);
				addPinMapping(ioPortIDGen, ioPorts, connectedPins, pinMapping, pinbit, Input::new, Type.PRE, "pre");
				addPinMapping(ioPortIDGen, ioPorts, connectedPins, pinMapping, pinbit, Output::new, Type.OUT, "out");
				addPinMapping(ioPortIDGen, ioPorts, connectedPins, pinMapping, pinbit, Input::new, Type.RES, "res");
			}

		VerilogComponentDeclaration declaration = new VerilogComponentDeclaration(verilogID, ioPorts);
		Set<VerilogEmulatedModelPin> finalPinMapping = pinMapping.values().stream().map(Map::values).flatMap(Collection::stream)
				.map(VerilogEmulatedModelPinBuilder::build).collect(Collectors.toSet());
		return new ModelComponentToVerilogComponentDeclarationMapping(modelID, params, declaration, finalPinMapping);
	}

	private static void addPinMapping(IdentifierGenerator ioPortIDGen, List<IOPort> ioPorts, UnionFind<PinNameBit> connectedPins,
			Map<Type, Map<PinNameBit, VerilogEmulatedModelPinBuilder>> pinMapping, PinNameBit pinbit,
			BiFunction<String, Integer, IOPort> constr, Type type, String suffix)
	{
		Map<PinNameBit, VerilogEmulatedModelPinBuilder> pinMappingCorrectType = pinMapping.get(type);
		pinMappingCorrectType.computeIfAbsent(connectedPins.find(pinbit), p ->
		{
			String portID = ioPortIDGen.generateID(p.getName() + "_" + p.getBit() + "_" + suffix);
			IOPort ioPort = constr.apply(portID, 2);
			int index = ioPorts.size();
			ioPorts.add(ioPort);
			return new VerilogEmulatedModelPinBuilder(ioPort, index, type);
		}).addPinbit(pinbit);
	}

	private static class VerilogEmulatedModelPinBuilder
	{
		private final IOPort verilogPort;
		private final int portIndex;
		private final Set<PinNameBit> pinbits;
		private final Type type;

		public VerilogEmulatedModelPinBuilder(IOPort verilogPort, int portIndex, Type type)
		{
			this.verilogPort = verilogPort;
			this.portIndex = portIndex;
			this.pinbits = new HashSet<>();
			this.type = type;
		}

		public void addPinbit(PinNameBit pinbit)
		{
			pinbits.add(pinbit);
		}

		public VerilogEmulatedModelPin build()
		{
			return new VerilogEmulatedModelPin(verilogPort, portIndex, pinbits, type);
		}
	}

	public ModelComponentToVerilogComponentDeclarationMapping getComponentMapping(ModelComponent component)
	{
		ModelComponentToVerilogComponentDeclarationMapping subcomponentMapping = componentMappingsPerModelIDPerParams
				.get(component.getIDForSerializing(new IdentifyParams())).get(component.getParamsForSerializingJSON(new IdentifyParams()));
		return subcomponentMapping;
	}

	public Set<VerilogComponentImplementation> getVerilogComponents()
	{
		return verilogComponents;
	}

	public static Set<VerilogComponentImplementation> convert(
			Set<ModelComponentToVerilogComponentDeclarationMapping> atomicComponentMappings, Set<ModelComponent> components,
			String verilogComponentIDPrefix)
	{
		ModelComponentToVerilogConverter converter = new ModelComponentToVerilogConverter(verilogComponentIDPrefix,
				atomicComponentMappings);
		for (ModelComponent modelComponent : components)
			converter.convert(modelComponent);
		return converter.getVerilogComponents();
	}

	public static IdentifierGenerator generateIdentifierGenerator(VerilogComponentDeclaration declaration)
	{
		IdentifierGenerator idGen = new IdentifierGenerator(
				declaration.getIOPorts().stream().map(IOPort::getName).collect(Collectors.toList()),
				ModelComponentToVerilogConverter::sanitizeVerilogID);
		return idGen;
	}

	public static String sanitizeVerilogID(String id)
	{
		return (id.matches("[0-9].*") ? "_" + id : id).replaceAll("[^A-Za-z0-9_]", "_");
	}
}
