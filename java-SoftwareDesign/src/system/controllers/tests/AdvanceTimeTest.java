package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalAccessException;
import system.patients.Diagnosis;
import system.time.TimeStamp;
import system.warehouse.MedicationItemType;
import testsuite.TestWithControllers;


public class AdvanceTimeTest extends TestWithControllers {
	private TimeStamp tomorrow;
	private TimeStamp dayAfterTomorrow;
	
	@Override
	@Before
	public void testInit() {
		super.testInit();
		tomorrow = getFutureTime(1);
		dayAfterTomorrow = getFutureTime(2);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testUserNull() {
		sessionController.logOut();
		adminController.advanceTime(tomorrow);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testUserNotHospitalAdmin() {
		sessionController.login(testC1Doctor1, getCampus(0));
		adminController.advanceTime(tomorrow);
	}
	@Test
	public void testTimeIsChanged() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(tomorrow);
		assertEquals(tomorrow,hospital.getHospitalTime().getTime());
		adminController.advanceTime(dayAfterTomorrow);
		assertEquals(dayAfterTomorrow,hospital.getHospitalTime().getTime());
	}
	
	@Test
	public void testEventsExecuted() {
				
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("fantoompijn");
		Diagnosis diagnosis=testC1Patient1.getPatientFile().getDiagnoses().get(0);
		doctorController.prescribeSurgery(diagnosis, "Surgery");
		List<MedicationItemType> medicationItemTypes = new ArrayList<MedicationItemType>();
		medicationItemTypes.add(MedicationItemType.VITAMINS);
		medicationItemTypes.add(MedicationItemType.SLEEPING_TABLETS);
		doctorController.prescribeMedication(diagnosis, "Medication", true, medicationItemTypes);
		doctorController.prescribeCast(diagnosis, "CastPart",30);
		doctorController.orderBloodAnalysis("BloodAnalysis", 11);
		doctorController.orderUltrasoundScan("UltrasoundScan", true, false);
		doctorController.orderXRayScan("XRayScan", 5, 5);
		doctorController.closePatientFile();
		sessionController.logOut();
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.selectRegisteredPatient(testC1Patient1);
 		nurseController.scheduleAppointment(testC1Doctor1);
 		nurseController.registerNewPatient("testPatient2");
		nurseController.registerNewPatient("testPatient3");
		sessionController.logOut();
		

		sessionController.login(testHospitalAdmin, getCampus(0));
		TimeStamp futureTime=getFutureTime(365);
		adminController.advanceTime(futureTime);
		sessionController.login(testC1Nurse1, getCampus(0));
		assertSame(3,nurseController.getUnfinishedMedicalTests().size());
		assertSame(3,nurseController.getUnfinishedTreatments().size());
		//assertFalse(sessionController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getLatestOrders(1).isEmpty());
	//	assertFalse(sessionController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getArrivedOrders().isEmpty());
	
	}
	
	
	
}
