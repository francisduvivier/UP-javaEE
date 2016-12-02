package system.repositories.tests;

import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.Hospital;
import system.controllers.SessionController;
import system.repositories.StaffType;


/**
 * Unit Test Hospital.java
 *
 */

public class HospitalTest {
	
	private Hospital hospital;
	private SessionController sessionController;
	
	@Before
	public void testInit() {
		sessionController = new SessionController();
		hospital = (Hospital) sessionController.getBigHospital();
		sessionController.setCurrentCampus((Campus)sessionController.getCampuses().get(0));
	}
	// , sessionController.getCurrentCampus()
	@Test
	public void testConstructor() {
		assertNotSame(hospital.getHospitalTime(), null);
		assertNotSame(sessionController.getCurrentCampus().getMachineRepository(), null);
		assertNotSame(sessionController.getCurrentCampus().getPatientRepository(), null);
		assertNotSame(sessionController.getCurrentCampus().getResources(StaffType.DOCTOR), null);
		assertNotSame(hospital.getScheduler(), null);
		assertNotSame(hospital.getStaffRepository(), null);
		assertNotSame(sessionController.getCurrentCampus().getWarehouse(), null);
	}
}
