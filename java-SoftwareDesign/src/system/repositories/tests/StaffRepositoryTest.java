package system.repositories.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import system.repositories.CampusStaffRepository;
import system.repositories.HospitalStaffRepository;
import system.repositories.StaffType;


/**
 * Unit Test StaffRepository.java
 *
 */

public class StaffRepositoryTest {

	private CampusStaffRepository campusStaffRepository;
	private HospitalStaffRepository hospitalStaffRepository;
	
	@Before
	public void testInit() {
		campusStaffRepository = new CampusStaffRepository();
		campusStaffRepository.addStaffMember(StaffType.NURSE,"Clair");
		campusStaffRepository.addStaffMember(StaffType.NURSE,"David");
		campusStaffRepository.addStaffMember(StaffType.NURSE,"Eric");
		campusStaffRepository.addStaffMember(StaffType.WAREHOUSE_MANAGER,"Fred");
		
		hospitalStaffRepository = new HospitalStaffRepository();
		hospitalStaffRepository.addStaffMember(StaffType.DOCTOR,"Alice");
		hospitalStaffRepository.addStaffMember(StaffType.DOCTOR,"Bob");
	}
	
	@Test
	public void testGetResources() {
		assertEquals(hospitalStaffRepository.getResources(StaffType.DOCTOR).size(), 2);
		assertEquals(campusStaffRepository.getResources(StaffType.NURSE).size(), 3);
		assertEquals(campusStaffRepository.getResources(StaffType.WAREHOUSE_MANAGER).size(), 1);
	}
	
}
