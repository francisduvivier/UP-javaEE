package system.staff.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.repositories.StaffType;
import system.staff.Doctor;
import system.staff.HospitalAdministrator;
import system.staff.Nurse;
import system.staff.Shift;
import system.staff.ShiftTable;
import system.staff.WarehouseManager;
import system.time.TimePeriod;
import system.time.TimeStamp;


public class StaffMemberTest {
	private WarehouseManager testWarehouseManager1;
	private HospitalAdministrator testHospitalAdministrator1;
	private Doctor testDoctor1;
	private Nurse testNurse1;
	
	@Before
	public void testInit() {	
		testWarehouseManager1 = new WarehouseManager("Alice");
		testHospitalAdministrator1 = new HospitalAdministrator("Bob");
		testDoctor1 = new Doctor("Clair");
		testNurse1 = new Nurse("David");
	}
	
	@Test
	public void testResourceTypes() {
		assertEquals(testWarehouseManager1.getResourceType(), StaffType.WAREHOUSE_MANAGER);
		assertEquals(testHospitalAdministrator1.getResourceType(), StaffType.HOSPITAL_ADMINISTRATOR);
		assertEquals(testDoctor1.getResourceType(), StaffType.DOCTOR);
		assertEquals(testNurse1.getResourceType(), StaffType.NURSE);
	}
	
	@Test
	public void testGetShiftTable() {
		ShiftTable table = new ShiftTable();
		assertEquals(testNurse1.getShiftTable(),table);
	}

	@Test
	public void testAddShift() {
		ShiftTable table = new ShiftTable();
		TimeStamp 	time1 = new TimeStamp(2012,10,10,9,0),
					time2 = new TimeStamp(2012,10,10,12,0);
		Shift	shift = new Shift(new TimePeriod(time1,time2));
		Campus campus = new Campus(new Hospital());
		table.add(new CampusId(campus), shift);
		testNurse1.addShift(new CampusId(campus), shift);
		assertEquals(testNurse1.getShiftTable(),table);
	}
}
