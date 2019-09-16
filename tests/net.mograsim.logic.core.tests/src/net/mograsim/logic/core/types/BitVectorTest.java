package net.mograsim.logic.core.types;

import static net.mograsim.logic.core.types.BitVector.*;
import static net.mograsim.logic.core.types.Bit.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class BitVectorTest
{

	@Test
	void testOfBitArray()
	{
		BitVector.of(); // should be allowed and work

		assertSame(SINGLE_U, BitVector.of(U));
		assertSame(SINGLE_X, BitVector.of(X));
		assertSame(SINGLE_0, BitVector.of(ZERO));
		assertSame(SINGLE_1, BitVector.of(ONE));
		assertSame(SINGLE_Z, BitVector.of(Z));
	}

	@Test
	void testOfBitInt()
	{
		assertEquals(BitVector.of(), BitVector.of(ONE, 0));

		assertSame(SINGLE_U, BitVector.of(U, 1));
		assertSame(SINGLE_X, BitVector.of(X, 1));
		assertSame(SINGLE_0, BitVector.of(ZERO, 1));
		assertSame(SINGLE_1, BitVector.of(ONE, 1));
		assertSame(SINGLE_Z, BitVector.of(Z, 1));

		assertEquals(BitVector.of(X, X, X), BitVector.of(X, 3));
	}

	@Test
	void testGetUnsignedValue()
	{
		assertEquals(BigInteger.valueOf(0b101), BitVector.parse("101").getUnsignedValue());
		assertEquals(BigInteger.valueOf(0b01010), BitVector.parse("01010").getUnsignedValue());
		assertEquals(BigInteger.valueOf(0), BitVector.parse("0000").getUnsignedValue());
		assertEquals(BigInteger.valueOf(0b0000000101), BitVector.parse("0000000101").getUnsignedValue());
		assertEquals(BigInteger.valueOf(0b1010000000), BitVector.parse("1010000000").getUnsignedValue());

		assertThrows(NumberFormatException.class, () -> BitVector.parse("00X1").getUnsignedValue());
	}

	@Test
	void testOfLongInt()
	{
		assertEquals(BitVector.parse("101"), BitVector.from(0b101L, 3));
		assertEquals(BitVector.parse("01010"), BitVector.from(0b01010L, 5));
		assertEquals(BitVector.parse("10101"), BitVector.from(-11L, 5));
		assertEquals(BitVector.parse("0000"), BitVector.from(0L, 4));
	}

	@Test
	void testOfBigIntegerInt()
	{
		assertEquals(BitVector.parse("101"), BitVector.from(BigInteger.valueOf(0b101), 3));
		assertEquals(BitVector.parse("01010"), BitVector.from(BigInteger.valueOf(0b01010), 5));
		assertEquals(BitVector.parse("10101"), BitVector.from(BigInteger.valueOf(-11), 5));
		assertEquals(BitVector.parse("0000"), BitVector.from(BigInteger.valueOf(0), 4));
	}

	@Test
	void testMutator()
	{
		var bv = BitVector.SINGLE_1;
		var bvm = bv.mutator();

		assertFalse(bvm.isEmpty());
		assertEquals(ONE, bvm.getLSBit(0));
		assertEquals(SINGLE_1, bvm.toBitVector());
	}

	@Test
	void testGetMSBit()
	{
		assertEquals(ONE, SINGLE_1.getMSBit(0));
		assertEquals(ONE, BitVector.of(ONE, X, X, X).getMSBit(0));
		assertEquals(ONE, BitVector.of(X, X, X, X, ONE, X).getMSBit(4));
	}

	@Test
	void testGetLSBit()
	{
		assertEquals(ONE, SINGLE_1.getLSBit(0));
		assertEquals(ONE, BitVector.of(X, X, X, ONE).getLSBit(0));
		assertEquals(ONE, BitVector.of(X, X, X, X, ONE, X).getLSBit(1));
	}

	@Test
	void testGetBits()
	{
		assertArrayEquals(new Bit[] { X, ONE, Z }, BitVector.of(X, ONE, Z).getBits());
		assertArrayEquals(new Bit[] { X, ONE, Z }, BitVector.parse("X1Z").getBits());
	}

	@Test
	void testIsBinary()
	{
		assertTrue(SINGLE_0.isBinary());
		assertTrue(SINGLE_1.isBinary());

		assertFalse(SINGLE_U.isBinary());
		assertFalse(SINGLE_X.isBinary());
		assertFalse(SINGLE_Z.isBinary());

		assertTrue(BitVector.of(ONE, ZERO, ONE, ONE, ZERO).isBinary());
		assertFalse(BitVector.of(ONE, ZERO, ZERO, X).isBinary());
	}

	@Test
	void testJoin()
	{
		// binary
		assertEquals(SINGLE_0, SINGLE_0.join(SINGLE_0));
		assertEquals(SINGLE_X, SINGLE_0.join(SINGLE_1));
		assertEquals(SINGLE_1, SINGLE_1.join(SINGLE_1));

		// other
		assertEquals(SINGLE_1, SINGLE_Z.join(SINGLE_1));
		assertEquals(SINGLE_U, SINGLE_0.join(SINGLE_U));
		assertEquals(SINGLE_X, SINGLE_X.join(SINGLE_Z));

		// higher length
		var result = BitVector.of(U, X, ZERO, ONE, Z).join(BitVector.of(ONE, ZERO, ZERO, ONE, Z));
		assertEquals(BitVector.of(U, X, ZERO, ONE, Z), result);
	}

	@Test
	void testAnd()
	{
		// binary
		assertEquals(SINGLE_0, SINGLE_0.and(SINGLE_0));
		assertEquals(SINGLE_0, SINGLE_0.and(SINGLE_1));
		assertEquals(SINGLE_1, SINGLE_1.and(SINGLE_1));

		// other
		assertEquals(SINGLE_X, SINGLE_Z.and(SINGLE_1));
		assertEquals(SINGLE_0, SINGLE_0.and(SINGLE_U));
		assertEquals(SINGLE_X, SINGLE_X.and(SINGLE_Z));

		// higher length
		var result = BitVector.of(U, X, ZERO, ONE, ONE).and(BitVector.of(ONE, ONE, ZERO, ZERO, ONE));
		assertEquals(BitVector.of(U, X, ZERO, ZERO, ONE), result);
	}

	@Test
	void testOr()
	{
		// binary
		assertEquals(SINGLE_0, SINGLE_0.or(SINGLE_0));
		assertEquals(SINGLE_1, SINGLE_0.or(SINGLE_1));
		assertEquals(SINGLE_1, SINGLE_1.or(SINGLE_1));

		// other
		assertEquals(SINGLE_1, SINGLE_Z.or(SINGLE_1));
		assertEquals(SINGLE_1, SINGLE_1.or(SINGLE_U));
		assertEquals(SINGLE_X, SINGLE_X.or(SINGLE_Z));

		// higher length
		var result = BitVector.of(U, X, ZERO, ONE, ZERO).or(BitVector.of(ZERO, ZERO, ZERO, ONE, ONE));
		assertEquals(BitVector.of(U, X, ZERO, ONE, ONE), result);
	}

	@Test
	void testXor()
	{
		// binary
		assertEquals(SINGLE_0, SINGLE_0.xor(SINGLE_0));
		assertEquals(SINGLE_1, SINGLE_0.xor(SINGLE_1));
		assertEquals(SINGLE_0, SINGLE_1.xor(SINGLE_1));

		// other
		assertEquals(SINGLE_X, SINGLE_Z.xor(SINGLE_1));
		assertEquals(SINGLE_U, SINGLE_0.xor(SINGLE_U));
		assertEquals(SINGLE_X, SINGLE_X.xor(SINGLE_Z));

		// higher length
		var result = BitVector.of(U, X, ZERO, ONE, ONE).xor(BitVector.of(ONE, ZERO, ZERO, ZERO, ONE));
		assertEquals(BitVector.of(U, X, ZERO, ONE, ZERO), result);
	}

	@Test
	void testNot()
	{
		// binary
		assertEquals(SINGLE_1, SINGLE_0.not());
		assertEquals(SINGLE_0, SINGLE_1.not());

		// other
		assertEquals(SINGLE_U, SINGLE_U.not());
		assertEquals(SINGLE_X, SINGLE_X.not());
		assertEquals(SINGLE_X, SINGLE_Z.not());

		// higher length
		var result = BitVector.of(U, X, ZERO, ONE, Z).not();
		assertEquals(BitVector.of(U, X, ONE, ZERO, X), result);
	}

	@Test
	void testLength()
	{
		assertEquals(0, BitVector.of().length());
		assertEquals(1, SINGLE_0.length());
		assertEquals(3, BitVector.of(X, X, Z).length());
	}

	@Test
	void testConcat()
	{
		assertEquals(BitVector.of(U, X), SINGLE_U.concat(SINGLE_X));
		assertEquals(BitVector.of(Z, X, U, ONE, X), BitVector.of(Z, X, U).concat(BitVector.of(ONE, X)));
	}

	@Test
	void testSubVectorInt()
	{
		assertEquals(SINGLE_0, SINGLE_0.subVector(0));
		assertEquals(BitVector.of(), SINGLE_0.subVector(1));
		assertEquals(SINGLE_0, BitVector.of(ONE, ZERO).subVector(1));
		assertEquals(BitVector.of(X, Z), BitVector.of(ZERO, U, ONE, X, Z).subVector(3));
	}

	@Test
	void testSubVectorIntInt()
	{
		assertEquals(SINGLE_0, SINGLE_0.subVector(0, 1));
		assertEquals(BitVector.of(), SINGLE_0.subVector(0, 0));
		assertEquals(SINGLE_0, BitVector.of(ONE, ZERO).subVector(1, 2));
		assertEquals(BitVector.of(ONE, X), BitVector.of(ZERO, U, ONE, X, Z).subVector(2, 4));
	}

	@Test
	void testEqualsObject()
	{
		assertEquals(SINGLE_X, SINGLE_X);
		assertNotEquals(SINGLE_0, SINGLE_1);

		assertEquals(BitVector.of(X, Z, U), BitVector.of(X, Z, U));
		assertNotEquals(BitVector.of(X, Z, U), BitVector.of(X, X, U));
	}

	@Test
	void testEqualsWithOffset()
	{
		assertTrue(SINGLE_X.equalsWithOffset(SINGLE_X, 0));
		assertFalse(SINGLE_0.equalsWithOffset(SINGLE_1, 0));

		assertTrue(BitVector.of(X, Z, U).equalsWithOffset(BitVector.of(Z, U), 1));
		assertFalse(BitVector.of(X, Z, U).equalsWithOffset(BitVector.of(X, U), 1));
		assertTrue(BitVector.of(X, Z, U).equalsWithOffset(BitVector.of(U), 2));
	}

	@Test
	void testParse()
	{
		assertEquals(SINGLE_U, BitVector.parse("U"));
		assertEquals(SINGLE_X, BitVector.parse("X"));
		assertEquals(SINGLE_0, BitVector.parse("0"));
		assertEquals(SINGLE_1, BitVector.parse("1"));
		assertEquals(SINGLE_Z, BitVector.parse("Z"));

		assertEquals(BitVector.of(X, U, Z, ONE, ZERO), BitVector.parse("XUZ10"));

		assertThrows(RuntimeException.class, () -> BitVector.parse("01BX"));
	}

	@Test
	void testIterator()
	{
		var bv = BitVector.of(U, Z, ONE, ZERO, X);
		Iterator<Bit> it = bv.iterator();

		assertEquals(U, it.next());
		assertEquals(Z, it.next());
		assertEquals(ONE, it.next());
		assertEquals(ZERO, it.next());
		assertEquals(X, it.next());
		assertFalse(it.hasNext());
	}
}
