package system.treatments.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.patients.Diagnosis;
import system.patients.Patient;
import system.scheduling.Priority;
import system.staff.Doctor;
import system.time.TimeDuration;
import system.treatments.Cast;


/**
 * Unit Test Cast.java
 *
 */

public class CastTest {

	private Cast testCast1;
	private Diagnosis testDiagnosis1;
	private Doctor testDoctor1;
	private Patient testPatient1;
	
	@Before
	public void testInit() {
		testDoctor1 = new Doctor("Alice");
		testPatient1 = new Patient("David");
		testDiagnosis1 =  new Diagnosis(testDoctor1, testPatient1, "test");
		
	}
	
	@Test
	public void testCastContructor() {
		testCast1 = new Cast(testDiagnosis1, "Arm", 15);
		assertEquals(testCast1.getBodyPart(), "Arm");
		assertEquals(testCast1.getKeepDurationInDays(), 15);
		assertEquals(testCast1.getPatient(), testPatient1);
		assertEquals(testCast1.getDiagnosis(), testDiagnosis1);
		assertEquals(testCast1.getDuration(), TimeDuration.hours(2));
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidCastConstructor() {
		testCast1 = new Cast(null, "Arm", 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2CastConstructor() {
		testCast1 = new Cast(testDiagnosis1, "Arm", 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3CastConstructor() {
		testCast1 = new Cast(testDiagnosis1, "Arm", -1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4CastConstructor() {
		testCast1 = new Cast(testDiagnosis1, null, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5CastConstructor() {
		testCast1 = new Cast(testDiagnosis1, "", 15);
	}
	
	@Test
	public void testCastContructor2() {
		testCast1 = new Cast(testDiagnosis1, Priority.NORMAL, "Arm", 15);
		assertEquals(testCast1.getBodyPart(), "Arm");
		assertEquals(testCast1.getKeepDurationInDays(), 15);
		assertEquals(testCast1.getPatient(), testPatient1);
		assertEquals(testCast1.getDiagnosis(), testDiagnosis1);
		assertEquals(testCast1.getDuration(), TimeDuration.hours(2));
		assertEquals(testCast1.getPriority(), Priority.NORMAL);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidCastConstructor2() {
		testCast1 = new Cast(null, Priority.NORMAL, "Arm", 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2CastConstructor2() {
		testCast1 = new Cast(testDiagnosis1, Priority.NORMAL, "Arm", 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3CastConstructor2() {
		testCast1 = new Cast(testDiagnosis1, Priority.NORMAL, "Arm", -1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4CastConstructor2() {
		testCast1 = new Cast(testDiagnosis1, Priority.NORMAL, null, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5CastConstructor2() {
		testCast1 = new Cast(testDiagnosis1, Priority.NORMAL, "", 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid5CastConstructor6() {
		testCast1 = new Cast(testDiagnosis1, null, "Arm", 15);
	}
	
	@Test
	public void testToString() {
		testCast1 = new Cast(testDiagnosis1, "Arm", 15);
		assertNotSame(testCast1.toString().length(), 0);
	}
	
}
