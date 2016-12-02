package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import client.IStaffMember;

import system.exceptions.IllegalOperationException;
import system.medicaltests.XRayScan;
import system.patients.Patient;
import system.staff.Doctor;
import system.treatments.Cast;
import testsuite.TestWithControllers;


/**
 * Undo en Redo wordt voor elk geval al afzonderlijk getest in de hiervoor
 * behorende testklasse.
 * 
 * We testen het gebruik van Undo en Redo voor Prescribe Cast ter illustratie.
 * 
 */

public class UndoTest extends TestWithControllers {
	protected IStaffMember testDoctor2;
	/**
	 * Initialiseert telkens de test.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		testDoctor2 = adminController.addDoctor("Da Doctor");
		sessionController.logOut();
	}

	/**
	 * We testen of we prescribe treatment correct kunnen undo'en en redo'en.
	 */
	@Test
	public void testPrescribeTreatment() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		doctorController.prescribeCast(doctorController.getDiagnoses().get(0),
				"Arm", 15);
		assertEquals(
				((Cast) ((Patient) doctorController.getSelectedPatient()).getPatientFile()
						.getDiagnoses().get(0).getTreatments().get(0))
						.getKeepDurationInDays(),
				15);
		assertEquals(doctorController.getUndoCommands().size(), 2);
		doctorController.undoCommand(1);
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 2);
	}

	/**
	 * We testen of we order medical test correct kunnen undo'en en redo'en.
	 */
	@Test
	public void testOrderMedicalTest() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.orderXRayScan("Foo bone", 10, 2);
		XRayScan xRay = new XRayScan(testC1Patient1, "Bar Bone", 10, 2);
		assertEquals(((Patient) doctorController.getSelectedPatient()).getPatientFile()
				.getMedicalTests().get(0).getDuration(), xRay.getDuration());
		assertEquals(doctorController.getUndoCommands().size(), 1);
		doctorController.undoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 0);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 1);
	}

	@Test
	public void testEnterDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Foo bone broke");
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0),
				"Foo bone", 100);
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0),
				"Bar bone", 100);
		assertEquals(doctorController.getUndoCommands().size(), 3);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 2);
		doctorController.undoCommand(0);
		assertTrue(testC1Patient1.getPatientFile().getDiagnoses().isEmpty());
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 2);
	}
	
	/**
	 * We testen of we prescribe cast correct kunnen undo'en en redo'en.
	 */
	@Test
	public void testApproveDiagnosis() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2, "Foo bone broke");
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0),
				"Foo bone", 100);
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0),
				"Bar bone", 100);
		assertEquals(doctorController.getUndoCommands().size(), 3);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);
		doctorController.undoCommand(0);
		assertTrue(testC1Patient1.getPatientFile().getDiagnoses().isEmpty());
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);
		assertEquals(doctorController.getUndoCommands().size(), 2);
		doctorController.redoCommand(0);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);

		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.approveDiagnosis(((Doctor) testDoctor2)
				.getSecondOpinionDiagnoses().get(0));
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 2);
		sessionController.login(testC1Doctor1, getCampus(0));
		try {
			doctorController.undoCommand(2);
			fail("Test Failed");
		} catch (IllegalOperationException e) {
		}
		assertEquals(doctorController.getUndoCommands().size(), 3);
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.undoCommand(0);
		assertTrue(((Doctor) testDoctor2).getSecondOpinionDiagnoses().get(0)
				.needsApproval());
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 2);
		assertEquals(doctorController.getUndoCommands().size(), 1);
		assertTrue(((Doctor) testDoctor2).getSecondOpinionDiagnoses().isEmpty());
		
		doctorController.undoCommand(0);
		assertEquals(testC1Patient1.getSchedule().getSchedule().size(), 0);
		doctorController.denyDiagnosis(((Doctor) testDoctor2).getSecondOpinionDiagnoses()
				.get(0), "Bar bone broke");
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0), 
				"Bar bone", 100);
		doctorController.prescribeCast(testC1Patient1.getPatientFile().getDiagnoses().get(0), 
				"Bar bone", 50);
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0).getDescription(), "Bar bone broke");
		assertEquals(((Doctor) testC1Doctor1).getSecondOpinionDiagnoses().get(0).getDescription(), "Bar bone broke");
		assertTrue(((Doctor) testDoctor2).getSecondOpinionDiagnoses().isEmpty());
		doctorController.undoCommand(0);
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0).getDescription(), "Foo bone broke");
		assertEquals(((Doctor) testDoctor2).getSecondOpinionDiagnoses().get(0).getDescription(), "Foo bone broke");
		assertTrue(((Doctor) testC1Doctor1).getSecondOpinionDiagnoses().isEmpty());
		doctorController.redoCommand(1);
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0).getDescription(), "Bar bone broke");
		assertEquals(((Doctor) testC1Doctor1).getSecondOpinionDiagnoses().get(0).getDescription(), "Bar bone broke");
		assertTrue(((Doctor) testDoctor2).getSecondOpinionDiagnoses().isEmpty());
	}
}
