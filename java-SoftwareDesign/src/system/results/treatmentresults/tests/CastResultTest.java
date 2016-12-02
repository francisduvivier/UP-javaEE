package system.results.treatmentresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.treatmentresults.CastResult;


/**
 * Unit Test CastResult.java
 *
 */

public class CastResultTest {

	private CastResult testCastResult;
	
	@Test
	public void testCastResultConstructor() {
		testCastResult = new CastResult("Test");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidCastResultConstructor() {
		testCastResult = new CastResult(null);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2CastResult() {
		testCastResult = new CastResult("");
	}
	
	@Test
	public void testResultType() {
		testCastResult = new CastResult("Test");
		assertEquals(testCastResult.getResultType(), ResultType.CAST_RESULT);
	}
	
	@Test
	public void testToString() {
		testCastResult = new CastResult("Test");
		assertNotSame(testCastResult.toString().length(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testCastResult = new CastResult("Test");
		assertNotSame(testCastResult.getDetails().length(), 0);
	}
	
}
