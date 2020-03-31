package net.mograsim.logic.model.am2900.am2901;

import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Dest.NOP;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Dest.QREG;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Dest.RAMF;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Func.ADD;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Func.AND;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Func.EXOR;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Func.OR;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Func.SUBR;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Src.AB;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Src.DA;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Am2901_Src.DZ;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Register.Q;
import static net.mograsim.logic.model.am2900.am2901.TestableAm2901.Register.r0;
import static net.mograsim.logic.model.am2900.util.TestUtil.signed4ToSigned32;
import static net.mograsim.logic.model.am2900.util.TestUtil.to1bitBin;
import static net.mograsim.logic.model.am2900.util.TestUtil.to4bitBin;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.mograsim.logic.model.am2900.am2901.TestableAm2901.Register;
import net.mograsim.logic.model.am2900.util.DisplayStateOnFailure;

@DisplayName("Am2901 Tests")
@TestMethodOrder(OrderAnnotation.class)
public class Am2901Test
{
	private TestableAm2901 am2901 = new TestableAm2901Impl();

	@RegisterExtension
	DisplayStateOnFailure failureRule = new DisplayStateOnFailure(am2901);

	@BeforeEach
	void initialize()
	{
		createAndSetup();
		setInputsToZero();
	}

	void createAndSetup()
	{
		am2901.setup();
	}

	void setRegistersToZero()
	{
		setInputsToZero();
		for (Register r : Register.values())
		{
			setRegisterToZero(r);
		}
	}

	void setRegisterToZero(Register r)
	{
		am2901.setD("0000");
		am2901.setSrc(DZ);
		am2901.setFunc(AND);
		setRegOutput(r);

		am2901.assertFullCycleSuccess();
	}

	void setRegOutput(Register r)
	{
		if (r == Q)
		{
			am2901.setDest(QREG);
		} else
		{
			am2901.setReg_B(r.toBitString());
			am2901.setDest(RAMF);
		}
	}

	void setInputsToZero()
	{
		am2901.setCarryIn("0");
		am2901.setQ_0("0");
		am2901.setQ_3("0");
		am2901.setRAM_0("0");
		am2901.setRAM_3("0");
		am2901.setReg_A("0000");
		am2901.setReg_B("0000");
		am2901.setD("0000");
		am2901.setSrc(AB);
		am2901.setFunc(ADD);
		am2901.setDest(QREG);
//		am2901.setNotOutEnable("0"); TODO
		am2901.clockOn(true);
		am2901.assertRunSuccess();
	}

	@ParameterizedTest(name = "{0}")
	@Order(1)
	@DisplayName("Direct / high level access")
	@EnumSource(Register.class)
	void testDirectAccess(Register r)
	{
		assertEquals("UUUU", am2901.getDirectly(r));

		am2901.setDirectly(r, "1011");

		assertEquals("1011", am2901.getDirectly(r));
	}

	@ParameterizedTest(name = "{0}")
	@Order(2)
	@DisplayName("Setting each register to 0")
	@EnumSource(Register.class)
	void testSetToZero(Register r)
	{
		assertEquals("UUUU", am2901.getDirectly(r));

		setRegisterToZero(r);

		assertEquals("0000", am2901.getDirectly(r));
		assertEquals("0000", am2901.getY());
		assertEquals("0", am2901.getCarryOut());
		assertEquals("0", am2901.getOverflow());
		assertEquals("0", am2901.getSign());
		assertEquals("1", am2901.getZero());
	}

	@Test
	@Order(3)
	@DisplayName("Setting all registers to 0")
	void testSetAllToZero()
	{
		setRegistersToZero();

		assertEquals("0000", am2901.getY());
		assertEquals("0", am2901.getCarryOut());
		assertEquals("0", am2901.getOverflow());
		assertEquals("0", am2901.getSign());
		assertEquals("1", am2901.getZero());
		assertEquals("0", am2901.getQ_0());
		assertEquals("0", am2901.getQ_3());
		assertEquals("0", am2901.getRAM_0());
		assertEquals("0", am2901.getRAM_3());

		assertAll("register values", Register.stream().map(r -> () ->
		{
			assertEquals("0000", am2901.getDirectly(r), r.name());
		}));
	}

	@Test
	@Order(4)
	@DisplayName("ADD operation")
	void testADD()
	{
		am2901.setSrc(DA);
		am2901.setFunc(ADD);
		am2901.setDest(NOP);
		am2901.setReg_A(r0.toBitString());

		assertAll(getAll4BitPairs().map(xy -> () ->
		{
			am2901.setDirectly(r0, to4bitBin(xy.x));
			am2901.setD(to4bitBin(xy.y));

			am2901.assertFullCycleSuccess();

			int res32Bit = xy.x + xy.y;
			int res4Bit = res32Bit & 0b1111;
			int res32Bit_sgn = signed4ToSigned32(xy.x) + signed4ToSigned32(xy.y);
			int res4Bit_sgn = signed4ToSigned32(res32Bit_sgn);

			assertAll("Result of " + xy.x + " + " + xy.y + " = " + res32Bit,
					() -> assertEquals(to4bitBin(res32Bit), am2901.getY(), "    Y"),
					() -> assertEquals(to1bitBin(res4Bit == 0), am2901.getZero(), "    F=0"),
					() -> assertEquals(to1bitBin(res4Bit & 0b1000), am2901.getSign(), "    F3"),
					() -> assertEquals(to1bitBin(res32Bit > 15), am2901.getCarryOut(), "    Cn+4"),
					() -> assertEquals(to1bitBin(res4Bit_sgn != res32Bit_sgn), am2901.getOverflow(), "    OVR"));
		}));
	}

