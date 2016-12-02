package testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import client.IPatient;

import system.exceptions.IllegalOperationException;
import system.time.TimeStamp;

public class Scenario5_epidemic extends TestWithControllers {
	@Before
	public void testInit() {
		super.testInit();		
	}
	
	/**
	 * Dit scenario test of het registeren van patienten correct werkt in 'open' state
	 * en in 'lockdown' state.
	 */
	@Test
	public void scenario5_1() {
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		for (int i = 0; i < 19; i++) 
			doctorController.enterDiagnosis("Griep",1);
		
		sessionController.login(testC1Nurse1, getCampus(0));
		int size = nurseController.getRegisteredPatients().size();
		IPatient createdTestPatient = nurseController.registerNewPatient("Test patient");
		// patienten registreren gaat
		assertEquals(size+1,nurseController.getRegisteredPatients().size());
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(createdTestPatient);
		doctorController.dischargePatient();
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Ziek",1);
		
		sessionController.login(testC1Nurse1, getCampus(0));
		try {
			nurseController.selectRegisteredPatient(createdTestPatient);
			fail("Exception niet geworpen");
		} catch (IllegalArgumentException e) {}
		size = nurseController.getRegisteredPatients().size();
		createdTestPatient = nurseController.registerNewPatient("Test patient na ziek");
		assertEquals(size+1,nurseController.getRegisteredPatients().size());
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(createdTestPatient);
		doctorController.dischargePatient();
		doctorController.consultPatientFile(testC1Patient1);
		// 20ste keer griep geconstateerd => lockdown
		doctorController.enterDiagnosis("Griep",1);
		
		sessionController.login(testC1Nurse1, getCampus(0));
		try {
			nurseController.selectRegisteredPatient(createdTestPatient);
			fail("Exception niet geworpen");
		} catch (IllegalArgumentException e) {}
		size = nurseController.getRegisteredPatients().size();
		int counter = 0;
		for (int i = 0; i <  100; i++) {
			try {
				nurseController.registerNewPatient("Test patient "+i);
			} catch (IllegalOperationException ioe) {counter++;}
		}
		// er kunnen geen nieuwe patiënten meer geregistreerd worden (lockdown!)
		assertEquals(counter,100);
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		try {
			doctorController.dischargePatient();
			fail("Exception niet geworpen");
		} catch (IllegalOperationException e) {}
		
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.selectRegisteredPatient(testC1Patient1);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertEquals(adminController.getListOfPeopleWhoLeft().size(),2);

		sessionController.login(testC1Nurse1, getCampus(0));

		try {
			nurseController.registerPatient(createdTestPatient);
			fail("Exception niet geworpen");
		} catch (IllegalOperationException ioe) {}
		try {
			nurseController.selectRegisteredPatient(createdTestPatient);
			fail("Exception niet geworpen");
		} catch (IllegalArgumentException e) {}
		assertEquals(size,nurseController.getRegisteredPatients().size());
	}
	
	/**
	 * Dit scenario test of er een patient ontslaan is wegens het verstrijken van veel tijd,
	 * ondanks dat er 20x (=max. bedreigingsniveau!) griep werd gediagnoseerd.
	 */
	@Test
	public void scenario5_2() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(new TimeStamp(2011,10,8,14,0));
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Griep",1);
		doctorController.prescribeCast(doctorController.getDiagnoses().get(0), "Hoofd", 14002);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(new TimeStamp(2011,10,9,9,0));
		sessionController.login(testC1Doctor1, getCampus(0));
		for (int i = 0; i < 19; i++) 
			doctorController.enterDiagnosis("Griep",1);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertEquals(adminController.getListOfPeopleWhoLeft().size(),1);

		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.undoCommand(5);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertFalse(adminController.campusIsInLockdown());
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.redoCommand(0);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertEquals(adminController.getListOfPeopleWhoLeft().size(),1);
		
		adminController.openCampusAfterLockdown();
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Griep",1);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertFalse(adminController.campusIsInLockdown());
		
		sessionController.login(testC1Doctor1, getCampus(0));
		for (int i = 0; i < 19; i++) 
			doctorController.enterDiagnosis("Griep",1);
		
		sessionController.login(testHospitalAdmin, getCampus(0));
		assertTrue(adminController.campusIsInLockdown());
	}
}
