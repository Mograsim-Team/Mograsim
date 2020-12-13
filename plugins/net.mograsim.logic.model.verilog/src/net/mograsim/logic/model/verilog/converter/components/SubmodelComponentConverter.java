package net.mograsim.logic.model.verilog.converter.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelSplitter;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.verilog.converter.ComponentConversionResult;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogComponentDeclarationMapping;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogConverter;
import net.mograsim.logic.model.verilog.converter.PinBit;
import net.mograsim.logic.model.verilog.converter.PinNameBit;
import net.mograsim.logic.model.verilog.converter.VerilogEmulatedModelPin;
import net.mograsim.logic.model.verilog.converter.VerilogEmulatedModelPin.Type;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.expressions.Constant;
import net.mograsim.logic.model.verilog.model.expressions.Expression;
import net.mograsim.logic.model.verilog.model.expressions.SignalReference;
import net.mograsim.logic.model.verilog.model.signals.IOPort;
import net.mograsim.logic.model.verilog.model.signals.Input;
import net.mograsim.logic.model.verilog.model.signals.Output;
import net.mograsim.logic.model.verilog.model.signals.Signal;
import net.mograsim.logic.model.verilog.model.signals.Wire;
import net.mograsim.logic.model.verilog.model.statements.Assign;
import net.mograsim.logic.model.verilog.model.statements.ComponentReference;
import net.mograsim.logic.model.verilog.model.statements.Statement;
import net.mograsim.logic.model.verilog.model.statements.WireDeclaration;
import net.mograsim.logic.model.verilog.utils.IdentifierGenerator;
import net.mograsim.logic.model.verilog.utils.UnionFind;

public class SubmodelComponentConverter implements ComponentConverter<SubmodelComponent>
{
	private final ModelComponentToVerilogConverter converter;

	public SubmodelComponentConverter(ModelComponentToVerilogConverter converter)
	{
		this.converter = converter;
	}

	@Override
	public ComponentConversionResult convert(SubmodelComponent modelComponent, String modelID, JsonElement params, String verilogID)
	{
		for (ModelComponent subcomponent : modelComponent.submodel.getComponentsByName().values())
			if (!subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				converter.convert(subcomponent);

		UnionFind<PinBit> connectedPins = findConnectedPins(modelComponent);

		ModelComponentToVerilogComponentDeclarationMapping mapping = mapDeclaration(modelComponent, connectedPins, modelID, params,
				verilogID);

		VerilogComponentImplementation implementation = mapImplementation(modelComponent, connectedPins, mapping);

		return new ComponentConversionResult(mapping, implementation);
	}

	private UnionFind<PinBit> findConnectedPins(SubmodelComponent modelComponent)
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
			} else if (!(subcomponent instanceof ModelWireCrossPoint)
					&& !subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
			{
				ModelComponentToVerilogComponentDeclarationMapping subcomponentMapping = converter.getComponentMapping(subcomponent);
				for (Set<PinNameBit> connected : subcomponentMapping.getInternallyConnectedPins())
					connectedPins.unionAll(connected.stream().map(p -> p.toPinBit(subcomponent)).collect(Collectors.toList()));
			}

		return connectedPins;
	}

	private static ModelComponentToVerilogComponentDeclarationMapping mapDeclaration(SubmodelComponent modelComponent,
			UnionFind<PinBit> connectedPins, String modelID, JsonElement params, String verilogID)
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

		return generateCanonicalDeclarationMapping(modelComponent, connectedPinsByName, modelID, params, verilogID);
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
		Map<PinBit, Expression> currentPreExprs = new HashMap<>();
		Map<PinBit, Signal> finalOutSignals = new HashMap<>();
		Map<PinBit, Expression> resExprs = new HashMap<>();
		for (Set<PinNameBit> connectedPinGroup : declarationMapping.getInternallyConnectedPins())
		{
			PinNameBit pinnamebit = connectedPinGroup.iterator().next();
			PinBit root = connectedPins.find(pinnamebit.toSubmodelPinBit(modelComponent));
			resExprs.put(root, new SignalReference(declarationMapping.getResPinMapping().get(pinnamebit).getVerilogPort()));
			finalOutSignals.put(root, declarationMapping.getOutPinMapping().get(pinnamebit).getVerilogPort());
			currentPreExprs.put(root, new SignalReference(declarationMapping.getPrePinMapping().get(pinnamebit).getVerilogPort()));
		}

		IdentifierGenerator idGen = ModelComponentToVerilogConverter
				.generateIdentifierGenerator(declarationMapping.getVerilogComponentDeclaration());
		List<Statement> statements = new ArrayList<>();
		for (ModelComponent subcomponent : modelComponent.submodel.getComponentsByName().values())
		{
			// TODO do we really want to use instanceof?
			if (subcomponent instanceof ModelSplitter || subcomponent instanceof ModelWireCrossPoint
					|| subcomponent.getName().equals(SubmodelComponent.SUBMODEL_INTERFACE_NAME))
				continue;

			String subcomponentVerilogName = idGen.generateID(subcomponent.getName());
			ModelComponentToVerilogComponentDeclarationMapping subcomponentMapping = converter.getComponentMapping(subcomponent);
			int parameterCount = subcomponentMapping.getVerilogComponentDeclaration().getIOPorts().size();
			List<Expression> arguments = new ArrayList<>(parameterCount);
			for (int i = 0; i < parameterCount; i++)
				arguments.add(null);
			for (Pin pin : subcomponent.getPins().values())
				for (int bit = 0; bit < pin.logicWidth; bit++)
				{
					PinBit pinbit = new PinBit(pin, bit);
					PinBit root = connectedPins.find(pinbit);
					Wire outSignal = new Wire(idGen.generateID(subcomponentVerilogName + "_" + pin.name + "_" + bit), 2);
					statements.add(new WireDeclaration(outSignal));
					Expression preExpr = currentPreExprs.put(root, new SignalReference(outSignal));
					Expression outExpr = new SignalReference(outSignal);
					Expression resExpr = resExprs.get(root);
					if (resExpr == null)
					{
						preExpr = new Constant(BitVector.of(Bit.ZERO, 2));
						Wire resWire = new Wire(idGen.generateID(subcomponentVerilogName + "_" + pin.name + "_" + bit + "_res"), 2);
						resExpr = new SignalReference(resWire);
						statements.add(new WireDeclaration(resWire));
						finalOutSignals.put(root, resWire);
						resExprs.put(root, resExpr);
					}
					PinNameBit pinnamebit = pinbit.toPinNameBit();
					arguments.set(subcomponentMapping.getPrePinMapping().get(pinnamebit).getPortIndex(), preExpr);
					arguments.set(subcomponentMapping.getOutPinMapping().get(pinnamebit).getPortIndex(), outExpr);
					arguments.set(subcomponentMapping.getResPinMapping().get(pinnamebit).getPortIndex(), resExpr);
				}
			statements
					.add(new ComponentReference(subcomponentVerilogName, subcomponentMapping.getVerilogComponentDeclaration(), arguments));
		}

		for (Entry<PinBit, Signal> e : finalOutSignals.entrySet())
			statements.add(new Assign(e.getValue(), currentPreExprs.get(e.getKey())));

		return new VerilogComponentImplementation(declarationMapping.getVerilogComponentDeclaration(), statements);
	}
}
