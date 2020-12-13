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
import net.mograsim.logic.model.verilog.helper.UnionFind.UnionFindElement;
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

		ModelComponentToVerilogComponentDeclarationMapping mapping = mapDeclaration(modelComponentC, modelID, params);
		componentMappingsPerModelIDPerParams.computeIfAbsent(modelID, i -> new HashMap<>()).put(params, mapping);

		for (ModelComponent subcomponent : modelComponentC.submodel.getComponentsByName().values())
			if (!subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				convert(subcomponent);

		verilogComponents.add(mapImplementation(modelComponentC, mapping));
	}

	private ModelComponentToVerilogComponentDeclarationMapping mapDeclaration(ModelComponent modelComponent, String modelID,
			JsonElement params)
	{
		return generateCanonicalDeclarationMapping(modelComponent, modelID, params,
				verilogComponentIDGen.generateID(verilogComponentIDPrefix + modelID + (params.isJsonNull() ? "" : "_" + params)));
	}

	public static ModelComponentToVerilogComponentDeclarationMapping generateCanonicalDeclarationMapping(ModelComponent modelComponent,
			String modelID, JsonElement params, String verilogID)
	{
		IdentifierGenerator ioPortIDGen = new IdentifierGenerator(ModelComponentToVerilogConverter::sanitizeVerilogID);
		List<IOPort> ioPorts = new ArrayList<>();
		Set<VerilogEmulatedModelPin> pinMapping = new HashSet<>();
		for (Pin modelPin : modelComponent.getPins().values())
			for (int bit = 0; bit < modelPin.logicWidth; bit++)
			{
				addPinMapping(ioPortIDGen, ioPorts, pinMapping, modelPin, bit, Input::new, Type.PRE, "pre");
				addPinMapping(ioPortIDGen, ioPorts, pinMapping, modelPin, bit, Output::new, Type.OUT, "out");
				addPinMapping(ioPortIDGen, ioPorts, pinMapping, modelPin, bit, Input::new, Type.RES, "res");
			}

		VerilogComponentDeclaration declaration = new VerilogComponentDeclaration(verilogID, ioPorts);
		return new ModelComponentToVerilogComponentDeclarationMapping(modelID, params, declaration, pinMapping);
	}

	private static void addPinMapping(IdentifierGenerator ioPortIDGen, List<IOPort> ioPorts, Set<VerilogEmulatedModelPin> pinMapping,
			Pin modelPin, int bit, BiFunction<String, Integer, IOPort> constr, Type type, String suffix)
	{
		String portID = ioPortIDGen.generateID(modelPin.name + "_" + bit + "_" + suffix);
		IOPort ioPort = constr.apply(portID, 2);
		int index = ioPorts.size();
		ioPorts.add(ioPort);
		pinMapping.add(new VerilogEmulatedModelPin(ioPort, index, new PinNameBit(modelPin.name, bit), type));
	}

	private VerilogComponentImplementation mapImplementation(SubmodelComponent modelComponent,
			ModelComponentToVerilogComponentDeclarationMapping declarationMapping)
	{
		UnionFind<PinBit> connectedPins = new UnionFind<>();
		for (ModelWire w : modelComponent.submodel.getWiresByName().values())
			for (int bit = 0; bit < w.getPin1().logicWidth; bit++)
				connectedPins.union(new PinBit(w.getPin1(), bit), new PinBit(w.getPin2(), bit));

		Map<UnionFindElement<PinBit>, Signal> currentPreSignals = new HashMap<>();
		Map<UnionFindElement<PinBit>, NamedSignal> finalOutSignals = new HashMap<>();
		Map<UnionFindElement<PinBit>, NamedSignal> resSignals = new HashMap<>();
		for (Pin submodelPin : modelComponent.getSubmodelPins().values())
			for (int bit = 0; bit < submodelPin.logicWidth; bit++)
			{
				PinBit pinbit = new PinBit(submodelPin, bit);
				PinNameBit pinnamebit = pinbit.toPinNameBit();
				UnionFindElement<PinBit> root = UnionFind.find(connectedPins.getElement(pinbit));
				resSignals.put(root, declarationMapping.getResPinMapping().get(pinnamebit).getVerilogPort());
				finalOutSignals.put(root, declarationMapping.getOutPinMapping().get(pinnamebit).getVerilogPort());
				Signal prePort = declarationMapping.getPrePinMapping().get(pinnamebit).getVerilogPort();
				Signal previousPrePort = currentPreSignals.put(root, prePort);
				if (previousPrePort != null)
					// TODO implement this
					throw new IllegalArgumentException("Can't convert components with connected pins");
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
					UnionFindElement<PinBit> root = UnionFind.find(connectedPins.getElement(pinbit));
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
		for (Entry<UnionFindElement<PinBit>, NamedSignal> e : finalOutSignals.entrySet())
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
