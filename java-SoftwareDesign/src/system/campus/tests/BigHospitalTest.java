package system.campus.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.campus.CampusId;
import system.campus.Hospital;
import system.machines.Identifier;
import system.patients.Patient;
import system.repositories.MachineType;
import system.repositories.PatientType;
import system.repositories.StaffType;
import system.scheduling.ScheduleResource;
import system.staff.Doctor;
import system.time.Time;
import system.time.TimeDuration;

/**
 * Unit Test Hospital.java
 * 
 * @author Swop team 10
 */

public class BigHospitalTest {
	
	Hospital hospital;
	
	@Before
	public void testInit() {
		hospital = new Hospital();
	}
	
	@Test
	public void testGetResources() {
		
		// test nurses
		for (int i = 0; i < 10; i++)
			hospital.getCampuses().get(0).getStaffRepository().addStaffMember(StaffType.NURSE,""+i);
		List<ScheduleResource> resources = hospital.getResources(StaffType.NURSE, hospital.getCampuses().get(0));
		assertEquals(resources.size(), 10);
		
		// test warehouse managers
		for (int i = 0; i < 10; i++)
			hospital.getCampuses().get(0).getStaffRepository().addStaffMember(StaffType.WAREHOUSE_MANAGER,""+i);;
		resources = hospital.getResources(StaffType.WAREHOUSE_MANAGER, hospital.getCampuses().get(0));
		assertEquals(resources.size(), 10);
		
		// test doctors
		for (int i = 0; i < 10; i++)
			hospital.getStaffRepository().addStaffMember(StaffType.DOCTOR,""+i);
		resources = hospital.getResources(StaffType.DOCTOR, hospital.getCampuses().get(0));
		assertEquals(resources.size(), 10);
		
		// test machines
		for (int i = 0; i < 10; i++)
			hospital.getCampuses().get(0).getMachineRepository().addMachine(new Identifier(i), i, i, MachineType.BLOOD_ANALYZER);
		resources = hospital.getResources(MachineType.BLOOD_ANALYZER, hospital.getCampuses().get(0));
		assertEquals(resources.size(), 10);
				
		// test patients
		for (int i = 0; i < 10; i++)
			hospital.getCampuses().get(0).getPatientRepository().addPatient(new Patient(""+i));
		resources = hospital.getResources(PatientType.STAYING_PATIENT, hospital.getCampuses().get(0));
		assertEquals(resources.size(), 10);
			
	}
	
	@Test
	public void testGetStaffRepository() {
		
		for (int i = 0; i < 20; i++) {
			hospital.getStaffRepository().addStaffMember(StaffType.DOCTOR,"" + i);
		}
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).size(), 20);
		
		for (int i = 20; i < 40; i++) {
			hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).add(new Doctor("" + i));
		}
		assertEquals(hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).size(), 20); // should not increase size
	}
	
	@Test // FIXME: @VINCME
	public void testGetScheduler() {
		hospital.getScheduler();
	}
	
	@Test
	public void testCalcTravelTime() {
		assertEquals(TimeDuration.minutes(15), hospital.calcTravelTime(new CampusId(hospital.getCampuses().get(0)), new CampusId(hospital.getCampuses().get(1))));
	}
	
	@Test
	public void testGetHospitalTime() {
		Time time = new Time(2011,10,8,8,0);
		assertEquals(time.getTime(), hospital.getHospitalTime().getTime());
	}

}
