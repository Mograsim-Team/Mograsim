package net.mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.Component;
import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.ui.model.components.Am2901NANDBased;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public class Am2901NANDBasedAdapter implements ComponentAdapter<Am2901NANDBased>
{
	@Override
	public Class<Am2901NANDBased> getSupportedClass()
	{
		return Am2901NANDBased.class;
	}

	@Override
	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, Am2901NANDBased guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		Wire w00 = logicWiresPerPin.get(guiComponent.getPins().get(0));
		Wire w01 = logicWiresPerPin.get(guiComponent.getPins().get(1));
		Wire w02 = logicWiresPerPin.get(guiComponent.getPins().get(2));
		Wire w03 = logicWiresPerPin.get(guiComponent.getPins().get(3));
		Wire w04 = logicWiresPerPin.get(guiComponent.getPins().get(4));
		Wire w05 = logicWiresPerPin.get(guiComponent.getPins().get(5));
		Wire w06 = logicWiresPerPin.get(guiComponent.getPins().get(6));
		Wire w07 = logicWiresPerPin.get(guiComponent.getPins().get(7));
		Wire w08 = logicWiresPerPin.get(guiComponent.getPins().get(8));
		Wire w09 = logicWiresPerPin.get(guiComponent.getPins().get(9));
		Wire w10 = logicWiresPerPin.get(guiComponent.getPins().get(10));
		Wire w11 = logicWiresPerPin.get(guiComponent.getPins().get(11));
		Wire w12 = logicWiresPerPin.get(guiComponent.getPins().get(12));
		Wire w13 = logicWiresPerPin.get(guiComponent.getPins().get(13));
		Wire w14 = logicWiresPerPin.get(guiComponent.getPins().get(14));
		Wire w15 = logicWiresPerPin.get(guiComponent.getPins().get(15));
		Wire w16 = logicWiresPerPin.get(guiComponent.getPins().get(16));
		Wire w17 = logicWiresPerPin.get(guiComponent.getPins().get(17));
		Wire w18 = logicWiresPerPin.get(guiComponent.getPins().get(18));
		Wire w19 = logicWiresPerPin.get(guiComponent.getPins().get(19));
		Wire w20 = logicWiresPerPin.get(guiComponent.getPins().get(20));
		Wire w21 = logicWiresPerPin.get(guiComponent.getPins().get(21));
		Wire w22 = logicWiresPerPin.get(guiComponent.getPins().get(22));
		Wire w23 = logicWiresPerPin.get(guiComponent.getPins().get(23));
		Wire w24 = logicWiresPerPin.get(guiComponent.getPins().get(24));
		Wire w25 = logicWiresPerPin.get(guiComponent.getPins().get(25));
		Wire w26 = logicWiresPerPin.get(guiComponent.getPins().get(26));
		Wire w27 = logicWiresPerPin.get(guiComponent.getPins().get(27));
		Wire w28 = logicWiresPerPin.get(guiComponent.getPins().get(28));
		Wire w29 = logicWiresPerPin.get(guiComponent.getPins().get(29));
		Wire w30 = logicWiresPerPin.get(guiComponent.getPins().get(30));
		Wire w31 = logicWiresPerPin.get(guiComponent.getPins().get(31));
		Wire w32 = logicWiresPerPin.get(guiComponent.getPins().get(32));
		Wire w33 = logicWiresPerPin.get(guiComponent.getPins().get(33));
		Wire w34 = logicWiresPerPin.get(guiComponent.getPins().get(34));
		Wire w35 = logicWiresPerPin.get(guiComponent.getPins().get(35));
		Wire w36 = logicWiresPerPin.get(guiComponent.getPins().get(36));
		Wire w37 = logicWiresPerPin.get(guiComponent.getPins().get(37));
		createAm2901(timeline, params, w00, w01, w02, w03, w04, w05, w06, w07, w08, w09, w10, w11, w12, w13, w14, w15, w16, w17, w18, w19,
				w20, w21, w22, w23, w24, w25, w26, w27, w28, w29, w30, w31, w32, w33, w34, w35, w36, w37);
		return null;
	}

	private static void create_rsLatch(Timeline timeline, LogicModelParameters params, Wire _S, Wire _R, Wire Q, Wire _Q)
	{
		createNand(timeline, params, _S, _Q, Q);
		createNand(timeline, params, _R, Q, _Q);
	}

	private static void createAm2901(Timeline timeline, LogicModelParameters params, Wire I8, Wire I7, Wire I6, Wire I5, Wire I4, Wire I3,
			Wire I2, Wire I1, Wire I0, Wire C, Wire Cn, Wire D1, Wire D2, Wire D3, Wire D4, Wire A0, Wire A1, Wire A2, Wire A3, Wire B0,
			Wire B1, Wire B2, Wire B3, Wire IRAMn, Wire IRAMnplus3, Wire IQn, Wire IQnplus3, Wire Y1, Wire Y2, Wire Y3, Wire Y4, Wire Feq0,
			Wire Cnplus4, Wire OVR, Wire F3_ORAMnplus3, Wire ORAMn, Wire OQn, Wire OQnplus3)
	{
		Wire NSH = createWire(timeline, params);
		Wire RSH = createWire(timeline, params);
		Wire RAMWE = createWire(timeline, params);
		Wire YF = createWire(timeline, params);
		Wire LSH = createWire(timeline, params);
		Wire QWE = createWire(timeline, params);
		Wire notC = createWire(timeline, params);
		Wire RAMWEandnotC = createWire(timeline, params);
		Wire ramD1 = createWire(timeline, params);
		Wire ramD2 = createWire(timeline, params);
		Wire ramD3 = createWire(timeline, params);
		Wire ramD4 = createWire(timeline, params);
		Wire ramA1 = createWire(timeline, params);
		Wire ramA2 = createWire(timeline, params);
		Wire ramA3 = createWire(timeline, params);
		Wire ramA4 = createWire(timeline, params);
		Wire ramB1 = createWire(timeline, params);
		Wire ramB2 = createWire(timeline, params);
		Wire ramB3 = createWire(timeline, params);
		Wire ramB4 = createWire(timeline, params);
		Wire Alatch1 = createWire(timeline, params);
		Wire Alatch2 = createWire(timeline, params);
		Wire Alatch3 = createWire(timeline, params);
		Wire Alatch4 = createWire(timeline, params);
		Wire Blatch1 = createWire(timeline, params);
		Wire Blatch2 = createWire(timeline, params);
		Wire Blatch3 = createWire(timeline, params);
		Wire Blatch4 = createWire(timeline, params);
		Wire F0 = ORAMn;
		Wire F1 = createWire(timeline, params);
		Wire F2 = createWire(timeline, params);
		Wire F3_inner = F3_ORAMnplus3;
		Wire Fneq0 = createWire(timeline, params);
		Wire Q1 = OQn;
		Wire Q2 = createWire(timeline, params);
		Wire Q3 = createWire(timeline, params);
		Wire Q4 = OQnplus3;
		Wire qregD1 = createWire(timeline, params);
		Wire qregD2 = createWire(timeline, params);
		Wire qregD3 = createWire(timeline, params);
		Wire qregD4 = createWire(timeline, params);

		createAm2901DestDecode(timeline, params, I8, I7, I6, NSH, RSH, RAMWE, YF, LSH, QWE);
		createSel3_4(timeline, params, LSH, NSH, RSH, IRAMn, F0, F1, F2, F0, F1, F2, F3_inner, F1, F2, F3_inner, IRAMnplus3, ramD1, ramD2,
				ramD3, ramD4);
		createNand(timeline, params, C, C, notC);
		createAnd(timeline, params, RAMWE, notC, RAMWEandnotC);
		createRam4(timeline, params, A0, A1, A2, A3, B0, B1, B2, B3, RAMWEandnotC, ramD1, ramD2, ramD3, ramD4, ramA1, ramA2, ramA3, ramA4,
				ramB1, ramB2, ramB3, ramB4);
		createDlatch4(timeline, params, C, ramA1, ramA2, ramA3, ramA4, Alatch1, Alatch2, Alatch3, Alatch4);
		createDlatch4(timeline, params, C, ramB1, ramB2, ramB3, ramB4, Blatch1, Blatch2, Blatch3, Blatch4);
		createSel3_4(timeline, params, LSH, NSH, RSH, IQn, Q1, Q2, Q3, F0, F1, F2, F3_inner, Q2, Q3, Q4, IQnplus3, qregD1, qregD2, qregD3,
				qregD4);
		createAm2901QReg(timeline, params, C, QWE, qregD1, qregD2, qregD3, qregD4, Q1, Q2, Q3, Q4);
		createAm2901ALUInclSourceDecodeInclFunctionDecode(timeline, params, I5, I4, I3, I2, I1, I0, Cn, D1, D2, D3, D4, Alatch1, Alatch2,
				Alatch3, Alatch4, Blatch1, Blatch2, Blatch3, Blatch4, Q1, Q2, Q3, Q4, F0, F1, F2, F3_inner, Cnplus4, OVR);
		createMux1_4(timeline, params, YF, Alatch1, Alatch2, Alatch3, Alatch4, F0, F1, F2, F3_inner, Y1, Y2, Y3, Y4);
		createOr4(timeline, params, F0, F1, F2, F3_inner, Fneq0);
		createNand(timeline, params, Fneq0, Fneq0, Feq0);
	}

	private static void createAm2901ALUFuncDecode(Timeline timeline, LogicModelParameters params, Wire I5_FN, Wire I4_SN, Wire I3_RN,
			Wire CinE, Wire L, Wire SBE)
	{
		Wire notI5 = CinE;
		Wire notI4 = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);
		Wire w3 = createWire(timeline, params);

		createNand(timeline, params, I5_FN, I5_FN, notI5);
		createNand(timeline, params, I4_SN, I4_SN, notI4);
		createNand3(timeline, params, I4_SN, I3_RN, notI5, w1);
		createNand(timeline, params, I5_FN, notI4, w2);
		createNand(timeline, params, I3_RN, I4_SN, w3);
		createNand(timeline, params, w1, w2, L);
		createAnd(timeline, params, w3, notI5, SBE);
	}

	private static void createAm2901ALUInclDecode(Timeline timeline, LogicModelParameters params, Wire I5, Wire I4, Wire I3, Wire Cn,
			Wire R1, Wire R2, Wire R3, Wire R4, Wire S1, Wire S2, Wire S3, Wire S4, Wire F1, Wire F2, Wire F3, Wire F4, Wire Cnplus4,
			Wire OVR)
	{
		Wire CinE = createWire(timeline, params);
		Wire L = createWire(timeline, params);
		Wire SN = I4;
		Wire SBE = createWire(timeline, params);
		Wire FN = I5;
		Wire RN = I3;
		Wire Cnplus1 = createWire(timeline, params);
		Wire Cnplus2 = createWire(timeline, params);
		Wire Cnplus3 = createWire(timeline, params);
		Wire Cnplus4_inner = Cnplus4;

		createAm2901ALUFuncDecode(timeline, params, I5, I4, I3, CinE, L, SBE);
		createAm2901ALUOneBit(timeline, params, Cn, CinE, R1, RN, S1, SN, SBE, FN, L, Cnplus1, F1);
		createAm2901ALUOneBit(timeline, params, Cnplus1, CinE, R2, RN, S2, SN, SBE, FN, L, Cnplus2, F2);
		createAm2901ALUOneBit(timeline, params, Cnplus2, CinE, R3, RN, S3, SN, SBE, FN, L, Cnplus3, F3);
		createAm2901ALUOneBit(timeline, params, Cnplus3, CinE, R4, RN, S4, SN, SBE, FN, L, Cnplus4_inner, F4);
		createXor(timeline, params, Cnplus3, Cnplus4_inner, OVR);
	}

	private static void createAm2901ALUInclSourceDecodeInclFunctionDecode(Timeline timeline, LogicModelParameters params, Wire I5, Wire I4,
			Wire I3, Wire I2, Wire I1, Wire I0, Wire Cn, Wire D1, Wire D2, Wire D3, Wire D4, Wire A1, Wire A2, Wire A3, Wire A4, Wire B1,
			Wire B2, Wire B3, Wire B4, Wire Q1, Wire Q2, Wire Q3, Wire Q4, Wire F1, Wire F2, Wire F3, Wire F4, Wire Cnplus4, Wire OVR)
	{
		Wire SQ = createWire(timeline, params);
		Wire RA = createWire(timeline, params);
		Wire SB = createWire(timeline, params);
		Wire SA = createWire(timeline, params);
		Wire RD = createWire(timeline, params);
		Wire R1 = createWire(timeline, params);
		Wire R2 = createWire(timeline, params);
		Wire R3 = createWire(timeline, params);
		Wire R4 = createWire(timeline, params);
		Wire S1 = createWire(timeline, params);
		Wire S2 = createWire(timeline, params);
		Wire S3 = createWire(timeline, params);
		Wire S4 = createWire(timeline, params);

		createAm2901SourceDecode(timeline, params, I2, I1, I0, SQ, RA, SB, SA, RD);
		createSel2_4(timeline, params, RD, RA, D1, D2, D3, D4, A1, A2, A3, A4, R1, R2, R3, R4);
		createSel3_4(timeline, params, SA, SB, SQ, A1, A2, A3, A4, B1, B2, B3, B4, Q1, Q2, Q3, Q4, S1, S2, S3, S4);
		createAm2901ALUInclDecode(timeline, params, I5, I4, I3, Cn, R1, R2, R3, R4, S1, S2, S3, S4, F1, F2, F3, F4, Cnplus4, OVR);
	}

	private static void createAm2901ALUOneBit(Timeline timeline, LogicModelParameters params, Wire Cin, Wire CinE, Wire R, Wire RN, Wire S,
			Wire SN, Wire CoutE, Wire FN, Wire L, Wire Cout, Wire F)
	{
		Wire Cintemp = createWire(timeline, params);
		Wire Rtemp = createWire(timeline, params);
		Wire Stemp = createWire(timeline, params);
		Wire xor = createWire(timeline, params);
		Wire nand = createWire(timeline, params);
		Wire Couttemp = createWire(timeline, params);
		Wire Ftemp = createWire(timeline, params);

		createAnd(timeline, params, Cin, CinE, Cintemp);
		createXor(timeline, params, R, RN, Rtemp);
		createXor(timeline, params, S, SN, Stemp);
		createFulladder(timeline, params, Cintemp, Rtemp, Stemp, xor, Couttemp);
		createNand(timeline, params, Rtemp, Stemp, nand);
		createAnd(timeline, params, CoutE, Couttemp, Cout);
		createMux1(timeline, params, L, xor, nand, Ftemp);
		createXor(timeline, params, Ftemp, FN, F);
	}

	private static void createAm2901DestDecode(Timeline timeline, LogicModelParameters params, Wire I8, Wire I7, Wire I6, Wire NSH,
			Wire RSH, Wire RAMWE, Wire YF, Wire LSH, Wire QWE)
	{
		Wire notI8 = NSH;
		Wire notI7 = createWire(timeline, params);
		Wire notI6 = createWire(timeline, params);
		Wire I8nandI7 = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);
		Wire w3 = createWire(timeline, params);
		Wire w4 = createWire(timeline, params);

		createNand(timeline, params, I8, I8, notI8);
		createNand(timeline, params, I7, I7, notI7);
		createNand(timeline, params, I6, I6, notI6);
		createNand(timeline, params, I8, I7, I8nandI7);
		createNand(timeline, params, I8, notI7, w1);
		createNand(timeline, params, notI8, notI7, RAMWE);
		createNand(timeline, params, notI8, I7, w2);
		createNand(timeline, params, I8nandI7, I8nandI7, LSH);
		createNand(timeline, params, w1, w1, RSH);
		createNand(timeline, params, w2, w2, w3);
		createNand(timeline, params, w2, notI6, w4);
		createNand(timeline, params, w3, notI6, YF);
		createNand(timeline, params, w4, w4, QWE);
	}

	private static void createAm2901QReg(Timeline timeline, LogicModelParameters params, Wire C, Wire WE, Wire D1, Wire D2, Wire D3,
			Wire D4, Wire Q1, Wire Q2, Wire Q3, Wire Q4)
	{
		Wire CandWE = createWire(timeline, params);
		Wire nc1 = createWire(timeline, params);
		Wire nc2 = createWire(timeline, params);
		Wire nc3 = createWire(timeline, params);
		Wire nc4 = createWire(timeline, params);

		createAnd(timeline, params, C, WE, CandWE);
		createDff(timeline, params, CandWE, D1, Q1, nc1);
		createDff(timeline, params, CandWE, D2, Q2, nc2);
		createDff(timeline, params, CandWE, D3, Q3, nc3);
		createDff(timeline, params, CandWE, D4, Q4, nc4);
	}

	private static void createAm2901SourceDecode(Timeline timeline, LogicModelParameters params, Wire I2, Wire I1, Wire I0, Wire SQ,
			Wire RA, Wire SB, Wire SA, Wire RD)
	{
		Wire notI2 = createWire(timeline, params);
		Wire notI1 = createWire(timeline, params);
		Wire notI0 = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);
		Wire w3 = createWire(timeline, params);
		Wire w4 = createWire(timeline, params);
		Wire w5 = createWire(timeline, params);
		Wire w6 = createWire(timeline, params);
		Wire w7 = createWire(timeline, params);

		createNand(timeline, params, I2, I2, notI2);
		createNand(timeline, params, I1, I1, notI1);
		createNand(timeline, params, I0, I0, notI0);
		createNand(timeline, params, I2, notI1, w1);
		createNand(timeline, params, notI2, notI1, w2);
		createNand(timeline, params, notI2, I0, w3);
		createNand(timeline, params, notI1, I2, w4);
		createNand(timeline, params, notI1, notI0, w5);
		createNand(timeline, params, notI0, w1, w6);
		createNand(timeline, params, w2, w2, RA);
		createNand(timeline, params, w3, w3, SB);
		createNand(timeline, params, w4, w4, SA);
		createNand(timeline, params, w5, I2, w7);
		createNand(timeline, params, w6, w6, SQ);
		createNand(timeline, params, w7, w7, RD);
	}

	private static void createAnd(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire Y)
	{
		Wire AnandB = createWire(timeline, params);

		createNand(timeline, params, A, B, AnandB);
		createNand(timeline, params, AnandB, AnandB, Y);
	}

	private static void createAnd41(Timeline timeline, LogicModelParameters params, Wire A1, Wire A2, Wire A3, Wire A4, Wire B, Wire Y1,
			Wire Y2, Wire Y3, Wire Y4)
	{
		createAnd(timeline, params, A1, B, Y1);
		createAnd(timeline, params, A2, B, Y2);
		createAnd(timeline, params, A3, B, Y3);
		createAnd(timeline, params, A4, B, Y4);
	}

	private static void createAndor414(Timeline timeline, LogicModelParameters params, Wire C1, Wire C2, Wire C3, Wire C4, Wire A1, Wire A2,
			Wire A3, Wire A4, Wire B, Wire Y1, Wire Y2, Wire Y3, Wire Y4)
	{
		Wire A1andB = createWire(timeline, params);
		Wire A2andB = createWire(timeline, params);
		Wire A3andB = createWire(timeline, params);
		Wire A4andB = createWire(timeline, params);

		createAnd41(timeline, params, A1, A2, A3, A4, B, A1andB, A2andB, A3andB, A4andB);
		createOr_4(timeline, params, C1, C2, C3, C4, A1andB, A2andB, A3andB, A4andB, Y1, Y2, Y3, Y4);
	}

	private static void createDemux2(Timeline timeline, LogicModelParameters params, Wire S0, Wire S1, Wire Y00, Wire Y01, Wire Y10,
			Wire Y11)
	{
		Wire notS0 = createWire(timeline, params);
		Wire notS1 = createWire(timeline, params);

		createNand(timeline, params, S0, S0, notS0);
		createNand(timeline, params, S1, S1, notS1);
		createAnd(timeline, params, notS0, notS1, Y00);
		createAnd(timeline, params, S0, notS1, Y01);
		createAnd(timeline, params, notS0, S1, Y10);
		createAnd(timeline, params, S0, S1, Y11);
	}

	private static void createDff(Timeline timeline, LogicModelParameters params, Wire C, Wire D, Wire Q, Wire _Q)
	{
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);
		Wire w3 = createWire(timeline, params);
		Wire nc = createWire(timeline, params);

		create_rsLatch(timeline, params, w3, C, nc, w1);
		createNand3(timeline, params, w1, C, w3, w2);
		createNand(timeline, params, w2, D, w3);
		create_rsLatch(timeline, params, w1, w2, Q, _Q);
	}

	private static void createDlatch(Timeline timeline, LogicModelParameters params, Wire D, Wire E, Wire Q, Wire _Q)
	{
		Wire DnandE = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);

		createNand(timeline, params, D, E, DnandE);
		createNand(timeline, params, DnandE, E, w1);
		create_rsLatch(timeline, params, DnandE, w1, Q, _Q);
	}

	private static void createDlatch4(Timeline timeline, LogicModelParameters params, Wire C, Wire D1, Wire D2, Wire D3, Wire D4, Wire Q1,
			Wire Q2, Wire Q3, Wire Q4)
	{
		Wire nc1 = createWire(timeline, params);
		Wire nc2 = createWire(timeline, params);
		Wire nc3 = createWire(timeline, params);
		Wire nc4 = createWire(timeline, params);

		createDlatch(timeline, params, D1, C, Q1, nc1);
		createDlatch(timeline, params, D2, C, Q2, nc2);
		createDlatch(timeline, params, D3, C, Q3, nc3);
		createDlatch(timeline, params, D4, C, Q4, nc4);
	}

	private static void createFulladder(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire C, Wire Y, Wire Z)
	{
		Wire BxorC = createWire(timeline, params);
		Wire BnandC = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);

		createHalfadder(timeline, params, B, C, BxorC, BnandC);
		createHalfadder(timeline, params, A, BxorC, Y, w1);
		createNand(timeline, params, w1, BnandC, Z);
	}

	private static void createHalfadder(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire Y, Wire _Z)
	{
		Wire AnandB = _Z;
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);

		createNand(timeline, params, A, B, AnandB);
		createNand(timeline, params, A, AnandB, w1);
		createNand(timeline, params, B, AnandB, w2);
		createNand(timeline, params, w1, w2, Y);
	}

	private static void createMux1(Timeline timeline, LogicModelParameters params, Wire S0, Wire I0, Wire I1, Wire Y)
	{
		Wire notS0 = createWire(timeline, params);
		Wire I0temp = createWire(timeline, params);
		Wire I1temp = createWire(timeline, params);

		createNand(timeline, params, S0, S0, notS0);
		createNand(timeline, params, notS0, I0, I0temp);
		createNand(timeline, params, S0, I1, I1temp);
		createNand(timeline, params, I0temp, I1temp, Y);
	}

	private static void createMux1_4(Timeline timeline, LogicModelParameters params, Wire S0, Wire I0_0, Wire I0_1, Wire I0_2, Wire I0_3,
			Wire I1_0, Wire I1_1, Wire I1_2, Wire I1_3, Wire Y1, Wire Y2, Wire Y3, Wire Y4)
	{
		createMux1(timeline, params, S0, I0_0, I1_0, Y1);
		createMux1(timeline, params, S0, I0_1, I1_1, Y2);
		createMux1(timeline, params, S0, I0_2, I1_2, Y3);
		createMux1(timeline, params, S0, I0_3, I1_3, Y4);
	}

	private static void createNand3(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire C, Wire Y)
	{
		Wire AnandB = createWire(timeline, params);
		Wire AandB = createWire(timeline, params);

		createNand(timeline, params, A, B, AnandB);
		createNand(timeline, params, AnandB, AnandB, AandB);
		createNand(timeline, params, AandB, C, Y);
	}

	private static void createNot4(Timeline timeline, LogicModelParameters params, Wire A1, Wire A2, Wire A3, Wire A4, Wire Y1, Wire Y2,
			Wire Y3, Wire Y4)
	{
		createNand(timeline, params, A1, A1, Y1);
		createNand(timeline, params, A2, A2, Y2);
		createNand(timeline, params, A3, A3, Y3);
		createNand(timeline, params, A4, A4, Y4);
	}

	private static void createOr_4(Timeline timeline, LogicModelParameters params, Wire A1, Wire A2, Wire A3, Wire A4, Wire B1, Wire B2,
			Wire B3, Wire B4, Wire Y1, Wire Y2, Wire Y3, Wire Y4)
	{
		Wire notA1 = createWire(timeline, params);
		Wire notA2 = createWire(timeline, params);
		Wire notA3 = createWire(timeline, params);
		Wire notA4 = createWire(timeline, params);
		Wire notB1 = createWire(timeline, params);
		Wire notB2 = createWire(timeline, params);
		Wire notB3 = createWire(timeline, params);
		Wire notB4 = createWire(timeline, params);

		createNand(timeline, params, A1, A1, notA1);
		createNand(timeline, params, A2, A2, notA2);
		createNand(timeline, params, A3, A3, notA3);
		createNand(timeline, params, A4, A4, notA4);
		createNand(timeline, params, B1, B1, notB1);
		createNand(timeline, params, B2, B2, notB2);
		createNand(timeline, params, B3, B3, notB3);
		createNand(timeline, params, B4, B4, notB4);

		createNand(timeline, params, notA1, notB1, Y1);
		createNand(timeline, params, notA2, notB2, Y2);
		createNand(timeline, params, notA3, notB3, Y3);
		createNand(timeline, params, notA4, notB4, Y4);
	}

	private static void createOr4(Timeline timeline, LogicModelParameters params, Wire A1, Wire A2, Wire A3, Wire A4, Wire Y)
	{
		Wire notA1 = createWire(timeline, params);
		Wire notA2 = createWire(timeline, params);
		Wire notA3 = createWire(timeline, params);
		Wire notA4 = createWire(timeline, params);
		Wire A1orA2 = createWire(timeline, params);
		Wire A3orA4 = createWire(timeline, params);
		Wire A1norA2 = createWire(timeline, params);
		Wire A3norA4 = createWire(timeline, params);

		createNand(timeline, params, A1, A1, notA1);
		createNand(timeline, params, A2, A2, notA2);
		createNand(timeline, params, A3, A3, notA3);
		createNand(timeline, params, A4, A4, notA4);
		createNand(timeline, params, notA1, notA2, A1orA2);
		createNand(timeline, params, notA3, notA4, A3orA4);
		createNand(timeline, params, A1orA2, A1orA2, A1norA2);
		createNand(timeline, params, A3orA4, A3orA4, A3norA4);
		createNand(timeline, params, A1norA2, A3norA4, Y);
	}

	private static void createRam2(Timeline timeline, LogicModelParameters params, Wire A0, Wire A1, Wire B0, Wire B1, Wire WE, Wire D1,
			Wire D2, Wire D3, Wire D4, Wire QA1, Wire QA2, Wire QA3, Wire QA4, Wire QB1, Wire QB2, Wire QB3, Wire QB4)
	{
		Wire A00 = createWire(timeline, params);
		Wire A01 = createWire(timeline, params);
		Wire A10 = createWire(timeline, params);
		Wire A11 = createWire(timeline, params);
		Wire B00 = createWire(timeline, params);
		Wire B01 = createWire(timeline, params);
		Wire B10 = createWire(timeline, params);
		Wire B11 = createWire(timeline, params);
		Wire B00andWE = createWire(timeline, params);
		Wire B01andWE = createWire(timeline, params);
		Wire B10andWE = createWire(timeline, params);
		Wire B11andWE = createWire(timeline, params);
		Wire Q001 = createWire(timeline, params);
		Wire Q002 = createWire(timeline, params);
		Wire Q003 = createWire(timeline, params);
		Wire Q004 = createWire(timeline, params);
		Wire Q011 = createWire(timeline, params);
		Wire Q012 = createWire(timeline, params);
		Wire Q013 = createWire(timeline, params);
		Wire Q014 = createWire(timeline, params);
		Wire Q101 = createWire(timeline, params);
		Wire Q102 = createWire(timeline, params);
		Wire Q103 = createWire(timeline, params);
		Wire Q104 = createWire(timeline, params);
		Wire Q111 = createWire(timeline, params);
		Wire Q112 = createWire(timeline, params);
		Wire Q113 = createWire(timeline, params);
		Wire Q114 = createWire(timeline, params);
		Wire QAtempto001 = createWire(timeline, params);
		Wire QAtempto002 = createWire(timeline, params);
		Wire QAtempto003 = createWire(timeline, params);
		Wire QAtempto004 = createWire(timeline, params);
		Wire QAtempto011 = createWire(timeline, params);
		Wire QAtempto012 = createWire(timeline, params);
		Wire QAtempto013 = createWire(timeline, params);
		Wire QAtempto014 = createWire(timeline, params);
		Wire QAtempto101 = createWire(timeline, params);
		Wire QAtempto102 = createWire(timeline, params);
		Wire QAtempto103 = createWire(timeline, params);
		Wire QAtempto104 = createWire(timeline, params);
		Wire QBtempto001 = createWire(timeline, params);
		Wire QBtempto002 = createWire(timeline, params);
		Wire QBtempto003 = createWire(timeline, params);
		Wire QBtempto004 = createWire(timeline, params);
		Wire QBtempto011 = createWire(timeline, params);
		Wire QBtempto012 = createWire(timeline, params);
		Wire QBtempto013 = createWire(timeline, params);
		Wire QBtempto014 = createWire(timeline, params);
		Wire QBtempto101 = createWire(timeline, params);
		Wire QBtempto102 = createWire(timeline, params);
		Wire QBtempto103 = createWire(timeline, params);
		Wire QBtempto104 = createWire(timeline, params);

		createDemux2(timeline, params, A0, A1, A00, A01, A10, A11);
		createDemux2(timeline, params, B0, B1, B00, B01, B10, B11);
		createAnd41(timeline, params, B00, B01, B10, B11, WE, B00andWE, B01andWE, B10andWE, B11andWE);
		createDlatch4(timeline, params, B00andWE, D1, D2, D3, D4, Q001, Q011, Q101, Q111);
		createDlatch4(timeline, params, B01andWE, D1, D2, D3, D4, Q002, Q012, Q102, Q112);
		createDlatch4(timeline, params, B10andWE, D1, D2, D3, D4, Q003, Q013, Q103, Q113);
		createDlatch4(timeline, params, B11andWE, D1, D2, D3, D4, Q004, Q014, Q104, Q114);
		createAnd41(timeline, params, Q001, Q002, Q003, Q004, A00, QAtempto001, QAtempto002, QAtempto003, QAtempto004);
		createAndor414(timeline, params, QAtempto001, QAtempto002, QAtempto003, QAtempto004, Q011, Q012, Q013, Q014, A01, QAtempto011,
				QAtempto012, QAtempto013, QAtempto014);
		createAndor414(timeline, params, QAtempto011, QAtempto012, QAtempto013, QAtempto014, Q101, Q102, Q103, Q104, A10, QAtempto101,
				QAtempto102, QAtempto103, QAtempto104);
		createAndor414(timeline, params, QAtempto101, QAtempto102, QAtempto103, QAtempto104, Q111, Q112, Q113, Q114, A11, QA1, QA2, QA3,
				QA4);
		createAnd41(timeline, params, Q001, Q002, Q003, Q004, B00, QBtempto001, QBtempto002, QBtempto003, QBtempto004);
		createAndor414(timeline, params, QBtempto001, QBtempto002, QBtempto003, QBtempto004, Q011, Q012, Q013, Q014, B01, QBtempto011,
				QBtempto012, QBtempto013, QBtempto014);
		createAndor414(timeline, params, QBtempto011, QBtempto012, QBtempto013, QBtempto014, Q101, Q102, Q103, Q104, B10, QBtempto101,
				QBtempto102, QBtempto103, QBtempto104);
		createAndor414(timeline, params, QBtempto101, QBtempto102, QBtempto103, QBtempto104, Q111, Q112, Q113, Q114, B11, QB1, QB2, QB3,
				QB4);
	}

	private static void createRam4(Timeline timeline, LogicModelParameters params, Wire A0, Wire A1, Wire A2, Wire A3, Wire B0, Wire B1,
			Wire B2, Wire B3, Wire WE, Wire D1, Wire D2, Wire D3, Wire D4, Wire QA1, Wire QA2, Wire QA3, Wire QA4, Wire QB1, Wire QB2,
			Wire QB3, Wire QB4)
	{
		Wire A00 = createWire(timeline, params);
		Wire A01 = createWire(timeline, params);
		Wire A10 = createWire(timeline, params);
		Wire A11 = createWire(timeline, params);
		Wire B00 = createWire(timeline, params);
		Wire B01 = createWire(timeline, params);
		Wire B10 = createWire(timeline, params);
		Wire B11 = createWire(timeline, params);
		Wire B00andWE = createWire(timeline, params);
		Wire B01andWE = createWire(timeline, params);
		Wire B10andWE = createWire(timeline, params);
		Wire B11andWE = createWire(timeline, params);
		Wire QA001 = createWire(timeline, params);
		Wire QA002 = createWire(timeline, params);
		Wire QA003 = createWire(timeline, params);
		Wire QA004 = createWire(timeline, params);
		Wire QA011 = createWire(timeline, params);
		Wire QA012 = createWire(timeline, params);
		Wire QA013 = createWire(timeline, params);
		Wire QA014 = createWire(timeline, params);
		Wire QA101 = createWire(timeline, params);
		Wire QA102 = createWire(timeline, params);
		Wire QA103 = createWire(timeline, params);
		Wire QA104 = createWire(timeline, params);
		Wire QA111 = createWire(timeline, params);
		Wire QA112 = createWire(timeline, params);
		Wire QA113 = createWire(timeline, params);
		Wire QA114 = createWire(timeline, params);
		Wire QB001 = createWire(timeline, params);
		Wire QB002 = createWire(timeline, params);
		Wire QB003 = createWire(timeline, params);
		Wire QB004 = createWire(timeline, params);
		Wire QB011 = createWire(timeline, params);
		Wire QB012 = createWire(timeline, params);
		Wire QB013 = createWire(timeline, params);
		Wire QB014 = createWire(timeline, params);
		Wire QB101 = createWire(timeline, params);
		Wire QB102 = createWire(timeline, params);
		Wire QB103 = createWire(timeline, params);
		Wire QB104 = createWire(timeline, params);
		Wire QB111 = createWire(timeline, params);
		Wire QB112 = createWire(timeline, params);
		Wire QB113 = createWire(timeline, params);
		Wire QB114 = createWire(timeline, params);
		Wire QAtempto001 = createWire(timeline, params);
		Wire QAtempto002 = createWire(timeline, params);
		Wire QAtempto003 = createWire(timeline, params);
		Wire QAtempto004 = createWire(timeline, params);
		Wire QAtempto011 = createWire(timeline, params);
		Wire QAtempto012 = createWire(timeline, params);
		Wire QAtempto013 = createWire(timeline, params);
		Wire QAtempto014 = createWire(timeline, params);
		Wire QAtempto101 = createWire(timeline, params);
		Wire QAtempto102 = createWire(timeline, params);
		Wire QAtempto103 = createWire(timeline, params);
		Wire QAtempto104 = createWire(timeline, params);
		Wire QBtempto001 = createWire(timeline, params);
		Wire QBtempto002 = createWire(timeline, params);
		Wire QBtempto003 = createWire(timeline, params);
		Wire QBtempto004 = createWire(timeline, params);
		Wire QBtempto011 = createWire(timeline, params);
		Wire QBtempto012 = createWire(timeline, params);
		Wire QBtempto013 = createWire(timeline, params);
		Wire QBtempto014 = createWire(timeline, params);
		Wire QBtempto101 = createWire(timeline, params);
		Wire QBtempto102 = createWire(timeline, params);
		Wire QBtempto103 = createWire(timeline, params);
		Wire QBtempto104 = createWire(timeline, params);

		createDemux2(timeline, params, A0, A1, A00, A01, A10, A11);
		createDemux2(timeline, params, B0, B1, B00, B01, B10, B11);
		createAnd41(timeline, params, B00, B01, B10, B11, WE, B00andWE, B01andWE, B10andWE, B11andWE);
		createRam2(timeline, params, A2, A3, B2, B3, B00andWE, D1, D2, D3, D4, QA001, QA011, QA101, QA111, QB001, QB011, QB101, QB111);
		createRam2(timeline, params, A2, A3, B2, B3, B01andWE, D1, D2, D3, D4, QA002, QA012, QA102, QA112, QB002, QB012, QB102, QB112);
		createRam2(timeline, params, A2, A3, B2, B3, B10andWE, D1, D2, D3, D4, QA003, QA013, QA103, QA113, QB003, QB013, QB103, QB113);
		createRam2(timeline, params, A2, A3, B2, B3, B11andWE, D1, D2, D3, D4, QA004, QA014, QA104, QA114, QB004, QB014, QB104, QB114);
		createAnd41(timeline, params, QA001, QA002, QA003, QA004, A00, QAtempto001, QAtempto002, QAtempto003, QAtempto004);
		createAndor414(timeline, params, QAtempto001, QAtempto002, QAtempto003, QAtempto004, QA011, QA012, QA013, QA014, A01, QAtempto011,
				QAtempto012, QAtempto013, QAtempto014);
		createAndor414(timeline, params, QAtempto011, QAtempto012, QAtempto013, QAtempto014, QA101, QA102, QA103, QA104, A10, QAtempto101,
				QAtempto102, QAtempto103, QAtempto104);
		createAndor414(timeline, params, QAtempto101, QAtempto102, QAtempto103, QAtempto104, QA111, QA112, QA113, QA114, A11, QA1, QA2, QA3,
				QA4);
		createAnd41(timeline, params, QB001, QB002, QB003, QB004, B00, QBtempto001, QBtempto002, QBtempto003, QBtempto004);
		createAndor414(timeline, params, QBtempto001, QBtempto002, QBtempto003, QBtempto004, QB011, QB012, QB013, QB014, B01, QBtempto011,
				QBtempto012, QBtempto013, QBtempto014);
		createAndor414(timeline, params, QBtempto011, QBtempto012, QBtempto013, QBtempto014, QB101, QB102, QB103, QB104, B10, QBtempto101,
				QBtempto102, QBtempto103, QBtempto104);
		createAndor414(timeline, params, QBtempto101, QBtempto102, QBtempto103, QBtempto104, QB111, QB112, QB113, QB114, B11, QB1, QB2, QB3,
				QB4);
	}

	private static void createSel2_4(Timeline timeline, LogicModelParameters params, Wire SA, Wire SB, Wire A1, Wire A2, Wire A3, Wire A4,
			Wire B1, Wire B2, Wire B3, Wire B4, Wire Y1, Wire Y2, Wire Y3, Wire Y4)
	{
		Wire A1temp = createWire(timeline, params);
		Wire A2temp = createWire(timeline, params);
		Wire A3temp = createWire(timeline, params);
		Wire A4temp = createWire(timeline, params);
		Wire B1temp = createWire(timeline, params);
		Wire B2temp = createWire(timeline, params);
		Wire B3temp = createWire(timeline, params);
		Wire B4temp = createWire(timeline, params);

		createNand(timeline, params, A1, SA, A1temp);
		createNand(timeline, params, A2, SA, A2temp);
		createNand(timeline, params, A3, SA, A3temp);
		createNand(timeline, params, A4, SA, A4temp);
		createNand(timeline, params, B1, SB, B1temp);
		createNand(timeline, params, B2, SB, B2temp);
		createNand(timeline, params, B3, SB, B3temp);
		createNand(timeline, params, B4, SB, B4temp);
		createNand(timeline, params, A1temp, B1temp, Y1);
		createNand(timeline, params, A2temp, B2temp, Y2);
		createNand(timeline, params, A3temp, B3temp, Y3);
		createNand(timeline, params, A4temp, B4temp, Y4);
	}

	private static void createSel3_4(Timeline timeline, LogicModelParameters params, Wire SA, Wire SB, Wire SC, Wire A1, Wire A2, Wire A3,
			Wire A4, Wire B1, Wire B2, Wire B3, Wire B4, Wire C1, Wire C2, Wire C3, Wire C4, Wire Y1, Wire Y2, Wire Y3, Wire Y4)
	{
		Wire selAB1 = createWire(timeline, params);
		Wire selAB2 = createWire(timeline, params);
		Wire selAB3 = createWire(timeline, params);
		Wire selAB4 = createWire(timeline, params);
		Wire notSelAB1 = createWire(timeline, params);
		Wire notSelAB2 = createWire(timeline, params);
		Wire notSelAB3 = createWire(timeline, params);
		Wire notSelAB4 = createWire(timeline, params);
		Wire C1temp = createWire(timeline, params);
		Wire C2temp = createWire(timeline, params);
		Wire C3temp = createWire(timeline, params);
		Wire C4temp = createWire(timeline, params);

		createSel2_4(timeline, params, SA, SB, A1, A2, A3, A4, B1, B2, B3, B4, selAB1, selAB2, selAB3, selAB4);
		createNot4(timeline, params, selAB1, selAB2, selAB3, selAB4, notSelAB1, notSelAB2, notSelAB3, notSelAB4);
		createNand(timeline, params, C1, SC, C1temp);
		createNand(timeline, params, C2, SC, C2temp);
		createNand(timeline, params, C3, SC, C3temp);
		createNand(timeline, params, C4, SC, C4temp);
		createNand(timeline, params, notSelAB1, C1temp, Y1);
		createNand(timeline, params, notSelAB2, C2temp, Y2);
		createNand(timeline, params, notSelAB3, C3temp, Y3);
		createNand(timeline, params, notSelAB4, C4temp, Y4);
	}

	private static void createXor(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire Y)
	{
		Wire AnandB = createWire(timeline, params);
		Wire w1 = createWire(timeline, params);
		Wire w2 = createWire(timeline, params);

		createNand(timeline, params, A, B, AnandB);
		createNand(timeline, params, A, AnandB, w1);
		createNand(timeline, params, B, AnandB, w2);
		createNand(timeline, params, w1, w2, Y);
	}

	@SuppressWarnings("unused") // AndGate and NotGate
	private static void createNand(Timeline timeline, LogicModelParameters params, Wire A, Wire B, Wire Y)
	{
		Wire w = new Wire(timeline, 1, params.wireTravelTime);
		new AndGate(timeline, params.gateProcessTime, w.createReadWriteEnd(), A.createReadOnlyEnd(), B.createReadOnlyEnd());
		// -2 because of delay 1 of wire and delay 1 of AndGate.
		// Math.max to avoid negative / zero delays, which are forbidden by Timeline.
		new NotGate(timeline, Math.max(1, params.gateProcessTime - 2), w.createReadOnlyEnd(), Y.createReadWriteEnd());
	}

	private static Wire createWire(Timeline timeline, LogicModelParameters params)
	{
		return new Wire(timeline, 1, params.wireTravelTime);
	}
}