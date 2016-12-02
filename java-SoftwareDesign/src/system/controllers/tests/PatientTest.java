package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.controllers.DoctorController;
import system.exceptions.IllegalOperationException;
import system.exceptions.InsufficientWarehouseItemsException;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.repositories.StaffType;
import system.results.treatmentresults.CastResult;
import system.scheduling.ScheduleResource;
import system.staff.Doctor;
import system.staff.StaffMember;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.warehouse.stock.StockType;
import testsuite.TestWithControllers;


/**
 * PatientTest.java is een test-case voor volgende use-cases:
 * 	- Register Patient
 *  - Discharge Patient
 *
 * Register Patient en Discharge Patient zijn verwerkt in PatientController.java.
 */

public class PatientTest extends TestWithControllers {

	private StaffMember testDoctor2;
	private Patient testPatient2, testPatient3;
	/**
	 * Init voor PatientTest
	 * 
	 * Enkele Staff Members worden aangemaakt. We voegen deze niet toe aan de repository.
	 * Daarna voegen we enkele subtypen (Nurse, Doctor, WarehouseManager) toe aan de repository
	 * via de Administrator Controller.
	 */
	@Override
	@Before
	public void testInit() {	
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.addDoctor("Clair");
		testDoctor2 = adminController.getHospitalStaffRepository().getStaffMembers(StaffType.DOCTOR).get(1);
		sessionController.login(testC1Nurse1, getCampus(0));
	}
	
	/**
	 * We testen een patient die niet in de patient repository zit.
	 * Een verpleger zou deze patient niet mogen kunnen registeren en moet een exception gooien.
	 */
	@Test
	public void testInvalid1RegisterPatients() {
		testPatient2 = new Patient("Chuck");
		try {
			nurseController.registerPatient(testPatient2);
			fail("Test failed");
		} catch (IllegalOperationException e) {}
	}

	/**
	 * We testen of er een registeringerror gegooid wordt wanneer we proberen om
	 * een patient te registeren die al geregistered is.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void registerAlreadyRegisteredPatient() {
	nurseController.registerPatient(testC1Patient1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2RegisterPatients() {
		nurseController.registerPatient(null);
	}
	/**
	 * We testen een patient die in de patient repository zit.
	 * Dit test evens het toevoegen van een patient aan de repository via de patient controller.
	 * Een verpleger zou deze patient moeten kunnen registreren.
	 */
	@Test
	public void testRegisterdValidPatients() {
		List<Patient> testRegisteredPatients = new ArrayList<Patient>();
		testPatient2=nurseController.registerNewPatient("Alice");
		testPatient3=nurseController.registerNewPatient("Dana");
		testRegisteredPatients.add(testC1Patient1);
		testRegisteredPatients.add(testPatient2);
		testRegisteredPatients.add(testPatient3);
		
		assertEquals(nurseController.getRegisteredPatients().get(2), testPatient3);
		testPatient2 =nurseController.registerNewPatient("Bob");
		testRegisteredPatients.add(testPatient2);
		//testRegisteredPatients.add(testPatient3);
		assertEquals(nurseController.getRegisteredPatients().get(3), testPatient2);
		assertEquals(nurseController.getRegisteredPatients(), testRegisteredPatients);
		
	}
	
	/**
	 * We testen of we een patient met als naam een lege string kunnen registreren.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid1AddPatient() {
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerNewPatient("");
	}
	
	/**
	 * We testen of we een patient met als naam een lege string kunnen registreren.
	 */
	@Test (expected = InsufficientWarehouseItemsException.class)
	public void testPatientNotEnoughFood() {
		sessionController.login(testC1Nurse1, getCampus(0));
		while(sessionController.getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem()!=null);
		sessionController.login(testC2Nurse1, getCampus(1));
		while(sessionController.getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem()!=null);
		nurseController.registerNewPatient("verhongerende patient2");
	}
	
	/**
	 * We testen of we een patient met een 'null' naam kunnen registreren.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid2AddPatient() {
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerNewPatient(null);
	}
	
	/**
	 * We testen of we twee patienten met dezelfde nama kunnen toevoegen.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3AddPatient() {
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerNewPatient("Alice");
		nurseController.registerNewPatient("Alice");
	}
	
	/**
	 * We testen of canBeDischarged correct werkt
	 */
	@Test
	public void testCanBeDischarged() {
		TimeStamp 	time1 = new TimeStamp(2012,11,21,0,0),
					time2 = new TimeStamp(2012,11,22,0,0);
		TimePeriod 	timePeriod = new TimePeriod(time1,time2);
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerNewPatient("Alice");
		testPatient2 = sessionController.getCurrentCampus().getPatientRepository().getRegisteredPatients().get(0);
		Diagnosis diagnosis = new Diagnosis((Doctor) testC1Doctor1, (Doctor) testDoctor2,testPatient2,"Test");
		Diagnosis diagnosis2 = new Diagnosis((Doctor) testC1Doctor1, testPatient2, "Test");
		sessionController.login(testC1Doctor1, getCampus(0));
		DoctorController doctorController = new DoctorController(sessionController);
		doctorController.consultPatientFile(testPatient2);
		diagnosis.register();
		assertFalse(testPatient2.canBeDischarged());
		diagnosis.unregister();
		assertTrue(testPatient2.canBeDischarged());
		diagnosis2.register();
		assertTrue(testPatient2.canBeDischarged());
		Cast cast = new Cast(diagnosis2, "Foo bone", 30);
		cast.store();
		assertFalse(testPatient2.canBeDischarged());
		cast.schedule(timePeriod,new ArrayList<ScheduleResource>(),null);
		cast.getStart().execute();
		cast.getStop().execute();
		assertTrue(testPatient2.canBeDischarged());
		cast.setResult(new CastResult("FOO BONE RESTORED"));
		assertTrue(testPatient2.canBeDischarged());

		doctorController.orderBloodAnalysis("Foo cells", 50);
		assertFalse(testPatient2.canBeDischarged());
		sessionController.login(testHospitalAdmin, getCampus(0));
		TimeStamp advanceDay = TimeStamp.addedToTimeStamp(TimeDuration.days(1),
				this.hospital.getHospitalTime().getTime());
		adminController.advanceTime(advanceDay);
		assertTrue(testPatient2.canBeDischarged());

	}
}
