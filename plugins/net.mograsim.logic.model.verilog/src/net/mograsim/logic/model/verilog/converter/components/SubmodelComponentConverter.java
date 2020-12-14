package net.mograsim.logic.model.verilog.converter.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.expressions.Constant;
import net.mograsim.logic.model.verilog.model.expressions.Expression;
import net.mograsim.logic.model.verilog.model.expressions.SignalReference;
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

		return ModelComponentToVerilogConverter.generateCanonicalDeclarationMapping(modelComponent, connectedPinsByName, modelID, params,
				verilogID);
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
			for (Set<PinNameBit> connectedGroup : subcomponentMapping.getInternallyConnectedPins())
			{
				PinNameBit pinnamebit = connectedGroup.iterator().next();
				String pinBaseName = subcomponentVerilogName + "_" + pinnamebit.getName() + "_" + pinnamebit.getBit();
				PinBit root = connectedPins.find(pinnamebit.toPinBit(subcomponent));
				Wire outSignal = new Wire(idGen.generateID(pinBaseName), 2);
				statements.add(new WireDeclaration(outSignal));
				Expression preExpr = currentPreExprs.put(root, new SignalReference(outSignal));
				Expression outExpr = new SignalReference(outSignal);
				Expression resExpr = resExprs.get(root);
				if (resExpr == null)
				{
					preExpr = new Constant(BitVector.of(Bit.ZERO, 2));
					Wire resWire = new Wire(idGen.generateID(pinBaseName + "_res"), 2);
					resExpr = new SignalReference(resWire);
					statements.add(new WireDeclaration(resWire));
					finalOutSignals.put(root, resWire);
					resExprs.put(root, resExpr);
				}
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
