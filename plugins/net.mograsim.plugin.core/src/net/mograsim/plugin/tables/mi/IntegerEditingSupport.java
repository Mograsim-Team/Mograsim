package net.mograsim.plugin.tables.mi;

import java.math.BigInteger;

import org.eclipse.jface.viewers.TableViewer;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberCellEditingSupport;

public class IntegerEditingSupport extends NumberCellEditingSupport
{
	private IntegerClassification classification;
	private int index;
	private InstructionTableContentProvider provider;

	public IntegerEditingSupport(TableViewer viewer, MicroInstructionDefinition miDef, int index, DisplaySettings displaySettings,
			InstructionTableContentProvider provider)
	{
		super(viewer, displaySettings, true);
		classification = (IntegerClassification) miDef.getParameterClassifications()[index];
		this.index = index;
		this.provider = provider;
	}

	@Override
	protected void setAsBigInteger(Object element, BigInteger value)
	{
		InstructionTableRow row = ((InstructionTableRow) element);
		MicroInstructionParameter[] params = row.data.getCell(row.address).getParameters();
		IntegerImmediate newParam = new IntegerImmediate(classification, value, classification.getExpectedBits());
		if (params[index].equals(newParam))
			return;
		params[index] = newParam;
		row.data.setCell(row.address, MicroInstruction.create(params));
		provider.update(row.address);
//		viewer.update(element, null); Does not do anything for some reason
	}

	@Override
	protected BigInteger getAsBigInteger(Object element)
	{
		InstructionTableRow row = ((InstructionTableRow) element);
		return ((IntegerImmediate) row.data.getCell(row.address).getParameter(index)).getValueAsBigInteger();
	}

	@Override
	public int getBitLength(Object element)
	{
		return ((InstructionTableRow) element).getData().getDefinition().getMicroInstructionDefinition().getParameterClassification(index)
				.getExpectedBits();
	}

}
