package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.util.function.Function;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.mi.nandbased.GUI_rsLatch;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIand41;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIandor414;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdemux2;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdff;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdlatch;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdlatch4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIhalfadder;
import net.mograsim.logic.model.model.components.mi.nandbased.GUImux1;
import net.mograsim.logic.model.model.components.mi.nandbased.GUImux1_4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUInand3;
import net.mograsim.logic.model.model.components.mi.nandbased.GUInot4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIor4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIor_4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIram2;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIram4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIsel2_4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIsel3_4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIxor;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901ALUFuncDecode;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901ALUInclDecode;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901ALUInclSourceDecodeInclFunctionDecode;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901ALUOneBit;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901DestDecode;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901QReg;
import net.mograsim.logic.model.model.components.mi.nandbased.am2901.GUIAm2901SourceDecode;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.SubmodelComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentSerializer;
import net.mograsim.logic.model.util.JsonHandler;

public class ComponenetSerializer
{
	public static void main(String[] args) throws IOException
	{
		// we know we only use components where this works
		Function<GUIComponent, String> getIdentifier = c -> c.getClass().getSimpleName();

		ViewModelModifiable model = new ViewModelModifiable();
		SubmodelComponent[] components = { new GUIAm2901(model), new GUIAm2901ALUFuncDecode(model), new GUIAm2901ALUInclDecode(model),
				new GUIAm2901ALUInclSourceDecodeInclFunctionDecode(model), new GUIAm2901ALUOneBit(model), new GUIAm2901DestDecode(model),
				new GUIAm2901QReg(model), new GUIAm2901SourceDecode(model), new GUI_rsLatch(model), new GUIand(model), new GUIand41(model),
				new GUIandor414(model), new GUIdemux2(model), new GUIdff(model), new GUIdlatch(model), new GUIdlatch4(model),
				new GUIfulladder(model), new GUIhalfadder(model), new GUImux1(model), new GUImux1_4(model), new GUInand3(model),
				new GUInot4(model), new GUIor4(model), new GUIor_4(model), new GUIram2(model), new GUIram4(model), new GUIsel2_4(model),
				new GUIsel3_4(model), new GUIxor(model) };

		for (SubmodelComponent comp : components)
		{
			SubmodelComponentParams params = SubmodelComponentSerializer.serialize(comp, getIdentifier);
			JsonHandler
					.writeJson(params,
							"components/"
									+ comp.getClass().getName()
											.substring("net.mograsim.logic.model.model.components.mi.nandbased.".length()).replace('.', '/')
									+ ".json");
		}
	}
}