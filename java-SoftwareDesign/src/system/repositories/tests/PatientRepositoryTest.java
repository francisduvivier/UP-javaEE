package system.repositories.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.patients.Patient;
import system.repositories.PatientRepository;
import system.repositories.PatientType;
import system.time.Time;


/**
 * Unit Test PatientRepository.java
 *
 */

public class PatientRepositoryTest {

	private PatientRepository patientRepository;
	private Patient testPatient3;
	
	@Before
	public void testInit() {
		patientRepository = new PatientRepository(new Time(2012,11,21,0,0));
		patientRepository.addPatient(new Patient("Alice"));
		patientRepository.addPatient(new Patient("Bob"));
		patientRepository.addPatient(new Patient("Clair"));
		patientRepository.addPatient(new Patient("David"));
		testPatient3 = patientRepository.getRegisteredPatients().get(2);
	}
	
	@Test
	public void testGetDischargedPatients() {
		patientRepository.dischargePatient(testPatient3);
		assertTrue(testPatient3.isDischarged());
		assertEquals(patientRepository.getDischargedPatients().get(0), testPatient3);
	}
	
	@Test
	public void testGetResources() {
		patientRepository.dischargePatient(testPatient3);
		assertEquals(patientRepository.getResources(PatientType.DISCHARGED_PATIENT).get(0), testPatient3);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidDischargePatient() {
		patientRepository.dischargePatient(new Patient("Eric"));
	}
	
	@Test
	public void testGetNonDischargedPatients() {
		assertEquals(patientRepository.getNonDischargedPatients().size(), 4);
		patientRepository.dischargePatient(testPatient3);
		assertEquals(patientRepository.getNonDischargedPatients().size(), 3);
	}
	
}
