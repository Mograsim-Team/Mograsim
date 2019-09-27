package net.mograsim.plugin.tables.mi;

import java.math.BigInteger;

import org.eclipse.swt.graphics.Color;

import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.NumberColumnLabelProvider;

public class IntegerColumnLabelProvider extends NumberColumnLabelProvider
{
	private final int index;
	private final ColorProvider cProv;

	public IntegerColumnLabelProvider(DisplaySettings displaySettings, ColorProvider cProv, int index)
	{
		super(displaySettings);
		this.cProv = cProv;
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

	@Override
	public Color getBackground(Object element)
	{
		return cProv.getBackground(element, index);
	}

	@Override
	public Color getForeground(Object element)
	{
		return cProv.getForeground(element, index);
	}
}
