package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import client.IStaffMember;

import system.scheduling.Priority;
import system.time.TimeStamp;
import testsuite.TestWithControllers;


/**
 * OrderMedicalTestTest.java is een test-case voor volgende use-cases:
 * 	- Order Medical Test
 *
 * Order Medical Test is verwerkt in de DoctorController.java.
 */

public class OrderMedicalTestTest extends TestWithControllers {
	
	private IStaffMember testDoctor2;
	
	/**
	 * Initialiseert telkens de test.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		testDoctor2 = adminController.addDoctor("Bob");
		sessionController.logOut();
	}
	
	/**
	 * We testen of de order blood analysis methode medische tests toevoegt aan de
	 * patient file van de geconsulteerde patient van een dokter.
	 */
	@Test
	public void testOrderBloodAnalysis() {
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 0);
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.orderBloodAnalysis("Allergie", 5);
		doctorController.orderBloodAnalysis(Priority.URGENT,"Ziekte", 17);
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 2);
	}
	
	/**
	 * We testen of de order ultrasound scan methode medische tests toevoegt aan de
	 * patient file van de geconsulteerde patient van een dokter.
	 */
	@Test
	public void testOrderUltrasoundSCan() {
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 0);
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.orderUltrasoundScan("Test", true, true);
		doctorController.orderUltrasoundScan(Priority.URGENT,"Test", true, true);
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 2);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertFalse(adminController.getMedicalTestNurses().contains(testC1Nurse1));
		adminController.advanceTime(new TimeStamp(2011,10,8,14,0));
		assertTrue(adminController.getMedicalTestNurses().contains(testC1Nurse1));
	}
	
	/**
	 * We testen of de order x-ray scan methode medische tests toevoegt aan de
	 * patient file van de geconsulteerde patient van een dokter.
	 */
	@Test
	public void testOrderXRayScan() {
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 0);
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.orderXRayScan("Test", 2, 2);
		doctorController.orderXRayScan(Priority.URGENT,"Test", 2, 2);
		assertEquals(testC1Patient1.getPatientFile().getMedicalTests().size(), 2);
	}

	/**
	 * We testen of we medical tests kunnen bestellen als de
	 * dokter geen patient consulteert.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalidOrderMedicalTest() {
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.orderBloodAnalysis("Allergie", 5);
	}
	
}
