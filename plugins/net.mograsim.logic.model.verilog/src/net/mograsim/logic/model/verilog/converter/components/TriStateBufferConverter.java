package net.mograsim.logic.model.verilog.converter.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.verilog.converter.ComponentConversionResult;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogComponentDeclarationMapping;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogConverter;
import net.mograsim.logic.model.verilog.converter.PinNameBit;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.expressions.Constant;
import net.mograsim.logic.model.verilog.model.expressions.Expression;
import net.mograsim.logic.model.verilog.model.expressions.SignalReference;
import net.mograsim.logic.model.verilog.model.signals.IOPort;
import net.mograsim.logic.model.verilog.model.signals.Input;
import net.mograsim.logic.model.verilog.model.signals.Output;
import net.mograsim.logic.model.verilog.model.signals.Wire;
import net.mograsim.logic.model.verilog.model.statements.Assign;
import net.mograsim.logic.model.verilog.model.statements.ComponentReference;
import net.mograsim.logic.model.verilog.model.statements.Statement;
import net.mograsim.logic.model.verilog.model.statements.WireDeclaration;
import net.mograsim.logic.model.verilog.utils.IdentifierGenerator;
import net.mograsim.logic.model.verilog.utils.UnionFind;

public class TriStateBufferConverter implements ComponentConverter<ModelTriStateBuffer>
{
	// TODO don't hardcode this
	private static final VerilogComponentDeclaration TSBW_COMBINE = new VerilogComponentDeclaration("tsbw_combine",
			List.of(new Input("wA", 2), new Input("wB", 2), new Output("res", 2)));
	private static final VerilogComponentDeclaration TSBW_CONDITIONAL = new VerilogComponentDeclaration("tsbw_conditional",
			List.of(new Input("cond", 2), new Input("onTrue", 2), new Input("onFalse", 2), new Output("res", 2)));

	@Override
	public ComponentConversionResult convert(ModelTriStateBuffer modelComponent, String modelID, JsonElement params, String verilogID)
	{
		UnionFind<PinNameBit> connectedPins = new UnionFind<>();
		ModelComponentToVerilogComponentDeclarationMapping mapping = ModelComponentToVerilogConverter
				.generateCanonicalDeclarationMapping(modelComponent, connectedPins, modelID, params, verilogID);
		VerilogComponentDeclaration declaration = mapping.getVerilogComponentDeclaration();

		List<Statement> statements = new ArrayList<>();

		PinNameBit condPinbit = new PinNameBit(modelComponent.getEnablePin().name, 0);
		IOPort condPre = mapping.getPrePinMapping().get(condPinbit).getVerilogPort();
		IOPort condOut = mapping.getOutPinMapping().get(condPinbit).getVerilogPort();
		IOPort condRes = mapping.getResPinMapping().get(condPinbit).getVerilogPort();
		Expression condrResRef = new SignalReference(condRes);

		statements.add(new Assign(condOut, new SignalReference(condPre)));

		IdentifierGenerator idGen = ModelComponentToVerilogConverter.generateIdentifierGenerator(declaration);

		Pin inputPin = modelComponent.getInputPin();
		Pin outputPin = modelComponent.getOutputPin();
		for (int bit = 0; bit < inputPin.logicWidth; bit++)
		{
			PinNameBit inputPinbit = new PinNameBit(inputPin.name, bit);
			PinNameBit outputPinbit = new PinNameBit(outputPin.name, bit);

			IOPort inPre = mapping.getPrePinMapping().get(inputPinbit).getVerilogPort();
			IOPort inOut = mapping.getOutPinMapping().get(inputPinbit).getVerilogPort();
			IOPort inRes = mapping.getResPinMapping().get(inputPinbit).getVerilogPort();
			IOPort outPre = mapping.getPrePinMapping().get(outputPinbit).getVerilogPort();
			IOPort outOut = mapping.getOutPinMapping().get(outputPinbit).getVerilogPort();
			Expression inPreRef = new SignalReference(inPre);
			Expression inResRef = new SignalReference(inRes);
			Expression outPreRef = new SignalReference(outPre);
			Expression outOutRef = new SignalReference(outOut);

			Wire condTmp = new Wire(idGen.generateID("cond_tmp_" + bit), 2);
			SignalReference condTmpRef = new SignalReference(condTmp);
			statements.add(new WireDeclaration(condTmp));

			statements.add(new Assign(inOut, inPreRef));
			statements.add(new ComponentReference(idGen.generateID("cond_" + bit), TSBW_CONDITIONAL,
					List.of(condrResRef, inResRef, new Constant(BitVector.of(Bit.ZERO, 2)), condTmpRef)));
			statements
					.add(new ComponentReference(idGen.generateID("comb_" + bit), TSBW_COMBINE, List.of(outPreRef, condTmpRef, outOutRef)));
		}

		VerilogComponentImplementation implementation = new VerilogComponentImplementation(declaration, statements);

		return new ComponentConversionResult(mapping, implementation);
	}
}
