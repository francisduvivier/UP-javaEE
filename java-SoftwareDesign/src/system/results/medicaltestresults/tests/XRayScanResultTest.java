package system.results.medicaltestresults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import system.results.ResultType;
import system.results.medicaltestresults.XRayScanResult;


/**
 * Unit Test XRayScanResult.java
 *
 */

public class XRayScanResultTest {

private XRayScanResult testXRayScanResult;
	
	@Test
	public void testXRayScanResultConstructor() {
		testXRayScanResult = new XRayScanResult("test", 1);
	}
	 
	@Test (expected = NullPointerException.class)
	public void testInvalid1XRayScanResultConstructor() {
		testXRayScanResult = new XRayScanResult(null, 1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2XRayScanResultConstructor() {
		testXRayScanResult = new XRayScanResult("", 1);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3XRayScanResultConstructor() {
		testXRayScanResult = new XRayScanResult("test", -1);
	}
	
	@Test
	public void testToString() {
		testXRayScanResult = new XRayScanResult("test", 1);
		assertNotSame(testXRayScanResult.toString(), 0);
	}
	
	@Test
	public void testGetDetails() {
		testXRayScanResult = new XRayScanResult("test", 1);
		assertNotSame(testXRayScanResult.getDetails(), 0);
	}
	
	@Test
	public void testResourceType() {
		testXRayScanResult = new XRayScanResult("test", 1);
		assertEquals(testXRayScanResult.getResultType(), ResultType.XRAY_SCAN_RESULT);
	}
	
}
