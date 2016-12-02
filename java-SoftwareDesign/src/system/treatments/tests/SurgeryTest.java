package system.treatments.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.patients.Diagnosis;
import system.patients.Patient;
import system.scheduling.Priority;
import system.staff.Doctor;
import system.treatments.Surgery;


public class SurgeryTest {
	private Surgery testSurgery1;
	private Diagnosis testDiagnosis1;
	private Doctor testDoctor1;
	private Patient testPatient1;
	
	@Before
	public void testInit() {
		testDoctor1 = new Doctor("Alice");
		testPatient1 = new Patient("David");
		testDiagnosis1 =  new Diagnosis(testDoctor1, testPatient1, "Schimmel op amandelen.");
		
	}
	
	@Test
	public void testSurgeryContructor() {
		testSurgery1 = new Surgery(testDiagnosis1, "Amandelen verwijderen");
		assertEquals(testSurgery1.getDescription(), "Amandelen verwijderen");
		assertEquals(testSurgery1.getPatient(), testPatient1);
		assertEquals(testSurgery1.getDiagnosis(), testDiagnosis1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidSurgeryConstructor() {
		testSurgery1 = new Surgery(null, "Amandelen verwijderen");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4SurgeryConstructor() {
		testSurgery1 = new Surgery(testDiagnosis1, null);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5SurgeryConstructor() {
		testSurgery1 = new Surgery(testDiagnosis1, "");
	}
	
	@Test
	public void testSurgeryContructor2() {
		testSurgery1 = new Surgery(testDiagnosis1, Priority.NORMAL, "Amandelen verwijderen");
		assertEquals(testSurgery1.getDescription(), "Amandelen verwijderen");
		assertEquals(testSurgery1.getPatient(), testPatient1);
		assertEquals(testSurgery1.getDiagnosis(), testDiagnosis1);
		assertEquals(testSurgery1.getPriority(), Priority.NORMAL);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidSurgeryConstructor2() {
		testSurgery1 = new Surgery(null, Priority.NORMAL, "Amandelen verwijderen");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4SurgeryConstructor2() {
		testSurgery1 = new Surgery(testDiagnosis1, Priority.NORMAL, null);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5SurgeryConstructor2() {
		testSurgery1 = new Surgery(testDiagnosis1, Priority.NORMAL, "");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid6SurgeryConstructor2() {
		testSurgery1 = new Surgery(testDiagnosis1, null, "Amandelen verwijderen");
	}
	
	@Test
	public void testToString() {
		testSurgery1 = new Surgery(testDiagnosis1, "Amandelen verwijderen");
		assertNotSame(testSurgery1.toString().length(), 0);
	}
}
