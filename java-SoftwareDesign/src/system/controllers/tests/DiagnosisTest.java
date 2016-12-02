package system.controllers.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import client.IDiagnosis;
import client.IStaffMember;

import system.exceptions.IllegalAccessException;
import testsuite.TestWithControllers;


/**
* DiagnosisTest.java is een test-case voor volgende use-cases:
*  - Enter Diagnosis
*  - Approve Diagnosis
*
* Enter Diagnosis & Approve Diagnosis zijn verwerkt in AdministratorController.java.
*/

public class DiagnosisTest extends TestWithControllers{
	private IStaffMember testDoctor2;

	/**
	 * Initialiseert telkens de test.
	 * 
	 * @throws PatientIDNotFoundException
	 *             De patientID kan niet gevonden worden in de repository.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		testDoctor2 = adminController.addDoctor("Bob");
		sessionController.logOut();
	}
	
	@Test
	public void testEnterDiagnosis() {
		try {
			doctorController.enterDiagnosis("Invalid Access");
			fail("Exception niet geworpen");
		} catch (IllegalAccessException e) {}
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		assertEquals(doctorController.getUndoCommands().size(), 0);
		doctorController.enterDiagnosis("1 - Test");
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertNotSame(doctorController.getUndoCommands().get(0), 0);
		doctorController.undoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 0);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertEquals(doctorController.getRedoCommands().size(), 0);
		
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidEnterDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(null);
	}
	
	@Test
	public void testGetDiagnoses() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		doctorController.enterDiagnosis("2 - Test");
		doctorController.enterDiagnosis("3 - Test");
		assertEquals(doctorController.getDiagnoses().size(), 3);
	}
	
	@Test
	public void testGetDiagnosisDetails() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0).getDescription(), "1 - Test");
		assertEquals(doctorController.getDiagnosisDetails(testC1Patient1.getPatientFile().getDiagnoses().get(0)), "1 - Test");
	}
	
	@Test
	public void testEnterDiagonisWithApprovingDoctor() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "A - Test");
		assertEquals(doctorController.getDiagnoses().size(), 1);
	}
	
	@Test
	public void testApproveDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "A - Test");
		sessionController.logOut();
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.approveDiagnosis(doctorController.getSecondOpinionDiagnoses().get(0));
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertNotSame(doctorController.getUndoCommands().get(0), 0);
		doctorController.undoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 0);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertEquals(doctorController.getRedoCommands().size(), 0);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidApproveDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "A - Test");
		doctorController.approveDiagnosis(null);
	}
	
	@Test
	public void testGetSecondOpinionDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "A - Test");
		IDiagnosis testDiagnosis=doctorController.getDiagnoses().get(0);
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		assertTrue(doctorController.getSecondOpinionDiagnoses().contains(testDiagnosis));
		
	}

	@Test
	public void testDenyDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "A - Test");
		sessionController.logOut();
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.denyDiagnosis(testC1Patient1.getPatientFile().getDiagnoses().get(0), "Test Diagnosis");
		doctorController.closePatientFile();
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0).getDescription(), "Test Diagnosis");
	}
}
