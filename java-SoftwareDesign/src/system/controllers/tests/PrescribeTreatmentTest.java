package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.patients.Patient;
import system.scheduling.Priority;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.treatments.Medication;
import system.treatments.Surgery;
import system.warehouse.MedicationItemType;
import testsuite.TestWithControllers;


/**
 * PrescribeTreatmentTest.java is een test-case voor volgende use-cases:
 * 	- Prescribe Treatment
 *
 * Prescribe Treatment is verwerkt in de PrescribeTreatmentController.java.
 */

public class PrescribeTreatmentTest extends TestWithControllers {	
	/** 
	 * Initialiseert telkens de test.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
	}
	
	@Test
	public void testPrescribeCast() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		doctorController.prescribeCast(doctorController.getDiagnoses().get(0), "Arm", 15);
		doctorController.prescribeCast(doctorController.getDiagnoses().get(0), Priority.URGENT, "Arm", 15);
		assertEquals(((Cast) ((Patient) doctorController.getSelectedPatient()).getPatientFile().getDiagnoses().get(0).getTreatments().get(0)).getKeepDurationInDays(), 15);
		assertEquals(doctorController.getUndoCommands().size(), 3);
		doctorController.undoCommand(1);
		assertEquals(doctorController.getUndoCommands().size(), 2);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 3);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertFalse(adminController.getTreatmentNurses().contains(testC1Nurse1));
		adminController.advanceTime(new TimeStamp(2011,10,8,14,0));
		assertTrue(adminController.getTreatmentNurses().contains(testC1Nurse1));
	}
	
	@Test
	public void testPrescribeMedication() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		List<MedicationItemType> medItemTypeList = new ArrayList<MedicationItemType>();
		medItemTypeList.add(MedicationItemType.ACTIVATED_CARBON);
		doctorController.prescribeMedication(doctorController.getDiagnoses().get(0), "Arm", true, medItemTypeList);
		doctorController.prescribeMedication(doctorController.getDiagnoses().get(0), Priority.URGENT, "Arm", true, medItemTypeList);
		doctorController.prescribeMedication(doctorController.getDiagnoses().get(0), Priority.NORMAL, "Arm", true, medItemTypeList);
		assertEquals(((Medication) ((Patient) doctorController.getSelectedPatient()).getPatientFile().getDiagnoses().get(0).getTreatments().get(0)).getDescription(), "Arm");
		assertEquals(doctorController.getUndoCommands().size(), 4);
		doctorController.undoCommand(1);
		assertEquals(doctorController.getUndoCommands().size(), 3);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 4);
	}
	
	@Test
	public void testPrescribeSurgery() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("1 - Test");
		List<MedicationItemType> medItemTypeList = new ArrayList<MedicationItemType>();
		medItemTypeList.add(MedicationItemType.ACTIVATED_CARBON);
		doctorController.prescribeSurgery(doctorController.getDiagnoses().get(0), "Arm");
		doctorController.prescribeSurgery(doctorController.getDiagnoses().get(0), Priority.URGENT, "Arm");
		assertEquals(((Surgery) ((Patient) doctorController.getSelectedPatient()).getPatientFile().getDiagnoses().get(0).getTreatments().get(0)).getDescription(), "Arm");
		assertEquals(doctorController.getUndoCommands().size(), 3);
		doctorController.undoCommand(1);
		assertEquals(doctorController.getUndoCommands().size(), 2);
		assertEquals(doctorController.getRedoCommands().size(), 1);
		doctorController.redoCommand(0);
		assertEquals(doctorController.getUndoCommands().size(), 3);
	}
//	
//	/**
//	 * Test of de correcte exception worden gegooid bij een zekere input van description.
//	 */
//	@Test
//	public void testTreatmentDescription(){
//		try {
//			try {
//				controller.prescribeMedication(testDoctor, testDiagnosis, null, false);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No NullPointerException");
//		} catch (NullPointerException e) {}
//		try {
//			try {
//				controller.prescribeSurgery(testDoctor, testDiagnosis, null);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No NullPointerException");
//		} catch (NullPointerException e) {}
//		try {
//			try {
//				controller.prescribeCast(testDoctor, testDiagnosis, null, 0);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No NullPointerException");
//		} catch (NullPointerException e) {}
//		try {
//			try {
//				controller.prescribeMedication(testDoctor, testDiagnosis, "", false);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No IllegalArgumentException");
//		} catch (IllegalArgumentException e) {}
//		try {
//			try {
//				controller.prescribeSurgery(testDoctor, testDiagnosis, "");
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No IllegalArgumentException");
//		} catch (IllegalArgumentException e) {}
//		try {
//			try {
//				controller.prescribeCast(testDoctor, testDiagnosis, "", 0);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			fail("No IllegalArgumentException");
//		} catch (IllegalArgumentException e) {}
//		try {
//			try {
//				controller.prescribeCast(testDoctor, testDiagnosis, "NotEmpty", 0);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			try {
//				controller.prescribeSurgery(testDoctor, testDiagnosis, "NotEmtpy");
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//			try {
//				controller.prescribeMedication(testDoctor, testDiagnosis, "NotEmpty", false);
//			} catch (SchedulingException e) {
//				e.printStackTrace();
//			}
//		} catch (IllegalArgumentException e) {
//			fail("Unexpected Exception: IllegalArgumentException");
//		} catch (NullPointerException e) {
//			fail("Unexpected Exception: NullPointerException");
//		}
//	}
//	
//	/**
//	 * Test of de methode canPrescribeTreatment faalt bij een staffmember die niet in de
//	 * repository zit en een staffmember die wel in de repository zit maar geen dokter is.
//	 * Test of de methode canPrescribeTreatment lukt bij een dokter die in de repository
//	 * zit.
//	 */
//	@Test
//	public void testPreconditionHospitalDoctor() 
//			 {
//		Doctor badDoctor=new Doctor("badTestDoctor");
//		badDoctor.setLastOpenedPatientFile(testDiagnosis.getPatient().getPatientFile());
//		assertFalse(controller.canPrescribeTreatment(badDoctor, testDiagnosis));
//		
//		Doctor goodDoctor=hospital.getStaffRepository().addDoctor("testDoc");
//		goodDoctor.setLastOpenedPatientFile(testDiagnosis.getPatient().getPatientFile());
//		assertTrue(controller.canPrescribeTreatment(goodDoctor, testDiagnosis));
//	}
//	
//	/**
//	 * Test of de methode canPrescribeTreatment faalt bij een dokter die geen patientfile
//	 * geopend heeft en een dokter die de verkeerde patientfile geopend heeft.
//	 * Test of de methode canPrescribeTreatment lukt bij een dokter die de juiste patientfile
//	 * geopend heeft, horend bij de doorgegeven patient.
//	 */
//	@Test
//	public void testPreconditionDoctorOpenedPatientFile() 
//			{
//		assertFalse(controller.canPrescribeTreatment(testDoctor, testDiagnosis));
//		testDoctor.setLastOpenedPatientFile(new Patient("TestPatient 2").getPatientFile());
//		assertFalse(controller.canPrescribeTreatment(testDoctor, testDiagnosis));
//		testDoctor.setLastOpenedPatientFile(testPatient);
//		assertTrue(controller.canPrescribeTreatment(testDoctor, testDiagnosis));
//	}
//	
//	/**
//	 * Test of de methode canPrescribeTreatment faalt bij een patient die ontslagen is
//	 * uit het ziekenhuis.
//	 * Test of de methode canPrescribeTreatment lukt bij een patient die niet ontslagen is
//	 * uit het ziekenhuis.
//	 */
//	@Test
//	public void testPreconditionDesertedPatient() {
//		testDoctor.setLastOpenedPatientFile(testPatient.getPatientFile());
//		assertTrue(controller.canPrescribeTreatment(testDoctor, testDiagnosis));
//		testPatient.setDischarged(true);
//		assertFalse(controller.canPrescribeTreatment(testDoctor, testDiagnosis));
//	}
//	
}
