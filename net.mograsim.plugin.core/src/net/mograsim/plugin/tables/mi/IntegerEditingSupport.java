package net.mograsim.plugin.tables.mi;

import java.math.BigInteger;

import org.eclipse.jface.viewers.TableViewer;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.plugin.tables.NumberCellEditingSupport;
import net.mograsim.plugin.tables.memory.DisplaySettings;

public class IntegerEditingSupport extends NumberCellEditingSupport
{
	private IntegerClassification classification;
	private int index;

	public IntegerEditingSupport(TableViewer viewer, MicroInstructionDefinition miDef, int index, DisplaySettings displaySettings)
	{
		super(viewer, displaySettings);
		classification = (IntegerClassification) miDef.getParameterClassifications()[index];
		this.index = index;
	}

	@Override
	protected void setAsBigInteger(Object element, BigInteger value)
	{
		((MicroInstruction) element).setParameter(index, new IntegerImmediate(value, classification.getExpectedBits()));
	}

	@Override
	protected BigInteger getAsBigInteger(Object element)
	{
		return ((IntegerImmediate) ((MicroInstruction) element).getParameter(index)).getValueAsBigInteger();
	}

}
