package system.treatments.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.exceptions.ReschedulingException;
import system.exceptions.ResultMismatchException;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.results.Result;
import system.results.treatmentresults.CastResult;
import system.results.treatmentresults.MedicationResult;
import system.scheduling.EventType;
import system.scheduling.ScheduleEvent;
import system.scheduling.ScheduleResource;
import system.scheduling.ScheduledItem;
import system.staff.Doctor;
import system.staff.Nurse;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.treatments.Medication;
import system.treatments.Treatment;
import system.warehouse.MedicationItemType;


/**
 * Unit Test Treatment.java
 * 
 */

public class TreatmentTest {

	private Treatment testTreatment;
	private Doctor testDoctor;
	private Patient testPatient;
	private Nurse testNurse;
	private Diagnosis testDiagnosis;

	@Before
	public void testInit() {
		testDoctor = new Doctor("Alice");
		testPatient = new Patient("Bob");
		testNurse = new Nurse("Charles");
		testDiagnosis = new Diagnosis(testDoctor, testPatient, "Test");
		List<MedicationItemType> items = new ArrayList<MedicationItemType>();
		items.add(MedicationItemType.ASPIRIN);
		testTreatment = new Medication(testDiagnosis, "Test", true, items);
	}

	@Test
	public void testNeedsResult() {
		TimeStamp time1 = new TimeStamp(2011, 11, 20, 10, 50), time2 = new TimeStamp(
				2011, 11, 20, 11, 20);

		TimePeriod timePeriod = new TimePeriod(time1, time2);

		assertFalse(this.testTreatment.needsResult());
		this.testTreatment.store();
		assertFalse(this.testTreatment.needsResult());
		this.testTreatment.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		assertFalse(this.testTreatment.needsResult());
		this.testTreatment.getStart().execute();
		assertFalse(this.testTreatment.needsResult());
		this.testTreatment.getStop().execute();
		assertTrue(this.testTreatment.needsResult());
	}
	
	@Test
	public void testIsFinished() {
		TimeStamp time1 = new TimeStamp(2011, 11, 20, 10, 50), time2 = new TimeStamp(
				2011, 11, 20, 11, 20);

		TimePeriod timePeriod = new TimePeriod(time1, time2);

		assertFalse(this.testTreatment.isFinished());
		this.testTreatment.store();
		assertFalse(this.testTreatment.isFinished());
		this.testTreatment.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		assertFalse(this.testTreatment.isFinished());
		this.testTreatment.getStart().execute();
		assertFalse(this.testTreatment.isFinished());
		this.testTreatment.getStop().execute();
		assertFalse(this.testTreatment.isFinished());
		testTreatment.setResult(new MedicationResult("Alles ok!", false));
		assertTrue(this.testTreatment.isFinished());
	}

	@Test
	public void testSetResult() {
		Result result = new MedicationResult("Alles ok!", false);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testTreatment.store();
		testTreatment.schedule(timePeriod, new ArrayList<ScheduleResource>(), null);
		testTreatment.getStart().execute();
		testTreatment.getStop().execute();
		testTreatment.setResult(result);
	}

	@Test
	public void testInvalid1SetResult() {
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testTreatment.store();
		testTreatment.schedule(timePeriod, new ArrayList<ScheduleResource>(), null);
		testTreatment.getStart().execute();
		testTreatment.getStop().execute();
		try {
			testTreatment.setResult(null);
			fail("Test Failed");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testInvalid2SetResult() {
		Result result = new CastResult("Niet ok!");
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testTreatment.store();
		testTreatment.schedule(timePeriod, new ArrayList<ScheduleResource>(), null);
		testTreatment.getStart().execute();
		testTreatment.getStop().execute();
		try {
			testTreatment.setResult(result);
			fail("Test Failed");
		} catch (ResultMismatchException e) {
		}
	}

	@Test
	public void testCancelRedo() {
		Cast testCast = new Cast(testDiagnosis, "Foo Bone", 300), testCast2 = new Cast(
				testDiagnosis, "Foo Bone", 300);
		List<ScheduleResource> resources = new ArrayList<ScheduleResource>();

		resources.add(testPatient);
		resources.add(testNurse);

		testCast.store();
		
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testCast.schedule(timePeriod, resources, null);
		testCast.cancel();
		assertEquals(testCast.getScheduledPeriod(), null);
		for (ScheduleResource resource : resources)
			assertTrue(resource.getSchedule().getSchedule().isEmpty());
		testCast.redo();
		assertEquals(testCast.getScheduledPeriod(), timePeriod);
		for (ScheduleResource resource : resources) {
			List<ScheduleEvent> list = scheduleToList(resource.getSchedule()
					.getSchedule());
			assertTrue(list.contains(testCast));
			assertEquals(list.size(), 1);
		}
		testCast.cancel();
		testCast2.store();
		
		resources.remove(testNurse);
		
		testCast2.schedule(timePeriod, resources, null);
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (ReschedulingException e) {
		}
		assertEquals(testCast.getScheduledPeriod(), null);
		assertEquals(testCast2.getScheduledPeriod(), timePeriod);
		for (ScheduleResource resource : resources) {
			List<ScheduleEvent> list = scheduleToList(resource.getSchedule()
					.getSchedule());
			assertTrue(list.contains(testCast2));
			assertEquals(list.size(), 1);
		}
	}

	private static List<ScheduleEvent> scheduleToList(
			SortedSet<ScheduledItem<?>> sSet) {
		List<ScheduleEvent> result = new ArrayList<ScheduleEvent>();
		for (ScheduledItem<?> elem : sSet)
			result.add(elem.getScheduleEvent());
		return result;
	}

	@Test
	public void testGetEventType() {
		Cast testCast = new Cast(testDiagnosis, "Foo Bone", 300);

		assertEquals(testCast.getEventType(), EventType.CAST);
	}

	@Test
	public void testFlowState() {
		Cast testCast = new Cast(testDiagnosis, "Foo Bone", 300);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);

		// CREATED
		try {
			testCast.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}

		testCast.store();

		// STORED
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}

		testCast.cancel();

		// CREATED

		testCast.redo();

		// STORED

		testCast.schedule(timePeriod, new ArrayList<ScheduleResource>(), null);

		// PLANNED
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}

		testCast.cancel();

		// CANCELLED
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testCast.getStart().execute();

		// IN PROGRESS
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}

		testCast.getStop().execute();

		// NEEDS RESULT
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}

		testCast.setResult(new CastResult("Test"));

		// FINISHED
		try {
			testCast.store();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.schedule(null, null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testCast.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
	}
}
