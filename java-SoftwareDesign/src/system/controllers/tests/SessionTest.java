package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import system.campus.Hospital;
import system.controllers.AdministratorController;
import system.controllers.SessionController;
import system.exceptions.IllegalAccessException;
import system.exceptions.IllegalOperationException;
import system.repositories.StaffType;
import system.staff.Doctor;
import system.staff.Nurse;
import system.staff.StaffMember;
import system.staff.WarehouseManager;


/**
 * SessionTest.java is een test-case voor volgende use-cases:
 * 	- Login
 *  - Logout
 *
 * Login en Logout zijn verwerkt in SessionController.java.
 */

public class SessionTest {

	private Hospital hospital;
	private SessionController controller;
	private AdministratorController adminController;
	private Doctor testDoctor;
	private Nurse testNurse;
	private StaffMember testHospitalAdministrator;
	private WarehouseManager testWarehouseManager;
	
	/**
	 * Init voor SessionTest
	 * 
	 * Enkele Staff Members worden aangemaakt. We voegen deze niet toe aan de repository.
	 * Daarna voegen we enkele subtypen (Nurse, Doctor, WarehouseManager) toe aan de repository
	 * via de Administrator Controller.
	 */
	@Before
	public void testInit() {	
		this.controller = new SessionController();
		this.hospital = (Hospital) controller.getBigHospital();
		this.adminController = new AdministratorController(controller);
		this.testHospitalAdministrator = hospital.getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0);
		this.testDoctor = new Doctor("Chuck");
		this.testNurse = new Nurse("Eve");
		this.testWarehouseManager = new WarehouseManager("Franky");
		controller.login(testHospitalAdministrator, hospital.getCampuses().get(0));
		try {
			adminController.addDoctor("Alice");
			adminController.addNurse("Bob");
			adminController.addWarehouseManager("Trent");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * We testen de staff members die NIET in de repository zitten.
	 * Deze kunnen we beschouwen als "buitenstaanders" en zouden niet
	 * ingelogd kunnen worden.
	 */
	@Test
	public void testLoginIllegalStaffMembers() {
		try {
			controller.login(testDoctor, controller.getCurrentCampus());
			fail("Intruder logging in!");
		}
		catch (IllegalOperationException e){ };
	
		try {
			controller.login(testNurse, controller.getCurrentCampus());
			fail("Intruder logging in!");
		}
		catch (IllegalOperationException e){ };

		try {
			controller.login(testWarehouseManager, controller.getCurrentCampus());
			fail("Intruder logging in!");
		}
		catch (IllegalOperationException e){ };

		try {
			controller.login(null, null);
			fail("Should've been a nullpointer.");
		}
		catch(NullPointerException e) {}
		
	}
	
	/**
	 * We testen of staff members die in het systeem, in de staff repository zitten,
	 * zich kunnen inloggen. Deze zouden zich mogen kunnen inloggen.
	 */
	@Test
	public void testLoginValidStaffMembers() {
		StaffMember staffMember = hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(0);
		controller.login(staffMember, controller.getCurrentCampus());
		assertEquals(controller.getCurrentUser(), staffMember);
		staffMember = hospital.getStaffRepository().getStaff().get(0);
		controller.login(staffMember, controller.getCurrentCampus());
		controller.login(testHospitalAdministrator, controller.getCurrentCampus());
	}
	
	/**
	 * We testen of we de ingelogde gebruiker kunnen uitloggen.
	 */
	@Test
	public void testLogout() {
		StaffMember testDoctor = hospital.getStaffRepository().getStaff().get(0);
		controller.login(testDoctor, controller.getCurrentCampus());
		controller.logOut();
		assertEquals(controller.getCurrentUser(), null);
	}
	
}
