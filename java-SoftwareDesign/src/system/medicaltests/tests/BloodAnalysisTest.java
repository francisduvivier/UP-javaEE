package system.medicaltests.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.BloodAnalysis;
import system.patients.Patient;
import system.scheduling.Priority;


public class BloodAnalysisTest {

	private BloodAnalysis testBloodAnalysis;
	private Patient testPatient;
	
	
	@Before
	public void testInit() {
		testPatient = new Patient("Alice");
	}
	
	@Test
	public void testBloodAnalysisConstructor() {
		testBloodAnalysis = new BloodAnalysis(testPatient, "Witte Bloedcellen", 15);
		assertEquals(testBloodAnalysis.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor() {
		testBloodAnalysis = new BloodAnalysis(null, "Witte Bloedcellen", 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor() {
		testBloodAnalysis = new BloodAnalysis(testPatient, null, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3Constructor() {
		testBloodAnalysis = new BloodAnalysis(testPatient, "", 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid4Constructor() {
		testBloodAnalysis = new BloodAnalysis(testPatient, "Witte Bloedcellen", -1);
	}
	
	@Test
	public void testBloodAnalysisConstructor2() {
		testBloodAnalysis = new BloodAnalysis(testPatient, Priority.NORMAL, "Witte Bloedcellen", 15);
		assertEquals(testBloodAnalysis.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor2() {
		testBloodAnalysis = new BloodAnalysis(null, Priority.NORMAL, "Witte Bloedcellen", 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor2() {
		testBloodAnalysis = new BloodAnalysis(testPatient, Priority.NORMAL, null, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3Constructor2() {
		testBloodAnalysis = new BloodAnalysis(testPatient, Priority.NORMAL, "", 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid4Constructor2() {
		testBloodAnalysis = new BloodAnalysis(testPatient, Priority.NORMAL, "Witte Bloedcellen", -1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5Constructor2() {
		testBloodAnalysis = new BloodAnalysis(testPatient, null, "Witte Bloedcellen", -1);
	}
	
	@Test
	public void testToString() {
		testBloodAnalysis = new BloodAnalysis(testPatient, "Witte Bloedcellen", 15);
		assertNotSame(testBloodAnalysis.toString().length(),0);
	}	
	
	@Test
	public void testGetFocus() {
		testBloodAnalysis = new BloodAnalysis(testPatient, "Witte Bloedcellen", 15);
		assertEquals(testBloodAnalysis.getFocus(), "Witte Bloedcellen");
	}
}
