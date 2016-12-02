package system.results.medicaltestresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.medicaltestresults.ScanMatter;
import system.results.medicaltestresults.UltrasoundScanResult;


/**
 * Unit Test UltraSoundScanResult.java
 *
 */

public class UltrasoundScanResultTest {

private UltrasoundScanResult testUltrasoundScan;
	
	@Test
	public void testUltrasoundScanResultConstructor() {
		testUltrasoundScan = new UltrasoundScanResult("Test", ScanMatter.BENIGN);
	}
	 
	@Test (expected = NullPointerException.class)
	public void testInvalid1UltrasoundScanResultConstructor() {
		testUltrasoundScan = new UltrasoundScanResult(null, ScanMatter.BENIGN);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2UltrasoundScanResultConstructor() {
		testUltrasoundScan = new UltrasoundScanResult("", ScanMatter.BENIGN);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3UltrasoundScanResultConstructor() {
		testUltrasoundScan = new UltrasoundScanResult("Test", null);
	}
	
	@Test
	public void testToString() {
		testUltrasoundScan = new UltrasoundScanResult("Test", ScanMatter.BENIGN);
		assertNotSame(testUltrasoundScan.toString(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testUltrasoundScan = new UltrasoundScanResult("Test", ScanMatter.BENIGN);
		assertNotSame(testUltrasoundScan.getDetails(), 0);
	}
	
	@Test
	public void testResourceType() {
		testUltrasoundScan = new UltrasoundScanResult("Test", ScanMatter.BENIGN);
		assertEquals(testUltrasoundScan.getResultType(), ResultType.ULTRASOUND_SCAN_RESULT);
	}
	
}