	@Test
	@Order(4)
	@DisplayName("AND operation")
	void testAND()
	{
		am2901.setSrc(DA);
		am2901.setFunc(AND);
		am2901.setDest(NOP);
		am2901.setReg_A(r0.toBitString());

		assertAll(getAll4BitPairs().map(xy -> () ->
		{
			am2901.setDirectly(r0, to4bitBin(xy.x));
			am2901.setD(to4bitBin(xy.y));

			am2901.assertFullCycleSuccess();

			int res32Bit = xy.x & xy.y;

			assertAll("Result of " + xy.x + " & " + xy.y + " = " + res32Bit,
					() -> assertEquals(to4bitBin(res32Bit), am2901.getY(), "    Y"),
					() -> assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), "    F=0"),
					() -> assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), "    F3")
//					() -> assertEquals(to1bitBin(res32Bit), am2901.getCarryOut(), "    Cn+4"), // TODO
//					() -> assertEquals(to1bitBin(res32Bit), am2901.getOverflow(), "    OVR") // TODO
			);
		}));
	}

	@Test
	@Order(4)
	@DisplayName("OR operation")
	void testOR()
	{
		am2901.setSrc(DA);
		am2901.setFunc(OR);
		am2901.setDest(NOP);
		am2901.setReg_A(r0.toBitString());

		assertAll(getAll4BitPairs().map(xy -> () ->
		{
			am2901.setDirectly(r0, to4bitBin(xy.x));
			am2901.setD(to4bitBin(xy.y));

			am2901.assertFullCycleSuccess();

			int res32Bit = xy.x | xy.y;

			assertAll("Result of " + xy.x + " | " + xy.y + " = " + res32Bit,
					() -> assertEquals(to4bitBin(res32Bit), am2901.getY(), "    Y"),
					() -> assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), "    F=0"),
					() -> assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), "    F3")
//					() -> assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getCarryOut(), "    Cn+4"), // TODO
//					() -> assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getOverflow(), "    OVR") // TODO
			);
		}));
	}

	@Test
	@Order(4)
	@DisplayName("XOR operation")
	void testXOR()
	{
		am2901.setSrc(DA);
		am2901.setFunc(EXOR);
		am2901.setDest(NOP);
		am2901.setReg_A(r0.toBitString());

		assertAll(getAll4BitPairs().map(xy -> () ->
		{
			am2901.setDirectly(r0, to4bitBin(xy.x));
			am2901.setD(to4bitBin(xy.y));

			am2901.assertFullCycleSuccess();

			int res32Bit = xy.x ^ xy.y;

			assertAll("Result of " + xy.x + " ^ " + xy.y + " = " + res32Bit,
					() -> assertEquals(to4bitBin(res32Bit), am2901.getY(), "    Y"),
					() -> assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), "    F=0"),
					() -> assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), "    F3"));
		}));
	}

	@Test
	@Order(4)
	@DisplayName("SUB operation")
	void testSUB()
	{
		am2901.setSrc(DA);
		am2901.setCarryIn("1");
		am2901.setFunc(SUBR);
		am2901.setDest(NOP);
		am2901.setReg_A(r0.toBitString());

		assertAll(getAll4BitPairs().map(xy -> () ->
		{
			am2901.setDirectly(r0, to4bitBin(xy.x));
			am2901.setD(to4bitBin(xy.y));

			am2901.assertFullCycleSuccess();

			int res32Bit = xy.x - xy.y;
			int res4Bit = res32Bit & 0b1111;
			int res32Bit_sgn = signed4ToSigned32(xy.x) - signed4ToSigned32(xy.y);
			int res4Bit_sgn = signed4ToSigned32(res32Bit_sgn);

			assertAll("Result of " + xy.x + " - " + xy.y + " = " + res32Bit,
					() -> assertEquals(to4bitBin(res32Bit), am2901.getY(), "    Y"),
					() -> assertEquals(to1bitBin(res4Bit == 0), am2901.getZero(), "    F=0"),
					() -> assertEquals(to1bitBin(res4Bit & 0b1000), am2901.getSign(), "    F3"),
					() -> assertEquals(to1bitBin(xy.x >= xy.y), am2901.getCarryOut(), "    Cn+4"),
					() -> assertEquals(to1bitBin(res4Bit_sgn != res32Bit_sgn), am2901.getOverflow(), "    OVR"));
		}));
	}

	static Stream<Point> getAll4BitPairs()
	{
		return IntStream.range(0, 16).boxed().flatMap(x -> IntStream.range(0, 16).mapToObj(y -> new Point(x, y)));
	}
}
