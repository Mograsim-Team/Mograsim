package net.mograsim.logic.model.am2900.am2910;

import static net.mograsim.logic.model.am2900.am2910.TestableAm2910.Am2910_Inst.*;
import static net.mograsim.logic.core.types.Bit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.am2910.TestableAm2910.Register;
import net.mograsim.logic.model.am2900.util.DisplayStateOnFailure;

@DisplayName("Am2910 Tests")
@TestMethodOrder(OrderAnnotation.class)
public class Am2910Test
{
	private TestableAm2910 am2910 = new TestableAm2910Impl();

	@RegisterExtension
	DisplayStateOnFailure failureRule = new DisplayStateOnFailure(am2910);

	@BeforeEach
	void initialize()
	{
		createAndSetup();
		setStandardInputs();
	}

	void createAndSetup()
	{
		am2910.setup();
	}

	void setStandardInputs()
	{
		am2910.set_CC("0");
		am2910.set_CCEN("0");
		am2910.set_OE("0");
		am2910.set_RLD("1");
		am2910.setCI("1");
		am2910.setD("000000000000");
		am2910.setInstruction(JZ);
		am2910.clockOn(true);
		am2910.assertRunSuccess();
	}

	@ParameterizedTest(name = "{0}")
	@Order(1)
	@DisplayName("Direct / high level access")
	@EnumSource(Register.class)
	void testDirectAccess(Register r)
	{
		String us = U.toVector(r.size()).toString();
		String three = BitVector.from(3, r.size()).toString();

		assertEquals(us, am2910.getDirectly(r));

		am2910.setDirectly(r, three);

		assertEquals(three, am2910.getDirectly(r));
	}
}
