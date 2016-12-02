package system.staff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.staff.HospitalAdministrator;


/**
 * Test Unit HospitalAdministrator.java
 *
 */

public class HospitalAdministratorTest {
	
	private HospitalAdministrator testHospitalAdministrator1, testHospitalAdministrator2;

	@Before
	public void testInit() {	
		testHospitalAdministrator1 = new HospitalAdministrator("Alice");
		testHospitalAdministrator2 = new HospitalAdministrator("Bob");
	}
	
	@Test
	public void testDoctorConstructor() {
		assertEquals(testHospitalAdministrator1.getName(), "Alice");
		assertEquals(testHospitalAdministrator2.getName(), "Bob");
	}
	
	@Test
	public void testToString() {
		testHospitalAdministrator1 = new HospitalAdministrator("Alice");
		assertNotSame(testHospitalAdministrator1.toString().length(), 0);
	}
}
