package system.medicaltests.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
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
import system.medicaltests.BloodAnalysis;
import system.medicaltests.MedicalTest;
import system.patients.Patient;
import system.results.Result;
import system.results.medicaltestresults.BloodAnalysisResult;
import system.results.medicaltestresults.ScanMatter;
import system.results.medicaltestresults.UltrasoundScanResult;
import system.scheduling.EventType;
import system.scheduling.ScheduleEvent;
import system.scheduling.ScheduleResource;
import system.scheduling.ScheduledItem;
import system.staff.Nurse;
import system.time.TimePeriod;
import system.time.TimeStamp;


public class MedicalTestTest {

	private MedicalTest medicalTest;
	private Patient testPatient;
	private Nurse testNurse;

	@Before
	public void testInit() {
		testPatient = new Patient("Alice");
		testNurse = new Nurse("Bob");
		medicalTest = new BloodAnalysis(testPatient, "Witte Bloedcellen", 15);
	}

	@Test
	public void testFlowState() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		
		// CREATED
		try {
			testBloodAnalysis.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testBloodAnalysis.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		
		// PLANNED
		try {
			testBloodAnalysis.schedule(null,null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testBloodAnalysis.cancel();
		
		// CANCELLED
		try {
			testBloodAnalysis.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.schedule(null,null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testBloodAnalysis.getStart().execute();
		
		// IN PROGRESS
		try {
			testBloodAnalysis.schedule(null,null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testBloodAnalysis.getStop().execute();

		// NEEDS RESULT
		try {
			testBloodAnalysis.schedule(null,null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		
		testBloodAnalysis.setResult(new BloodAnalysisResult(1, 2, 3, 4));
		
		// FINISHED
		try {
			testBloodAnalysis.schedule(null,null, null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.cancel();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStart().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.getStop().execute();
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (IllegalOperationException e) {}
	}

	@Test
	public void testGetScheduledPeriod() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		assertEquals(testBloodAnalysis.getScheduledPeriod(), null);
		testBloodAnalysis.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		assertEquals(testBloodAnalysis.getScheduledPeriod(), timePeriod);
	}
	
	@Test
	public void testIsFinished() {
		TimeStamp time1 = new TimeStamp(2011, 11, 20, 10, 50), time2 = new TimeStamp(
				2011, 11, 20, 11, 20);

		TimePeriod timePeriod = new TimePeriod(time1, time2);

		assertFalse(this.medicalTest.isFinished());
		this.medicalTest.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		assertFalse(this.medicalTest.isFinished());
		this.medicalTest.getStart().execute();
		assertFalse(this.medicalTest.isFinished());
		this.medicalTest.getStop().execute();
		assertFalse(this.medicalTest.isFinished());
		medicalTest.setResult(new BloodAnalysisResult(1, 2, 3, 4));
		assertTrue(this.medicalTest.isFinished());
	}
	@Test
	public void testCancelRedo() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15), testBloodAnalysis2 = new BloodAnalysis(
				testPatient, "Witte Bloedcellen", 15);
		List<ScheduleResource> resources = new ArrayList<ScheduleResource>();

		resources.add(testPatient);
		resources.add(testNurse);

		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testBloodAnalysis.schedule(timePeriod, resources, null);
		testBloodAnalysis.cancel();
		assertEquals(testBloodAnalysis.getScheduledPeriod(), null);
		for (ScheduleResource resource : resources)
			assertTrue(resource.getSchedule().getSchedule().isEmpty());
		testBloodAnalysis.redo();
		assertEquals(testBloodAnalysis.getScheduledPeriod(), timePeriod);
		for (ScheduleResource resource : resources) {
			List<ScheduleEvent> list = scheduleToList(resource.getSchedule()
					.getSchedule());
			assertTrue(list.contains(testBloodAnalysis));
			assertEquals(list.size(), 1);
		}
		testBloodAnalysis.cancel();
		
		resources.remove(testNurse);
		
		testBloodAnalysis2.schedule(timePeriod, resources, null);
		try {
			testBloodAnalysis.redo();
			fail("Test Failed");
		} catch (ReschedulingException e) {
		}
		assertEquals(testBloodAnalysis.getScheduledPeriod(), null);
		assertEquals(testBloodAnalysis2.getScheduledPeriod(), timePeriod);
		for (ScheduleResource resource : resources) {
			List<ScheduleEvent> list = scheduleToList(resource.getSchedule()
					.getSchedule());
			assertTrue(list.contains(testBloodAnalysis2));
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
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);

		assertEquals(testBloodAnalysis.getEventType(), EventType.BLOOD_ANALYSIS);
	}

	@Test
	public void testDuration() {
		assertNotSame(medicalTest.getDuration(), 0);
	}

	@Test
	public void testResult() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		assertFalse(testBloodAnalysis.needsResult());
		testBloodAnalysis.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		assertFalse(testBloodAnalysis.needsResult());
		testBloodAnalysis.getStart().execute();
		assertFalse(testBloodAnalysis.needsResult());
		testBloodAnalysis.getStop().execute();
		assertTrue(testBloodAnalysis.needsResult());
		Result result = new BloodAnalysisResult(1, 2, 3, 4);
		testBloodAnalysis.setResult(result);
		assertFalse(testBloodAnalysis.needsResult());
	}

	@Test
	public void testInvalidSetResult() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testBloodAnalysis.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		testBloodAnalysis.getStart().execute();
		testBloodAnalysis.getStop().execute();
		try {
			testBloodAnalysis.setResult(null);
			fail("Test Failed");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testInvalid2SetResult() {
		BloodAnalysis testBloodAnalysis = new BloodAnalysis(testPatient,
				"Witte Bloedcellen", 15);
		TimeStamp time = new TimeStamp(2012, 10, 10, 0, 0), time2 = new TimeStamp(
				2012, 10, 10, 30, 0);
		TimePeriod timePeriod = new TimePeriod(time, time2);
		testBloodAnalysis.schedule(timePeriod,
				new ArrayList<ScheduleResource>(), null);
		Result result = new UltrasoundScanResult("Test", ScanMatter.UNKNOWN);
		testBloodAnalysis.getStart().execute();
		testBloodAnalysis.getStop().execute();
		try {
			testBloodAnalysis.setResult(result);
			fail("Test Failed");
		} catch (ResultMismatchException e) {
		}

	}

}
