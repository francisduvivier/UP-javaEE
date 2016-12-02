package system.staff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.machines.Identifier;
import system.medicaltests.BloodAnalysis;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.repositories.MachineType;
import system.repositories.StaffType;
import system.staff.Doctor;
import system.staff.Nurse;
import system.staff.Shift;
import system.staff.StaffMember;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Cast;

/**
 * Unit Test Nurse.java
 *
 */

public class NurseTest {

	private StaffMember testNurse1, testNurse2;
	private StaffMember testDoctor1;
	private Patient testPatient1;
	private Hospital hospital;
	
	@Before
	public void testInit() {	
		TimeStamp 	time1 = new TimeStamp(2012,3,4,9,0),
					time2 = new TimeStamp(2012,3,4,17,0);
		Shift	shift = new Shift(new TimePeriod(time1,time2));
		hospital = new Hospital();
		Campus campus = hospital.getCampuses().get(0);
		campus.getStaffRepository().addStaffMember(StaffType.NURSE,"Alice");
		campus.getStaffRepository().addStaffMember(StaffType.NURSE,"Bob");
		testNurse1 = campus.getStaffRepository().getStaffMembers(StaffType.NURSE).get(0);
		testNurse2 = campus.getStaffRepository().getStaffMembers(StaffType.NURSE).get(1);
		testNurse1.addShift(new CampusId(campus), shift);
		testNurse2.addShift(new CampusId(campus), shift);
		campus.getMachineRepository().addMachine(new Identifier(123), 0, 3, MachineType.BLOOD_ANALYZER);
		hospital.getStaffRepository().addStaffMember(StaffType.DOCTOR,"Charles");
		testDoctor1 = hospital.getStaffRepository().getStaffMembers(StaffType.DOCTOR).get(0);
		testPatient1 = new Patient("David");
		campus.getPatientRepository().addPatient(testPatient1);
	}
	
	@Test
	public void testNurseConstructor() {
		assertEquals(testNurse1.getName(), "Alice");
		assertEquals(testNurse2.getName(), "Bob");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1NurseConstructor() {
		testNurse1 = new Nurse("");
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2NurseConstructor() {
		testNurse2 = new Nurse(null);
	}
	
	@Test
	public void testToString() {
		testNurse1 = new Nurse("Alice");
		assertNotSame(testNurse1.toString().length(), 0);
	}
	
	@Test
	public void testGetOpenTreatments() {
		Diagnosis diagnosis = new Diagnosis((Doctor)testDoctor1,testPatient1,"test");
		Cast cast = new Cast(diagnosis,"test",16);
		cast.store();
		hospital.getScheduler().schedule(cast, hospital.getHospitalTime().getTime());

		TimeStamp	future =
			TimeStamp.addedToTimeStamp(TimeDuration.days(2), 
					hospital.getHospitalTime().getTime());
		hospital.getHospitalTime().setTime(future);
		
		assertTrue(((Nurse)testNurse1).getOpenTreatments().contains(cast) ^ 
				((Nurse)testNurse2).getOpenTreatments().contains(cast));
	}
	
	@Test
	public void testGetOpenMedicalTests() {
		BloodAnalysis baTest = new BloodAnalysis(testPatient1,"foo",12);
		hospital.getScheduler().schedule(baTest, hospital.getHospitalTime().getTime());

		TimeStamp	future =
			TimeStamp.addedToTimeStamp(TimeDuration.days(2), 
					hospital.getHospitalTime().getTime());
		hospital.getHospitalTime().setTime(future);

		assertTrue(((Nurse) testNurse1).getOpenMedicalTests().contains(baTest) ^ 
				((Nurse)testNurse2).getOpenMedicalTests().contains(baTest));
	}
	
}
