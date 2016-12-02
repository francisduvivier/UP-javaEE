package system.repositories.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.repositories.CampusStaffRepository;
import system.repositories.StaffType;

public class CampusStaffRepositoryTest {

	CampusStaffRepository rep;
	
	@Before
	public void testInit() {
		rep = new CampusStaffRepository();
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testaddStaffMember() {
		rep.addStaffMember(StaffType.DOCTOR,"Test");
		assertEquals(rep.getStaffMembers(StaffType.DOCTOR).size(), 1);
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testGetDoctor() {
		assertEquals(rep.getStaffMembers(StaffType.DOCTOR).size(), 1);
	}
	
	@Test
	public void testAddWarehouseManager() {
		rep.addStaffMember(StaffType.WAREHOUSE_MANAGER,"Test");
		assertEquals(rep.getStaffMembers(StaffType.WAREHOUSE_MANAGER).size(), 1);
	}
	
	@Test
	public void testGetWarehouseManager() {
		assertEquals(rep.getStaffMembers(StaffType.WAREHOUSE_MANAGER).size(), 0);
	}
	
	@Test
	public void testAddNurse() {
		rep.addStaffMember(StaffType.NURSE,"Test");
		assertEquals(rep.getStaffMembers(StaffType.NURSE).size(), 1);
	}
	
	@Test
	public void testGetNurse() {
		assertEquals(rep.getStaffMembers(StaffType.NURSE).size(), 0);
	}
	
	@Test (expected = IllegalOperationException.class)
	public void testGetHospitalAdministrator() {
		assertEquals(rep.getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0), null);
	}
}
