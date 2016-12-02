package system.patients.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.BloodAnalysis;
import system.medicaltests.MedicalTest;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.repositories.PatientType;
import system.staff.Doctor;
import system.time.TimeStamp;


/**
 * Unit Test Patient.java
 *
 */

public class PatientTest {

	private Patient testPatient;
	private Doctor testDoctor;
	private Diagnosis testDiagnosis;
	
	@Before
	public void testInit() {
		testPatient = new Patient("Alice");
		testDoctor = new Doctor("Clair");
		testDiagnosis = new Diagnosis(testDoctor, testPatient, "Vanalles");
	}
	
	@Test
	public void testPatientConstructor() {
		Patient patient = new Patient("Bob");
		assertEquals(patient.getName(), "Bob");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidPatientConstructor() {
		@SuppressWarnings("unused")
		Patient patient = new Patient(null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2PatientConstructor() {
		@SuppressWarnings("unused")
		Patient patient = new Patient("");
	}
	
	@Test
	public void testIsDischarged() {
		assertFalse(testPatient.isDischarged());
		testPatient.discharge(new TimeStamp(2012,11,21,0,0));
		assertTrue(testPatient.isDischarged());
	}
	
	@Test
	public void testResourceType() {
		assertEquals(testPatient.getResourceType(), PatientType.STAYING_PATIENT);
		testPatient.discharge(new TimeStamp(2012,11,21,0,0));
		assertEquals(testPatient.getResourceType(), PatientType.DISCHARGED_PATIENT);
	}
	
	@Test
	public void testPatientFile() {
		MedicalTest medicalTest = new BloodAnalysis(testPatient, "Test", 20);
		testPatient.getPatientFile().addMedicalTest(medicalTest);
		assertEquals(testPatient.getPatientFile().getMedicalTests().size(), 1);
		testPatient.getPatientFile().setClosed(false);
		assertFalse(testPatient.getPatientFile().isClosed());
		testPatient.getPatientFile().removeMedicalTest(medicalTest);
		assertEquals(testPatient.getPatientFile().getMedicalTests().size(), 0);
		testDoctor.setLastOpenedPatientFile(testPatient.getPatientFile());
		testDiagnosis.register();
		assertEquals(testPatient.getPatientFile().getDiagnoses().get(0), testDiagnosis);
		testPatient.getPatientFile().setClosed(true);
		assertTrue(testPatient.getPatientFile().isClosed());
	}
	
	@Test
	public void testInvalidPatientFile() {
		testPatient.discharge(new TimeStamp(2012,11,21,0,0));
		assertFalse(testDiagnosis.canBeRegistered());
	}
	
	@Test
	public void testToString() {
		assertNotSame(testPatient.toString().length(), 0);
	}
	
	@Test
	public void testSetDischargedStaying() {
		testPatient.register();
	}
	
	@Test
	public void testCanBeDischarged() {
		assertTrue(testPatient.canBeDischarged());
	}
}
