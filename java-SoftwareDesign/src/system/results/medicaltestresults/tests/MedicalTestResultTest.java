package system.results.medicaltestresults.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import system.results.medicaltestresults.BloodAnalysisResult;
import system.results.medicaltestresults.ScanMatter;
import system.results.medicaltestresults.UltrasoundScanResult;
import system.results.medicaltestresults.XRayScanResult;


public class MedicalTestResultTest {
	private BloodAnalysisResult resultB1, resultB2;
	private XRayScanResult resultX1, resultX2;
	private UltrasoundScanResult resultU1, resultU2;
	
	@Test
	public void testEquals() {
		resultB1 = new BloodAnalysisResult(1, 2, 3, 4);
		assertTrue(resultB1.equals(resultB1));
		assertFalse(resultB1.equals(new Object()));
		resultB2 = new BloodAnalysisResult(1, 2, 3, 3);
		assertFalse(resultB1.equals(resultB2));
		resultB2 = new BloodAnalysisResult(1, 2, 3, 4);
		assertTrue(resultB1.equals(resultB2));
		
		resultX1 = new XRayScanResult("None",1);
		assertTrue(resultX1.equals(resultX1));
		assertFalse(resultX1.equals(new Object()));
		resultX2 = new XRayScanResult("Lots",1);
		assertFalse(resultX1.equals(resultX2));
		resultX2 = new XRayScanResult("None",1);
		assertTrue(resultX1.equals(resultX2));
		
		resultU1 = new UltrasoundScanResult("Info",ScanMatter.BENIGN);
		assertTrue(resultU1.equals(resultU1));
		assertFalse(resultU1.equals(new Object()));
		resultU2 = new UltrasoundScanResult("Info",ScanMatter.MALIGNANT);
		assertFalse(resultU1.equals(resultU2));
		resultU2 = new UltrasoundScanResult("Info",ScanMatter.BENIGN);
		assertTrue(resultU1.equals(resultU2));
	}
}
