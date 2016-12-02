package system.repositories.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.repositories.HospitalStaffRepository;
import system.repositories.StaffType;

/**
 * Unit Test HospitalStaffRepository.java
 * 
 * @author SWOP Groep 10
 */

public class HospitalStaffRepositoryTest {
	
	HospitalStaffRepository rep;
	
	@Before
	public void testInit() {
		rep = new HospitalStaffRepository();
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testAddNurse() {
		rep.addStaffMember(StaffType.NURSE,"Test");
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testGetNurses() {
		assertEquals(rep.getStaffMembers(StaffType.NURSE).size(), 1);
	}
	
	@Test
	public void testAddDoctor() {
		rep.addStaffMember(StaffType.DOCTOR,"Test");
		assertEquals(rep.getStaffMembers(StaffType.DOCTOR).size(), 1);
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testAddWarehouseManager() {
		rep.addStaffMember(StaffType.WAREHOUSE_MANAGER,"Test");
		assertEquals(rep.getStaffMembers(StaffType.WAREHOUSE_MANAGER).size(), 1);
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testGetWarehouseManager() {
		assertEquals(rep.getStaffMembers(StaffType.WAREHOUSE_MANAGER).size(), 1);
	}
	
	@Test
	public void testGetHospitalAdministrator() {
		assertEquals(rep.getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0).getName(), "Tom Holvoet");
	}
	
}
