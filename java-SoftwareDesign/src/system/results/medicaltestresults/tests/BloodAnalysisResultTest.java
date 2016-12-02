package system.results.medicaltestresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.medicaltestresults.BloodAnalysisResult;


/**
 * Unit Test BloodAnalysisResult.java
 *
 */

public class BloodAnalysisResultTest {

	private BloodAnalysisResult testBloodAnalysis;
	
	@Test
	public void testBloodAnalysisResultConstructor() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, 3, 4);
	}
	 
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid1BloodAnalysisResultConstructor() {
		testBloodAnalysis = new BloodAnalysisResult(-1, 2, 3, 4);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2BloodAnalysisResultConstructor() {
		testBloodAnalysis = new BloodAnalysisResult(1, -2, 3, 4);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3BloodAnalysisResultConstructor() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, -3, 4);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid4BloodAnalysisResultConstructor() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, 3, -4);
	}
	
	@Test
	public void testToString() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, 3, 4);
		assertNotSame(testBloodAnalysis.toString(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, 3, 4);
		assertNotSame(testBloodAnalysis.getDetails(), 0);
	}
	
	@Test
	public void testResourceType() {
		testBloodAnalysis = new BloodAnalysisResult(1, 2, 3, 4);
		assertEquals(testBloodAnalysis.getResultType(), ResultType.BLOODANALYSIS_RESULT);
	}
	
}
