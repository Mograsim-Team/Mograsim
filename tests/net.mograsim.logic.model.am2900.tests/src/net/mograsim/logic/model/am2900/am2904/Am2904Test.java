package net.mograsim.logic.model.am2900.am2904;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.mograsim.logic.model.am2900.am2904.TestableAm2904.Am2904_Carry;
import net.mograsim.logic.model.am2900.am2904.TestableAm2904.Am2904_Inst;
import net.mograsim.logic.model.am2900.am2904.TestableAm2904.Am2904_ShiftDir;
import net.mograsim.logic.model.am2900.am2904.TestableAm2904.CompleteStatus;
import net.mograsim.logic.model.am2900.am2904.TestableAm2904.Register;
import net.mograsim.logic.model.am2900.util.DisplayStateOnFailure;

@DisplayName("Am2904 Tests")
@TestMethodOrder(OrderAnnotation.class)
public class Am2904Test
{
	private TestableAm2904 am2904 = new TestableAm2904Impl();

	@RegisterExtension
	DisplayStateOnFailure failureRule = new DisplayStateOnFailure(am2904);

	@BeforeEach
	void initialize()
	{
		createAndSetup();
		setStandardInputs();
	}

	void createAndSetup()
	{
		am2904.setup();
	}

	void setStandardInputs()
	{
		am2904.set_CEµ("0");
		am2904.set_CEM("0");
		am2904.setI("0000");
		am2904.set_E("0000");
		am2904.set_OEY("1");
		am2904.set_OECT("0");
		am2904.setCarry(Am2904_Carry.CI0);
		am2904.setCX("0");
		am2904.setI10(Am2904_ShiftDir.RIGHT);
		am2904.setShiftCode("0000");
		am2904.setInstruction(Am2904_Inst.Load_Load_I_Z);
		am2904.setSIO0("Z");
		am2904.setSIO3("Z");
		am2904.setQIO0("Z");
		am2904.setQIO3("Z");
		am2904.set_SE("1");
		am2904.setY("ZZZZ");
		am2904.clockOn(true);
		am2904.assertRunSuccess();
	}

	@ParameterizedTest(name = "{0}")
	@Order(1)
	@DisplayName("Direct / high level access")
	@EnumSource(Register.class)
	void testDirectAccess(Register r)
	{
		assertEquals("U", am2904.getDirectly(r));

		am2904.setDirectly(r, "1");

		assertEquals("1", am2904.getDirectly(r));
	}

	@Test
	@Order(2)
	void testBasicStateAndOutputs()
	{
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getC0());
		assertEquals("0", am2904.getCT());
		assertEquals("Z", am2904.getQIO0());
		assertEquals("Z", am2904.getQIO3());
		assertEquals("Z", am2904.getSIO0());
		assertEquals("Z", am2904.getSIO3());
		assertEquals("ZZZZ", am2904.getY());
	}

	@Test
	@Order(3)
	void testSimpleLoadTestIZ()
	{
		am2904.setInstruction(Am2904_Inst.Load_Load_I_Z);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("1000");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());

		am2904.setInstruction(Am2904_Inst.Load_Load_I_notZ);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0000");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());
	}

	@Test
	@Order(3)
	void testSimpleLoadTestIC()
	{
		am2904.setInstruction(Am2904_Inst.Load_Load_I_C);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0100");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());

		am2904.setInstruction(Am2904_Inst.Load_Load_I_notC);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0000");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());
	}

	@Test
	@Order(3)
	void testSimpleLoadTestIN()
	{
		am2904.setInstruction(Am2904_Inst.Load_Load_I_N);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0010");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());

		am2904.setInstruction(Am2904_Inst.Load_Load_I_notN);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0000");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());
	}

	@Test
	@Order(3)
	void testSimpleLoadTestIOVR()
	{
		am2904.setInstruction(Am2904_Inst.Load_Load_I_OVR);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0001");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());

		am2904.setInstruction(Am2904_Inst.Load_Load_I_notOVR);
		am2904.assertFullCycleSuccess();

		assertEquals("0", am2904.getCT());

		am2904.setI("0000");
		am2904.assertFullCycleSuccess();

		assertEquals("1", am2904.getCT());
	}

	@Test
	@Order(3)
	void testRegisterContentAfterLoadLoad()
	{
		am2904.setInstruction(Am2904_Inst.Load_Load_I_Z);
		am2904.assertFullCycleSuccess();

		String[] statusValues = { "0001", "0010", "0100", "1000", "0000", "1111" };

		for (String status : statusValues)
		{
			am2904.setI(status);
			am2904.assertFullCycleSuccess();
			assertEquals(status, am2904.getDirectly(CompleteStatus.micro));
			assertEquals(status, am2904.getDirectly(CompleteStatus.MAKRO));
		}
	}
}