package net.mograsim.plugin.tables.mi;

import java.math.BigInteger;

import org.eclipse.jface.viewers.TableViewer;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberCellEditingSupport;

public class IntegerEditingSupport extends NumberCellEditingSupport
{
	private IntegerClassification classification;
	private int index;
	private TableViewer viewer;
	private InstructionTableContentProvider provider;

	public IntegerEditingSupport(TableViewer viewer, MicroInstructionDefinition miDef, int index, DisplaySettings displaySettings,
			InstructionTableContentProvider provider)
	{
		super(viewer, displaySettings);
		classification = (IntegerClassification) miDef.getParameterClassifications()[index];
		this.index = index;
		this.viewer = viewer;
		this.provider = provider;
	}

	@Override
	protected void setAsBigInteger(Object element, BigInteger value)
	{
		InstructionTableRow row = ((InstructionTableRow) element);
		row.data.setParameter(index, new IntegerImmediate(value, classification.getExpectedBits()));
		provider.update(row.address);
//		viewer.update(element, null); Does not do anything for some reason
	}

	@Override
	protected BigInteger getAsBigInteger(Object element)
	{
		return ((IntegerImmediate) ((InstructionTableRow) element).data.getParameter(index)).getValueAsBigInteger();
	}

}
