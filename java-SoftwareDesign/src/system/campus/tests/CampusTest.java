package system.campus.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.machines.Identifier;
import system.machines.Machine;
import system.patients.Patient;
import system.repositories.MachineType;
import system.repositories.PatientType;
import system.repositories.StaffType;
import system.scheduling.ScheduleResource;
import system.staff.StaffMember;
import system.warehouse.stock.StockType;


/**
 * Unit Test Campus.java
 *
 */

public class CampusTest {
	
	Hospital hospital;
	Campus campus;
	
	@Before
	public void testInit() {
		hospital = new Hospital();
		campus = new Campus(hospital);
	}
	
	@Test
	public void testCampus() {
		assertEquals(campus.getBigHospital(), hospital);
		assertTrue(campus.equals(campus));
		assertFalse(hospital.getCampuses().get(0).equals(campus));
		assertFalse(hospital.getCampuses().get(1).equals(campus));
	}
	
	@Test
	public void testGetStaffRepository() {
		// test nurses
		for (int i = 0; i < 10; i++)
			campus.getStaffRepository().addStaffMember(StaffType.NURSE,""+i);
		List<StaffMember> nurses = campus.getStaffRepository().getStaffMembers(StaffType.NURSE);
		assertEquals(nurses.size(), 10);
		
		// test nurses
		for (int i = 0; i < 10; i++)
			campus.getStaffRepository().addStaffMember(StaffType.WAREHOUSE_MANAGER,""+i);
		List<StaffMember> warehouseManagers = campus.getStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER);
		assertEquals(warehouseManagers.size(), 10);
	}
	
	@Test
	public void testGetMachineRepository() {
		for (int i = 0; i < 10; i++)
			campus.getMachineRepository().addMachine(new Identifier(i), i, i, MachineType.BLOOD_ANALYZER);
		List<Machine> machines = campus.getMachineRepository().getMachines();
		assertEquals(machines.size(), 10);
	}
	
	@Test
	public void testGetPatientRepository() {
		for (int i = 0; i < 10; i++)
			campus.getPatientRepository().addPatient(new Patient(""+i));
		List<Patient> patients = campus.getPatientRepository().getNonDischargedPatients();
		assertEquals(patients.size(), 10);
	}
	
	@Test
	public void testGetOrderRepository() { // FIXME	
		for (int i = 0; i < 10; i++)
			campus.getPatientRepository().addPatient(new Patient(""+i));
		List<Patient> patients = campus.getPatientRepository().getNonDischargedPatients();
		assertEquals(patients.size(), 10);
	}
	
	@Test
	public void testGetWarehouse() {
		assertEquals(campus.getWarehouse().getStockList().getDefaultStock(StockType.MEAL).getStockSize(), 120);
	}
	
	@Test
	public void testGetResources() {
		// test nurses
		for (int i = 0; i < 10; i++)
			campus.getStaffRepository().addStaffMember(StaffType.NURSE,""+i);
		List<ScheduleResource> resources = hospital.getResources(StaffType.NURSE, campus);
		assertEquals(resources.size(), 10);
		
		// test warehouse managers
		for (int i = 0; i < 10; i++)
			campus.getStaffRepository().addStaffMember(StaffType.WAREHOUSE_MANAGER,""+i);
		resources = hospital.getResources(StaffType.WAREHOUSE_MANAGER, campus);
		assertEquals(resources.size(), 10);;
		
		// test machines
		for (int i = 0; i < 10; i++)
			campus.getMachineRepository().addMachine(new Identifier(i), i, i, MachineType.BLOOD_ANALYZER);
		resources = hospital.getResources(MachineType.BLOOD_ANALYZER, campus);
		assertEquals(resources.size(), 10);
				
		// test patients
		for (int i = 0; i < 10; i++)
			campus.getPatientRepository().addPatient(new Patient(""+i));
		resources = hospital.getResources(PatientType.STAYING_PATIENT, campus);
		assertEquals(resources.size(), 10);

	}
	
	@Test
	public void testToString() {
		assertEquals(campus.toString(), "Campus " + Integer.toString(hospital.getCampuses().indexOf(this)+1));
	}
	
	@Test
	public void testEqualsInvalid() {
		assertFalse(campus.equals(null));
		assertFalse(campus.equals(new Object()));
		assertFalse(campus.equals(new CampusId(hospital.getCampuses().get(1))));
		assertTrue(campus.equals(campus));
	}
	
	
}
