package net.mograsim.logic.ui.am2900;

import static net.mograsim.logic.ui.am2900.TestUtil.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Dest.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Func.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Src.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Register.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import net.mograsim.logic.ui.am2900.TestableAm2901.Register;

@TestMethodOrder(OrderAnnotation.class)
public class Am2901Test
{
	private TestableAm2901 am2901;

	@BeforeEach
	void initialize()
	{
		createAndSetup();
		setInputsToZero();
	}

	void createAndSetup()
	{
		am2901 = new TestableAm2901Impl();
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
		System.out.println("Setting reg " + r + " to zero");

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

	@Order(1)
	@ParameterizedTest
	@ArgumentsSource(TestableAm2901.RegisterProvider.class)
	void testDirectAccess(Register r)
	{
		assertEquals("UUUU", am2901.getDirectly(r));

		am2901.setDirectly(r, "1011");

		assertEquals("1011", am2901.getDirectly(r));
	}

	@Order(2)
	@ParameterizedTest
	@ArgumentsSource(TestableAm2901.RegisterProvider.class)
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

	@Order(3)
	@Test
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

	@Order(4)
	@Test
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

			String desc = xy.x + " + " + xy.y + " = " + res4Bit + ": ";

			assertEquals(to4bitBin(res4Bit & 0b1111), am2901.getY(), desc + "Y");
			assertEquals(to1bitBin(res4Bit == 0), am2901.getZero(), desc + "F=0");
			assertEquals(to1bitBin(res4Bit & 0b1000), am2901.getSign(), desc + "F3");
			assertEquals(to1bitBin(res32Bit & 0b1_0000), am2901.getCarryOut(), desc + "Cn+4");
			assertEquals(to1bitBin(res4Bit_sgn != res32Bit_sgn), am2901.getOverflow(), desc + "OVR");
		}));
	}

	@Order(4)
	@Test
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

			String desc = xy.x + " & " + xy.y + " = " + res32Bit + ": ";

			assertEquals(to4bitBin(res32Bit), am2901.getY(), desc + "Y");
			assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), desc + "F=0");
			assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), desc + "F3");
//			assertEquals(to1bitBin(res32Bit), am2901.getCarryOut(), desc + "Cn+4"); // TODO
//			assertEquals(to1bitBin(res32Bit), am2901.getOverflow(), desc + "OVR"); // TODO
		}));
	}

	@Order(4)
	@Test
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

			String desc = xy.x + " | " + xy.y + " = " + res32Bit + ": ";

			assertEquals(to4bitBin(res32Bit), am2901.getY(), desc + "Y");
			assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), desc + "F=0");
			assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), desc + "F3");
//			assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getCarryOut(), desc + "Cn+4"); // TODO
//			assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getOverflow(), desc + "OVR"); // TODO
		}));
	}

	@Order(4)
	@Test
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

			String desc = xy.x + " ^ " + xy.y + " = " + res32Bit + ": ";

			assertEquals(to4bitBin(res32Bit), am2901.getY(), desc + "Y");
			assertEquals(to1bitBin(res32Bit == 0), am2901.getZero(), desc + "F=0");
			assertEquals(to1bitBin(res32Bit & 0b1000), am2901.getSign(), desc + "F3");
//			assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getCarryOut(), desc + "Cn+4"); // TODO
//			assertEquals(to1bitBin(res32Bit != 0b1111), am2901.getOverflow(), desc + "OVR"); // TODO
		}));
	}

	@Order(4)
	@Test
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

			String desc = xy.x + " - " + xy.y + " = " + res4Bit + ": ";

			assertEquals(to4bitBin(res4Bit & 0b1111), am2901.getY(), desc + "Y");
			assertEquals(to1bitBin(res4Bit == 0), am2901.getZero(), desc + "F=0");
			assertEquals(to1bitBin(res4Bit & 0b1000), am2901.getSign(), desc + "F3");
			assertEquals(to1bitBin(xy.x >= xy.y), am2901.getCarryOut(), desc + "Cn+4");
			assertEquals(to1bitBin(res4Bit_sgn != res32Bit_sgn), am2901.getOverflow(), desc + "OVR");
		}));
	}

	static Stream<Point> getAll4BitPairs()
	{
		return IntStream.range(0, 16).boxed().flatMap(x -> IntStream.range(0, 16).mapToObj(y -> new Point(x, y)));
	}
}
