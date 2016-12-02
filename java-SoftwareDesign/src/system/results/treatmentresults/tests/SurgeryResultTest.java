package system.results.treatmentresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.treatmentresults.SurgeryResult;


public class SurgeryResultTest {

private SurgeryResult testSurgeryResult;
	
	@Test
	public void testSurgeryResultConstructor() {
		testSurgeryResult = new SurgeryResult("Test", "Test");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidSurgeryResultConstructor() {
		testSurgeryResult = new SurgeryResult(null, "Test");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2SurgeryResult() {
		testSurgeryResult = new SurgeryResult("", "Test");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3SurgeryResult() {
		testSurgeryResult = new SurgeryResult("", null);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4SurgeryResult() {
		testSurgeryResult = new SurgeryResult("", "");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5SurgeryResult() {
		testSurgeryResult = new SurgeryResult("Test", null);
	}
	
	@Test
	public void testResultType() {
		testSurgeryResult = new SurgeryResult("Test", "Test");
		assertEquals(testSurgeryResult.getResultType(), ResultType.SURGERY_RESULT);
	}
	
	@Test
	public void testToString() {
		testSurgeryResult = new SurgeryResult("Test", "Test");
		assertNotSame(testSurgeryResult.toString().length(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testSurgeryResult = new SurgeryResult("Test", "Test");
		assertNotSame(testSurgeryResult.getDetails().length(), 0);
	}
	
}
