package system.medicaltests.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.UltrasoundScan;
import system.patients.Patient;
import system.scheduling.Priority;


/**
 * Unit Test UltrasoundScan.java
 *
 */

public class UltrasoundScanTest {

	private UltrasoundScan testUltrasoundScan;
	private Patient testPatient;
	
	
	@Before
	public void testInit() {
		testPatient = new Patient("Alice");
	}
	
	@Test
	public void testUltrasoundScanConstructor() {
		testUltrasoundScan = new UltrasoundScan(testPatient, "Witte Bloedcellen", true, true);
		assertEquals(testUltrasoundScan.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor() {
		testUltrasoundScan = new UltrasoundScan(null, "Witte Bloedcellen", true, true);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor() {
		testUltrasoundScan = new UltrasoundScan(testPatient, null, true, true);
	}
	
	@Test
	public void testUltrasoundScanConstructor2() {
		testUltrasoundScan = new UltrasoundScan(testPatient, Priority.NO_PRIORITY, "Witte Bloedcellen", true, true);
		assertEquals(testUltrasoundScan.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor2() {
		testUltrasoundScan = new UltrasoundScan(null, null, "Witte Bloedcellen", true, true);
	}
	
	@Test (expected = NullPointerException.class)
	public void testUltrasoundScanConstructor3() {
		testUltrasoundScan = new UltrasoundScan(testPatient, Priority.NO_PRIORITY, "", true, true);
		assertEquals(testUltrasoundScan.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor4() {
		testUltrasoundScan = new UltrasoundScan(testPatient, Priority.NO_PRIORITY, null, true, true);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor5() {
		testUltrasoundScan = new UltrasoundScan(testPatient, null, "Witte Bloedcellen", true, true);
	}
	
	@Test
	public void testToString() {
		testUltrasoundScan = new UltrasoundScan(testPatient, Priority.NO_PRIORITY, "Witte Bloedcellen", true, true);
		assertNotSame(testUltrasoundScan.toString().length(),0);
	}
		
}
