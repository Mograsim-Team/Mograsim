package net.mograsim.logic.model.am2900.machine.registers.am2901;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class Am2901Register extends HighLevelStateBasedRegister implements Am2900Register
{
	public Am2901Register(String id, String cellSuffix, int logicWidthPerAm2901)
	{
		super(id, new int[] { logicWidthPerAm2901, logicWidthPerAm2901, logicWidthPerAm2901, logicWidthPerAm2901 },
				prefixWithAm2901s(cellSuffix));
	}

	private static String[] prefixWithAm2901s(String suffix)
	{
		String[] prefixed = new String[4];
		for (int i = 0, b = 0; i < 4; i++, b += 4)
			prefixed[i] = String.format("am2901_%d-%d.%s", (b + 3), b, suffix);
		return prefixed;
	}
}