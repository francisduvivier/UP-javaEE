package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.Hospital;
import system.controllers.AdministratorController;
import system.controllers.SessionController;
import system.exceptions.IllegalAccessException;
import system.repositories.MachineType;
import system.repositories.StaffType;
import system.staff.StaffMember;
import system.time.TimeStamp;



/**
 * AddHospitalStaffAndEquipmentTest.java is een test-case voor volgende use-cases:
 * 	- Add Hospital Staff
 *  - Add Hospital Equipment
 *
 * Add Hospital Staff en Add Hospital Equipment zijn verwerkt in AdministratorController.java.
 */

public class AddHospitalStaffAndEquipmentTest {

	private Hospital hospital;
	private AdministratorController controller;
	private SessionController sessionController;
	private StaffMember testHospitalAdministrator;
	private StaffMember testDoctor;
	private StaffMember testNurse;
	/**
	 * Init voor AddHospitalStaffAndEquipmentTest
	 * 
	 * Een administrator controller wordt ge�nitialiseerd.
	 */
	@Before
	public void testInit() {	
		this.sessionController = new SessionController();
		this.hospital = (Hospital) sessionController.getBigHospital();
		this.controller = new AdministratorController(sessionController);
		this.testHospitalAdministrator = hospital.getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0);
		sessionController.login(hospital.getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0), getCampus(0));
		controller.addNurse("Astrid");
		controller.addDoctor("Ben");
		
		testNurse = controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.NURSE).get(0);
		testDoctor = controller.getHospitalStaffRepository().getStaffMembers(StaffType.DOCTOR).get(0);
	}
	
	/**
	 * We testen of we een Administrator Controller kunnen
	 * aanmaken met een 'null' hospital administrator
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalidSessionControllerConstructor() {
		this.controller = new AdministratorController(null);
	}
	
	/**
	 * We testen of we doctors kunnen toevoegen aan de staff repository via de administrator controller
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testAddDoctor() {
		sessionController.login(testHospitalAdministrator, getCampus(0));
		controller.addDoctor("John");
		controller.addDoctor("Kate");
		controller.addDoctor("Louis");
		controller.addDoctor("Olivier",getCampus(0),9,0,17,0);
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).size(), 5);
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(1).getName(), "John");
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(2).getName(), "Kate");
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(3).getName(), "Louis");
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(4).getName(), "Olivier");
		
		assertEquals(sessionController.getAllStaff().size(),7);
	}
	
	/**
	 * We testen of we een doctor met als naam een lege string kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid1AddDoctor() {
		controller.addDoctor("");
	}
	
	/**
	 * We testen of we een doctor met als naam een null kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid2AddDoctor() {
		controller.addDoctor(null);
	}
	
	/**
	 * We testen of we een doctor kunnen toevoegen wanneer 
	 * een nurse ingelogd is.
	 * 
	 */
	@Test (expected=IllegalAccessException.class)
	public void testInvalid3AddDoctor() {
		this.sessionController.login(testNurse, sessionController.getCurrentCampus());
		this.controller.addDoctor("Test");
	}
	
	/**
	 * We testen of we twee doctors met dezelfde naam kunnen toevoegen.
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testDuplicateDoctorName() {
		sessionController.login(testHospitalAdministrator, getCampus(0));
		controller.addDoctor("Kate");
		controller.addDoctor("Kate");
	}
	
	/**
	 * We testen of we nurses kunnen toevoegen aan de staff repository via de administrator controller
	 */
	@Test
	public void testAddNurse() {
		sessionController.login(testHospitalAdministrator, getCampus(0));
		controller.addNurse("John");
		controller.addNurse("Kate");
		controller.addNurse("Louis");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.NURSE).size(), 4);
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.NURSE).get(1).getName(), "John");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.NURSE).get(2).getName(), "Kate");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.NURSE).get(3).getName(), "Louis");
		
	}
	
	
	/**
	 * We testen of we een nurse met als naam een lege string kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid1AddNurse() {
		controller.addNurse("");
	}
	
	/**
	 * We testen of we een nurse met als naam een null kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid2AddDNurse() {
		controller.addNurse(null);
	}
	
	/**
	 * We testen of we een nurse kunnen toevoegen wanneer 
	 * een nurse ingelogd is.
	 * 
	 */
	@Test (expected=IllegalAccessException.class)
	public void testInvalid3AddNurse() {
		this.sessionController.login(testNurse, sessionController.getCurrentCampus());
		this.controller.addNurse("Test");
	}
	
	/**
	 * We testen of we twee nurses met dezelfde naam kunnen toevoegen.
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testDuplicateNurseName() {
		sessionController.login(testHospitalAdministrator, getCampus(0));
		controller.addNurse("Kate");
		controller.addNurse("Kate");
	}
	
	/**
	 * We testen of we warehouse managers kunnen toevoegen aan de staff repository via de administrator controller
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testAddWarehouseManager() {
		controller.addWarehouseManager("John");
		controller.addWarehouseManager("Kate");
		controller.addWarehouseManager("Louis");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER).size(), 3);
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER).get(0).getName(), "John");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER).get(1).getName(), "Kate");
		assertEquals(controller.getCurrentCampusStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER).get(2).getName(), "Louis");
	}
	
	/**
	 * We testen of we een WarehouseManager met als naam een lege string kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid1AddWarehouseManager() {
		controller.addWarehouseManager("");
	}
	
	/**
	 * We testen of we een WarehouseManager met als naam een null kunnen toevoegen aan de repository.
	 */
	@Test (expected = NullPointerException.class)
	public void testInvalid2AddDWarehouseManager() {
		controller.addWarehouseManager(null);
	}
	
	/**
	 * We testen of we een nurse kunnen toevoegen wanneer 
	 * een nurse ingelogd is.
	 * 
	 */
	@Test (expected=IllegalAccessException.class)
	public void testInvalid3AddWarehouseManager() {
		this.sessionController.login(testNurse, sessionController.getCurrentCampus());
		this.controller.addWarehouseManager("Test");
	}
	
	/**
	 * We testen of we twee warehouse managers met dezelfde naam kunnen toevoegen.
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testDuplicateWarehouseManagerName() {
		sessionController.login(testHospitalAdministrator, getCampus(0));
		controller.addWarehouseManager("Kate");
		controller.addWarehouseManager("Kate");
	}
	
	private Campus getCampus(int i) {
		return (Campus) sessionController.getCampuses().get(i);
	}

	/**
	 * We testen of we machines kunnen toevoegen aan de machine repository via de administrator controller
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testAddMachine() {
		// proberen een machine toe te voegen met een negatieve kamer (kan niet!)
		try {
			controller.addMachine(1, 2, -5, MachineType.BLOOD_ANALYZER);
			fail("Negatief kamernummer meegegeven.");
		} catch (IllegalArgumentException e) {}
		// proberen een null machine toe te voegen (kan niet!)
		try {
			controller.addMachine(2, 0, 0, null);
			fail("Null machine type meegegeven.");
		} catch (IllegalArgumentException e) {}
		
		
		try {
			controller.addMachine(3, 1, 5, MachineType.SURGICAL_EQUIPMENT);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		try {
			controller.addMachine(4, 3, 1, MachineType.XRAY_SCANNER);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test (expected = IllegalAccessException.class)
	public void testInvalidAddMachine() {
		sessionController.login(testDoctor, sessionController.getCurrentCampus());
		controller.addMachine(18, 1, 2, MachineType.ULTRASOUND_MACHINE);
	}
	
	/**
	 * We testen of we 2 machines met dezelfde ID kunnen toevoegen.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testDuplicateMachineID() {
		controller.addMachine(0, 1, 3, MachineType.XRAY_SCANNER);
		controller.addMachine(0, 1, 3, MachineType.BLOOD_ANALYZER);
	}
	
	@Test
	public void testAdvanceTime() {
		TimeStamp timeStamp = new TimeStamp(2012,0,1,1, 0);
		controller.advanceTime(timeStamp);
		assertEquals(hospital.getHospitalTime().getTime(), timeStamp);
	}
	
}
