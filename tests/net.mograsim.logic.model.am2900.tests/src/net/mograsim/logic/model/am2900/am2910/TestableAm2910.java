package net.mograsim.logic.model.am2900.am2910;

import java.util.Arrays;
import java.util.stream.Stream;

import net.mograsim.logic.model.am2900.TestableCircuit;

public interface TestableAm2910 extends TestableCircuit
{

	void setInstruction(Am2910_Inst inst);

	void set_CCEN(String val_1_bit);

	void setD(String val_12_bit);

	void set_CC(String val_1_bit);

	void setCI(String val_1_bit);

	void set_RLD(String val_1_bit);

	void set_OE(String val_1_bit);

	void setDirectly(Register r, String val_X_bit);

	String getY();

	String get_FULL();

	String get_PL();

	String get_MAP();

	String get_VECT();

	String getDirectly(Register r);

	enum Am2910_Inst
	{
		JZ, CJS, JMAP, CJP, PUSH, JSRP, CJV, JRP, RFCT, RPCT, CRTN, CJPP, LDCT, LOOP, CONT, TWB;
	}

	enum Register
	{
		S_0, S_1, S_2, S_3, S_4, SP, PC, REG_COUNT;

		public static Stream<Register> stream()
		{
			return Arrays.stream(values());
		}

		public int size()
		{
			if (this == SP)
				return 3;
			return 12;
		}
	}
}
