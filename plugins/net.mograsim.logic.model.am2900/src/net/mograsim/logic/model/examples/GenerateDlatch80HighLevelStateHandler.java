package net.mograsim.logic.model.examples;

import java.util.ArrayList;

import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.BitVectorSplittingAtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.BitVectorSplittingAtomicHighLevelStateHandler.BitVectorSplittingAtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.DelegatingAtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.DelegatingAtomicHighLevelStateHandler.DelegatingAtomicHighLevelStateHandlerParams;

public class GenerateDlatch80HighLevelStateHandler
{
	public static void main(String[] args)
	{
		Am2900Loader.setup();
		LogicModelModifiable model = new LogicModelModifiable();
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model, null, null, null);
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#0");// LSB
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#1");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#2");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#3");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#4");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#5");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#6");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#7");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#8");
		IndirectModelComponentCreator.createComponent(comp.getSubmodelModifiable(), "dlatch8", "dlatch8#9");// MSB

		StandardHighLevelStateHandler hlsh = new StandardHighLevelStateHandler(comp);
		comp.setHighLevelStateHandler(hlsh);
		BitVectorSplittingAtomicHighLevelStateHandlerParams p = new BitVectorSplittingAtomicHighLevelStateHandlerParams();
		p.vectorPartLengthes = new ArrayList<>();
		p.vectorPartTargets = new ArrayList<>();
		for (int i = 0; i < 10; i++)
		{
			addHandlersForByte(comp, hlsh, i, p);
		}
		hlsh.addAtomicHighLevelState("q", new BitVectorSplittingAtomicHighLevelStateHandler(comp, p));

		System.out.println(comp.getHighLevelStateHandler().getParamsForSerializingJSON(new IdentifyParams()));
	}

	private static void addHandlersForByte(SubmodelComponent comp, StandardHighLevelStateHandler hlsh, int LSByteIndex,
			BitVectorSplittingAtomicHighLevelStateHandlerParams p2)
	{
		// TODO remove the "+ 1" as soon as HighLevelStates count from 0
		// Also replace the 1 in "bitIndexInByte = 1" below with a 0
		int LSBitIndex = LSByteIndex * 8 + 1;
		int MSBitIndex = LSBitIndex + 7;
		String dlatchThisByte = "dlatch8#" + LSByteIndex;
		String thisByteHLSID = "q" + MSBitIndex + "-" + LSBitIndex;

		p2.vectorPartLengthes.add(0, 8);
		p2.vectorPartTargets.add(0, thisByteHLSID);

		DelegatingAtomicHighLevelStateHandlerParams p = new DelegatingAtomicHighLevelStateHandlerParams();
		p.delegateTarget = dlatchThisByte;
		p.subStateID = "q";
		hlsh.addAtomicHighLevelState(thisByteHLSID, new DelegatingAtomicHighLevelStateHandler(comp, p));
		for (int bitIndexOuter = LSBitIndex, bitIndexInByte = 1; bitIndexOuter <= MSBitIndex; bitIndexOuter++, bitIndexInByte++)
		{
			p = new DelegatingAtomicHighLevelStateHandlerParams();
			p.delegateTarget = dlatchThisByte;
			p.subStateID = "q" + bitIndexInByte;
			hlsh.addAtomicHighLevelState("q" + bitIndexOuter, new DelegatingAtomicHighLevelStateHandler(comp, p));
		}
	}
}