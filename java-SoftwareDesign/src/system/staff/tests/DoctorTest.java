package system.staff.tests;

/**
 * Unit Test Doctor.java
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.BloodAnalysis;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.staff.Doctor;


public class DoctorTest {

	private Doctor testDoctor1, testDoctor2;
	private Diagnosis testDiagnosis1, testDiagnosis2;
	private Patient testPatient;
	
	@Before
	public void testInit() {	
		testDoctor1 = new Doctor("Alice");
		testDoctor2 = new Doctor("Bob");
		testDiagnosis1 =  new Diagnosis(testDoctor2, new Patient("Clair"), "test");
		testDiagnosis2 =  new Diagnosis(testDoctor2, new Patient("Clair"), "testje");
		testPatient = new Patient("David");
		testPatient.getPatientFile().addMedicalTest(new BloodAnalysis(testPatient, "test", 15));
	}
	
	@Test
	public void testDoctorConstructor() {
		assertEquals(testDoctor1.getName(), "Alice");
		assertEquals(testDoctor2.getName(), "Bob");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1DoctorConstructor() {
		testDoctor1 = new Doctor("");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2DoctorConstructor() {
		testDoctor2 = new Doctor(null);
	}
	
	
	@Test
	public void testAddSecondOpinionDiagnosis() {
		testDoctor1.addSecondOpinionDiagnosis(testDiagnosis1);
		assertEquals(testDoctor1.getSecondOpinionDiagnoses().get(0), testDiagnosis1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidAddSecondOpinionDiagnosis() {
		testDoctor1.addSecondOpinionDiagnosis(null);
	}
	
	@Test
	public void testRemoveSecondOpinionDiagnosis() {
		testDoctor1.addSecondOpinionDiagnosis(testDiagnosis1);
		testDoctor1.removeSecondOpinionDiagnosis(testDiagnosis1);
		assertEquals(testDoctor1.getSecondOpinionDiagnoses().size(), 0);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidRemoveSecondOpinionDiagnosis() {
		testDoctor1.removeSecondOpinionDiagnosis(null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2RemoveSecondOpinionDiagnosis() {
		testDoctor1.removeSecondOpinionDiagnosis(testDiagnosis2);
	}
	
	@Test
	public void testSetLastOpenedPatientFile() {
		testDoctor1.setLastOpenedPatientFile(testPatient.getPatientFile());
		assertEquals(testDoctor1.getOpenPatientFile(), testPatient.getPatientFile());
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidGetLastOpenedPatientFile() {
		testDoctor1.setLastOpenedPatientFile(null);
		assertEquals(testDoctor1.getOpenPatientFile(), testPatient.getPatientFile());
	}
	
	@Test
	public void testInvalid2GetLastOpenedPatientFile() {
		testPatient.getPatientFile().setClosed(true);
		testDoctor1.setLastOpenedPatientFile(testPatient.getPatientFile());
		assertEquals(testDoctor1.getOpenPatientFile(), null);
	}
	
	@Test
	public void testGetOpenPatientFile() {
		assertEquals(testDoctor1.getOpenPatientFile(),null);
	}
	
	@Test
	public void testToString() {
		assertNotSame(testDoctor1.toString().length(), 0);
	}
	
}
