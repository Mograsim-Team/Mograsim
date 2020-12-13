package net.mograsim.logic.model.verilog.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelSplitter;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.verilog.converter.VerilogEmulatedModelPin.Type;
import net.mograsim.logic.model.verilog.helper.IdentifierGenerator;
import net.mograsim.logic.model.verilog.helper.UnionFind;
import net.mograsim.logic.model.verilog.model.Assign;
import net.mograsim.logic.model.verilog.model.ComponentReference;
import net.mograsim.logic.model.verilog.model.Constant;
import net.mograsim.logic.model.verilog.model.IOPort;
import net.mograsim.logic.model.verilog.model.Input;
import net.mograsim.logic.model.verilog.model.NamedSignal;
import net.mograsim.logic.model.verilog.model.Output;
import net.mograsim.logic.model.verilog.model.Signal;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.Wire;

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

	private void convert(ModelComponent modelComponent)
	{
		String modelID = modelComponent.getIDForSerializing(new IdentifyParams());
		JsonElement params = modelComponent.getParamsForSerializingJSON(new IdentifyParams());
		if (componentMappingsPerModelIDPerParams.getOrDefault(modelID, Map.of()).containsKey(params))
			// we already converted that component, or it was specified externally
			return;

		if (!(modelComponent instanceof SubmodelComponent))
			throw new IllegalArgumentException(
					"Can only convert SubmodelComponents, tried to convert " + modelID + " with params " + params);
		SubmodelComponent modelComponentC = (SubmodelComponent) modelComponent;

		UnionFind<PinBit> connectedPins = findConnectedPins(modelComponentC);

		ModelComponentToVerilogComponentDeclarationMapping mapping = mapDeclaration(modelComponentC, connectedPins, modelID, params);
		componentMappingsPerModelIDPerParams.computeIfAbsent(modelID, i -> new HashMap<>()).put(params, mapping);

		for (ModelComponent subcomponent : modelComponentC.submodel.getComponentsByName().values())
			if (!(subcomponent instanceof ModelSplitter) && !subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				convert(subcomponent);

		verilogComponents.add(mapImplementation(modelComponentC, connectedPins, mapping));
	}

	private static UnionFind<PinBit> findConnectedPins(SubmodelComponent modelComponent)
	{
		UnionFind<PinBit> connectedPins = new UnionFind<>();
		for (ModelWire w : modelComponent.submodel.getWiresByName().values())
			for (int bit = 0; bit < w.getPin1().logicWidth; bit++)
				connectedPins.union(new PinBit(w.getPin1(), bit), new PinBit(w.getPin2(), bit));

		for (ModelComponent subcomponent : modelComponent.submodel.getComponentsByName().values())
			if (subcomponent instanceof ModelSplitter)
			{
				ModelSplitter splitter = (ModelSplitter) subcomponent;
				for (int bit = 0; bit < splitter.logicWidth; bit++)
					connectedPins.union(new PinBit(splitter.getInputPin(), bit), new PinBit(splitter.getOutputPin(bit), 0));
			}

		// TODO connected pins of subcomponents

		return connectedPins;
	}

	private ModelComponentToVerilogComponentDeclarationMapping mapDeclaration(SubmodelComponent modelComponent,
			UnionFind<PinBit> connectedPins, String modelID, JsonElement params)
	{
		// TODO this is probably slow
		Map<PinBit, PinNameBit> representantMapping = new HashMap<>();
		UnionFind<PinNameBit> connectedPinsByName = new UnionFind<>();
		for (Pin p : modelComponent.getSubmodelPins().values())
			for (int bit = 0; bit < p.logicWidth; bit++)
			{
				PinNameBit pinnamebit = new PinNameBit(p.name, bit);
				PinNameBit representative = representantMapping.computeIfAbsent(connectedPins.find(new PinBit(p, bit)), q -> pinnamebit);
				connectedPinsByName.union(pinnamebit, representative);
			}

		return generateCanonicalDeclarationMapping(modelComponent, connectedPinsByName, modelID, params,
				verilogComponentIDGen.generateID(verilogComponentIDPrefix + modelID + (params.isJsonNull() ? "" : "_" + params)));
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

	private VerilogComponentImplementation mapImplementation(SubmodelComponent modelComponent, UnionFind<PinBit> connectedPins,
			ModelComponentToVerilogComponentDeclarationMapping declarationMapping)
	{
		Map<PinBit, Signal> currentPreSignals = new HashMap<>();
		Map<PinBit, NamedSignal> finalOutSignals = new HashMap<>();
		Map<PinBit, NamedSignal> resSignals = new HashMap<>();
		for (Pin submodelPin : modelComponent.getSubmodelPins().values())
			for (int bit = 0; bit < submodelPin.logicWidth; bit++)
			{
				PinBit pinbit = new PinBit(submodelPin, bit);
				PinNameBit pinnamebit = pinbit.toPinNameBit();
				PinBit root = connectedPins.find(pinbit);
				resSignals.put(root, declarationMapping.getResPinMapping().get(pinnamebit).getVerilogPort());
				finalOutSignals.put(root, declarationMapping.getOutPinMapping().get(pinnamebit).getVerilogPort());
				Signal prePort = declarationMapping.getPrePinMapping().get(pinnamebit).getVerilogPort();
				Signal previousPrePort = currentPreSignals.put(root, prePort);
				assert previousPrePort != null && !previousPrePort.equals(prePort);
			}

		IdentifierGenerator idGen = new IdentifierGenerator(
				declarationMapping.getVerilogComponentDeclaration().getIOPorts().stream().map(IOPort::getName).collect(Collectors.toList()),
				ModelComponentToVerilogConverter::sanitizeVerilogID);
		Set<Wire> internalWires = new HashSet<>();
		Set<ComponentReference> subcomponents = new HashSet<>();
		for (ModelComponent subcomponent : modelComponent.submodel.getComponentsByName().values())
		{
			// TODO do we really want to use instanceof?
			if (subcomponent instanceof ModelSplitter || subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				continue;

			String subcomponentVerilogName = idGen.generateID(subcomponent.getName());
			ModelComponentToVerilogComponentDeclarationMapping subcomponentMapping = componentMappingsPerModelIDPerParams
					.get(subcomponent.getIDForSerializing(new IdentifyParams()))
					.get(subcomponent.getParamsForSerializingJSON(new IdentifyParams()));
			int parameterCount = subcomponentMapping.getVerilogComponentDeclaration().getIOPorts().size();
			List<Signal> arguments = new ArrayList<>(parameterCount);
			for (int i = 0; i < parameterCount; i++)
				arguments.add(null);
			for (Pin pin : subcomponent.getPins().values())
				for (int bit = 0; bit < pin.logicWidth; bit++)
				{
					PinBit pinbit = new PinBit(pin, bit);
					PinBit root = connectedPins.find(pinbit);
					Wire outSignal = new Wire(idGen.generateID(subcomponentVerilogName + "_" + pin.name + "_" + bit), 2);
					internalWires.add(outSignal);
					Signal preSignal = currentPreSignals.put(root, outSignal);
					Signal resSignal = resSignals.get(root);
					if (resSignal == null)
					{
						preSignal = new Constant(BitVector.of(Bit.ZERO, 2));
						Wire resWire = new Wire(idGen.generateID(subcomponentVerilogName + "_" + pin.name + "_" + bit + "_res"), 2);
						resSignal = resWire;
						internalWires.add(resWire);
						finalOutSignals.put(root, resWire);
						resSignals.put(root, resWire);
					}
					PinNameBit pinnamebit = pinbit.toPinNameBit();
					arguments.set(subcomponentMapping.getPrePinMapping().get(pinnamebit).getPortIndex(), preSignal);
					arguments.set(subcomponentMapping.getOutPinMapping().get(pinnamebit).getPortIndex(), outSignal);
					arguments.set(subcomponentMapping.getResPinMapping().get(pinnamebit).getPortIndex(), resSignal);
				}
			subcomponents
					.add(new ComponentReference(subcomponentVerilogName, subcomponentMapping.getVerilogComponentDeclaration(), arguments));
		}

		Set<Assign> assigns = new HashSet<>();
		for (Entry<PinBit, NamedSignal> e : finalOutSignals.entrySet())
			assigns.add(new Assign(currentPreSignals.get(e.getKey()), e.getValue()));

		return new VerilogComponentImplementation(declarationMapping.getVerilogComponentDeclaration(), internalWires, assigns,
				subcomponents);
	}

	private Set<VerilogComponentImplementation> getVerilogComponents()
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

	public static String sanitizeVerilogID(String id)
	{
		return (id.matches("[0-9].*") ? "_" + id : id).replaceAll("[^A-Za-z0-9_]", "_");
	}
}
