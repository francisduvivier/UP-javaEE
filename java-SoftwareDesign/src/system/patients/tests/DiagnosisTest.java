package system.patients.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.exceptions.SchedulingException;
import system.patients.Diagnosis;
import system.staff.Doctor;
import system.treatments.Surgery;
import system.treatments.Treatment;
import testsuite.TestWithControllers;


/**
 * Unit Test Diagnosis.java
 *
 */

public class DiagnosisTest extends TestWithControllers{
	private Diagnosis testDiagnosis;
	private Doctor testDoctor2;
	
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		testDoctor2=(Doctor) adminController.addDoctor("Second Doctor");
	}
	
	@Test
	public void testDiagnosisConstructor() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		assertEquals(testDiagnosis.getPatient(), testC1Patient1);
		assertEquals(testDiagnosis.getDescription(), "Gebroken Arm");
	}
	
	@Test
	public void testConstructor() {
		try {
			new Diagnosis(null, testC1Patient1, "Gebroken Arm");
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, null, "Gebroken Arm");
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, null);
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis(null, testDoctor2, testC1Patient1, "Gebroken Arm");
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, null, testC1Patient1, "Gebroken Arm");
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, testDoctor2, null, "Gebroken Arm");
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, testDoctor2, testC1Patient1, null);
			fail("Test Failed");
		} catch (NullPointerException e) {}
		try {
			new Diagnosis((Doctor) testC1Doctor1, testDoctor2, testC1Patient1, "");
			fail("Description is null");
		} catch (IllegalArgumentException e) {}
		
		new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		new Diagnosis((Doctor) testC1Doctor1, testDoctor2, testC1Patient1, "Gebroken Arm");
	}
	
	@Test
	public void testRegister() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis=new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "description");
		testDiagnosis.register();
		assertEquals(testDiagnosis.getPatient(), testC1Patient1);
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().get(0), testDiagnosis);
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().size(), 1);
		testDiagnosis.unregister();
		assertEquals(testC1Patient1.getPatientFile().getDiagnoses().size(), 0);
	}
	
	@Test
	public void testRegisterFail() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		testDiagnosis=new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "description");
		try {
			testDiagnosis.register();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
	}
	
	@Test
	public void testApprovingDoctor() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testDoctor2, testC1Patient1, "Gebroken Arm");
		testDoctor2.setLastOpenedPatientFile(testC1Patient1.getPatientFile());
		testDoctor2.addSecondOpinionDiagnosis(testDiagnosis);
		assertTrue(testDiagnosis.canBeApprovedOrDenied());
	}
	
	@Test 
	public void testInvalidApprovingDoctor() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, new Doctor("bad doctor"), testC1Patient1, "Gebroken Arm");
		assertFalse(testDiagnosis.canBeApprovedOrDenied());
	}
	
	@Test
	public void testApprove() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2,"nieuwe diagnose");
		testDiagnosis=testC1Patient1.getPatientFile().getDiagnoses().get(0);
		try {
			testDiagnosis.approve();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
	}
	
	@Test
	public void testApproveFail() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2,"nieuwe diagnose");
		testDiagnosis=testC1Patient1.getPatientFile().getDiagnoses().get(0);
		sessionController.logOut();
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.approve();
	}
	
	@Test
	public void testHasApprovingDoctor() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testDoctor2, testC1Patient1, "Gebroken Arm");
		assertTrue(testDiagnosis.hasAsApprovingDoctor(testDoctor2));
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		assertFalse(testDiagnosis.hasAsApprovingDoctor((Doctor) testC1Doctor1));
	}
	
	@Test
	public void testDeny() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2,"eerste diagnose");
		testDiagnosis=testC1Patient1.getPatientFile().getDiagnoses().get(0);
		sessionController.logOut();
		
		assertEquals(testDoctor2.getSecondOpinionDiagnoses().size(), 1);
		
		sessionController.login(testDoctor2, sessionController.getCurrentCampus());
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.deny();
		assertEquals(testDoctor2.getSecondOpinionDiagnoses().size(), 0);
	}
	
	@Test
	public void testDenyFail() {
		sessionController.login((Doctor) testC1Doctor1, getCampus(0));	
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis(testDoctor2,"eerste diagnose");
		testDiagnosis=testC1Patient1.getPatientFile().getDiagnoses().get(0);
		
		try {
			testDiagnosis.deny();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
	}
	
	@Test
	public void testTreatments() throws SchedulingException {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		Treatment treatment = new Surgery(testDiagnosis, "IJzer in botten bevestigen");
		treatment.store();
		assertEquals(testDiagnosis.getTreatments().get(0), treatment);
		treatment.cancel();
		assertEquals(testDiagnosis.getTreatments().size(), 0);
	}
	
	@Test 
	public void testAddTreatment() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		try{
		Treatment badTreatment = new Surgery(new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "other diagnosis"),"Rechtervoet amputeren");
		testDiagnosis.addTreatment(badTreatment);
		fail("IllegalArgumentException verwacht");
		}catch (IllegalArgumentException e) {
			//Test is gelukt
		}
		Treatment goodTreatment=new Surgery(testDiagnosis,"Rechtervoet amputeren"); 
		testDiagnosis.addTreatment(goodTreatment);
		assertEquals(testDiagnosis.getTreatments().get(0),goodTreatment);
		
	}
	
	@Test
	public void testRemoveTreatment() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		Treatment treatment = new Surgery(testDiagnosis, "IJzer in botten bevestigen");
		treatment.store();
		assertEquals(testDiagnosis.getTreatments().get(0), treatment);
		try{
			Treatment treatment2 = new Surgery(testDiagnosis, "Niente");
			testDiagnosis.removeTreatment(treatment2);
		}catch (IllegalArgumentException e) {}
		testDiagnosis.removeTreatment(treatment);
		assertEquals(testDiagnosis.getTreatments().size(),0);
	}
	
	@Test
	public void testToString() {
		testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Gebroken Arm");
		assertNotSame(testDiagnosis.toString().length(), 0);
	}
}
