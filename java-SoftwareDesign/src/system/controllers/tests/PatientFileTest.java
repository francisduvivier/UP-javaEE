package system.controllers.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import client.IResult;

import system.exceptions.IllegalAccessException;
import system.exceptions.IllegalOperationException;
import system.patients.Patient;
import system.repositories.StaffType;
import system.staff.Doctor;
import testsuite.TestWithControllers;


/**
 * PatientFileTest.java is een test-case voor volgende use-cases:
 * 	- Consult Patient File
 *  - Close Patient File
 *
 * Consult Patient File en Close Patient File zijn verwerkt in PatientFileController.java.
 */

public class PatientFileTest extends TestWithControllers {

	private Patient testPatient2, testPatient3, testPatient4, testPatient5;
	
	/**
	 * Init voor PatientFileTest
	 * 
	 * Enkele patients worden toegevoegd aan de patient repository.
	 */
	@Override
	@Before
	public void testInit() {	
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.addDoctor("Frank");

		sessionController.login(testC1Nurse1, getCampus(0));
		testPatient2 = nurseController.registerNewPatient("Bob");
		testPatient3 = nurseController.registerNewPatient("Clair");
		testPatient4 = nurseController.registerNewPatient("Dennis");
		testPatient5 = nurseController.registerNewPatient("Eric");
		
	}
	
	/**
	 * We testen het consulteren van de patient files vna enkele patienten.
	 */
	@Test
	public void testConsultPatientFiles() {
		sessionController.login(testC1Doctor1, getCampus(0));
		// geldige consultatie
		doctorController.consultPatientFile(testC1Patient1);
		assertEquals(doctorController.getSelectedPatient(), testC1Patient1);
	}
	
	/**
	 * We testen het consulteren van een 'null' patient.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidConsultPatientFile() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(null);
	}
	
	/**
	 * We testen het consulteren van een patient die niet in de patient repository zit.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2ConsultPatientFile() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(new Patient("Sally"));
	}
	
	@Test (expected = IllegalAccessException.class)
	public void testInvalid3ConsultPatientFile() {
		sessionController.login(testC1Nurse1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
	}
	/**
	 * We testen of de resultaten in de patient file van testPatient1 overeenkomen met de methode geschreven voor de controller.
	 */
	@Test 
	public void testGetResults() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		List<IResult> resultsTestPatient1 = doctorController.getResults();
		assertEquals(resultsTestPatient1, testC1Patient1.getPatientFile().getResults());
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidGetResults(){
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.getResults();
	}
	
	/**
	 * We testen of we een patient file correct kunnen afsluiten.
	 */
	@Test
	public void testClosePatientFile() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testPatient5);
		assertEquals(doctorController.getSelectedPatient(), testPatient5);
		assertEquals(testPatient5.getPatientFile().isClosed(), false);
		doctorController.closePatientFile();
		assertEquals(testPatient5.getPatientFile().isClosed(), true);
		assertEquals(doctorController.getSelectedPatient(), null);
	}
	
	/**
	 * We testen of we een patient correct kunnen ontslaan
	 */
	@Test
	public void testDischargePatient() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testPatient4);
		doctorController.dischargePatient(); // ontslaat geselecteerde patient in patient file controller
		assertEquals(testPatient4.isDischarged(), true);
	}
	
	/**
	 * We testen of getNonDischargedPatients alle niet-ontslane patients uit de repositories geeft
	 */
	@Test
	public void testGetNonDischargedPatients() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testPatient5);
		doctorController.dischargePatient();
		assertEquals(testPatient5.isDischarged(), true);
		doctorController.consultPatientFile(testPatient4);
		doctorController.dischargePatient();
		assertEquals(testPatient4.isDischarged(), true);
		List<Patient> nonDischargedPatients = new ArrayList<Patient>();
		nonDischargedPatients.add(testC1Patient1);
		nonDischargedPatients.add(testPatient2);
		nonDischargedPatients.add(testPatient3);
		nonDischargedPatients.add(testC2Patient1);
		assertEquals(doctorController.getNonDischargedPatients(), nonDischargedPatients);
	}
	
	@Test
	public void testGetDoctors() {
		sessionController.login(testC1Doctor1, getCampus(0));
		assertEquals(doctorController.getDoctor(), testC1Doctor1);
		//sessionController.login(testHospitalAdmin, getCampus(0));
		assertEquals(doctorController.getDoctors(), hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR));
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testInvalidGetDoctor() {
		sessionController.login(new Doctor("Freddy"), sessionController.getCurrentCampus());
	}
	
}
