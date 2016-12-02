package system.scheduling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.patients.Patient;
import system.scheduling.Appointment;
import system.scheduling.EventType;
import system.scheduling.ScheduleResource;
import system.staff.Doctor;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;


public class AppointmentTest {

	private Patient testPatient1, testPatient2;
	private Doctor testDoctor1, testDoctor2;
	private Appointment testAppointment1, testAppointment2;
	
	@Before
	public void testInit()  {
		testPatient1 = new Patient("Alice");
		testPatient2 = new Patient("Bob");
		testDoctor1 = new Doctor("Clair");
		testDoctor2 = new Doctor("David");
		testAppointment1 = new Appointment(testPatient1, testDoctor1);
		testAppointment2 = new Appointment(testPatient2, testDoctor2);
	}
	
	@Test
	public void testAppointmentConstructor() {
		testAppointment1 = new Appointment(testPatient1, testDoctor1);
		assertEquals(testAppointment1.getEventType(), EventType.APPOINTMENT);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1AppointmentConstructor() {
		testAppointment1 = new Appointment(null, testDoctor1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2AppointmentConstructor() {
		testAppointment1 = new Appointment(testPatient1, null);
	}
	
	@Test
	public void testNeededResources() {
		assertTrue(testAppointment1.neededResources().isEmpty());
	}
	
	@Test
	public void testNeededSpecificResources() {
		List<ScheduleResource> specificResources = new ArrayList<ScheduleResource>();
		specificResources.add(testDoctor1);
		specificResources.add(testPatient1);
		assertEquals(testAppointment1.neededSpecificResources(), specificResources);
	}
	
	@Test
	public void testGetScheduledPeriod() {
		TimeStamp 	time = new TimeStamp(2011,11,8,0,0),
					time2 = new TimeStamp(2011,11,8,30,0);
		TimePeriod timePeriod = new TimePeriod(time,time2);
		testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);

		assertEquals(testAppointment2.getScheduledPeriod(),timePeriod);
		assertEquals(testAppointment1.getScheduledPeriod(), null);
	}
	
	@Test
	public void testStateFlowRight() {
		TimeStamp 	time = new TimeStamp(2011,11,8,0,0),
					time2 = new TimeStamp(2011,11,8,30,0);
		TimePeriod timePeriod = new TimePeriod(time,time2);
		testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);
		testAppointment2.getStart().execute();
		testAppointment2.getStop().execute();
	}
	
	@Test
	public void testStateFlowWrong() {
		TimeStamp 	time = new TimeStamp(2011,11,8,0,0),
					time2 = new TimeStamp(2011,11,8,30,0);
		TimePeriod timePeriod = new TimePeriod(time,time2);
		
		testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);
		try {
			testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		try {
			testAppointment2.getStop().execute();
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		testAppointment2.getStart().execute();
		try {
			testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		try {
			testAppointment2.getStart().execute();
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		testAppointment2.getStop().execute();
		try {
			testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		try {
			testAppointment2.getStart().execute();
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
		try {
			testAppointment2.getStop().execute();
			fail("Test failed"); 
		} catch(IllegalOperationException e) {}
	}
		
	@Test
	public void updateWarehouse() {
		testAppointment1.updateWarehouse(null, true);
	}
	
	@Test
	public void testToString() {
		TimeStamp 	time = new TimeStamp(2011,11,8,0,0),
					time2 = new TimeStamp(2011,11,8,30,0);
		TimePeriod timePeriod = new TimePeriod(time,time2);
		testAppointment2.schedule(timePeriod, testAppointment2.neededSpecificResources(), null);

		assertEquals(testAppointment2.toString(), "Appointment with " + testDoctor2.getName() + " at "
		+ timePeriod);
	}
	
	@Test
	public void testDuration() {
		assertEquals(TimeDuration.minutes(30), testAppointment1.getDuration());
	}

	@Test
	public void testCanBeScheduled() {
		assertTrue(testAppointment1.canBeScheduled(null));
	}
	
}
