package net.mograsim.logic.model.am2900.am2910;

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

	String getY();

	String get_FULL();

	String get_PL();

	String get_MAP();

	String get_VECT();

	enum Am2910_Inst
	{
		JZ, CJS, JMAP, CJP, PUSH, JSRP, CJV, JRP, RFCT, RPCT, CRTN, CJPP, LDCT, LOOP, CONT, TWB;
	}
}
