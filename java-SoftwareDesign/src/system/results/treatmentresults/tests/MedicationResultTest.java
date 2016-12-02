package system.results.treatmentresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.treatmentresults.MedicationResult;


/**
 * Unit Test MedicationResult.java
 *
 */

public class MedicationResultTest {

private MedicationResult testMedicationResult;
	
	@Test
	public void testMedicationResultConstructor() {
		testMedicationResult = new MedicationResult("Test", false);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidMedicationResultConstructor() {
		testMedicationResult = new MedicationResult(null, true);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2MedicationResult() {
		testMedicationResult = new MedicationResult("", true);
	}
	
	@Test
	public void testResultType() {
		testMedicationResult = new MedicationResult("Test", true);
		assertEquals(testMedicationResult.getResultType(), ResultType.MEDICATION_RESULT);
	}
	
	@Test
	public void testToString() {
		testMedicationResult = new MedicationResult("Test", true);
		assertNotSame(testMedicationResult.toString().length(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testMedicationResult = new MedicationResult("Test", true);
		assertNotSame(testMedicationResult.getDetails().length(), 0);
	}
	
}
