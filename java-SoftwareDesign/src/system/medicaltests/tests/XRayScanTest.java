package system.medicaltests.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.XRayScan;
import system.patients.Patient;
import system.scheduling.Priority;


/**
 * Unit Test XRayScan.java
 *
 */

public class XRayScanTest {
	private XRayScan testXRayScan;
	private Patient testPatient;
	
	@Before
	public void testInit() {
		testPatient = new Patient("Alice");
	}
	
	@Test
	public void testXRayScanConstructor() {
		testXRayScan = new XRayScan(testPatient, "Witte Bloedcellen", 15, 15);
		assertEquals(testXRayScan.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor() {
		testXRayScan = new XRayScan(null, "Witte Bloedcellen", 15, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor() {
		testXRayScan = new XRayScan(testPatient, null, 15, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3Constructor() {
		testXRayScan = new XRayScan(testPatient, "", 15, 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid4Constructor() {
		testXRayScan = new XRayScan(testPatient, "Witte Bloedcellen", -1, 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid5Constructor() {
		testXRayScan = new XRayScan(testPatient, "Witte Bloedcellen", 1, -15);
	}
	
	@Test
	public void testXRayScanConstructor2() {
		testXRayScan = new XRayScan(testPatient, Priority.NORMAL, "Witte Bloedcellen", 15, 15);
		assertEquals(testXRayScan.getPatient(), testPatient);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidConstructor2() {
		testXRayScan = new XRayScan(null, Priority.NORMAL, "Witte Bloedcellen", 15, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2Constructor2() {
		testXRayScan = new XRayScan(testPatient, Priority.NORMAL, null, 15, 15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3Constructor2() {
		testXRayScan = new XRayScan(testPatient, Priority.NORMAL, "", 15, 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid4Constructor2() {
		testXRayScan = new XRayScan(testPatient, Priority.NORMAL, "Witte Bloedcellen", -1, 15);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid5Constructor2() {
		testXRayScan = new XRayScan(testPatient, Priority.NORMAL, "Witte Bloedcellen", 1, -15);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid6Constructor2() {
		testXRayScan = new XRayScan(testPatient, null, "Witte Bloedcellen", 1, 15);
	}
	
	@Test
	public void testToString() {
		testXRayScan = new XRayScan(testPatient, "Witte Bloedcellen", 15, 15);
		assertNotSame(testXRayScan.toString().length(),0);
	}
	
}
