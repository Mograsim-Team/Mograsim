package net.mograsim.plugin.tables.mi;

import java.math.BigInteger;

import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberColumnLabelProvider;

public class IntegerColumnLabelProvider extends NumberColumnLabelProvider
{
	private int index;

	public IntegerColumnLabelProvider(DisplaySettings displaySettings, int index)
	{
		super(displaySettings);
		this.index = index;
	}

	@Override
	public BigInteger getAsBigInteger(Object element)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		return ((IntegerImmediate) row.data.getCell(row.address).getParameter(index)).getValueAsBigInteger();
	}

	@Override
	public int getBitLength(Object element)
	{
		return ((InstructionTableRow) element).getData().getDefinition().getMicroInstructionDefinition().getParameterClassification(index)
				.getExpectedBits();
	}

}
