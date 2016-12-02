package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.exceptions.IllegalOperationException;
import system.medicaltests.BloodAnalysis;
import system.medicaltests.UltrasoundScan;
import system.medicaltests.XRayScan;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.results.medicaltestresults.BloodAnalysisResult;
import system.results.medicaltestresults.ScanMatter;
import system.results.medicaltestresults.UltrasoundScanResult;
import system.results.medicaltestresults.XRayScanResult;
import system.scheduling.ScheduleEvent;
import system.staff.Doctor;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.treatments.Medication;
import system.treatments.Surgery;
import system.warehouse.MedicationItemType;
import testsuite.TestWithControllers;


/**
 * EnterResultsTest.java is een test-case voor volgende use-cases:
 * 	- Enter Medical Test Result
 *  - Enter Treatment Result
 *
 * Enter Medical Test Result en Enter Treatment Result zijn verwerkt in ResultController.java.
 */



public class EnterResultsTest extends TestWithControllers {
	TimeStamp tomorrow;
	TimeStamp now;
	Patient testPatient2;
	/**
	 * Init voor AddHospitalStaffAndEquipmentTest
	 * 
	 * Een administrator controller wordt geïnitialiseerd.
	 */
	@Override
	@Before
	public void testInit() {	
		super.testInit();
		this.now=getFutureTime(0);
		this.tomorrow = getFutureTime(1);
		sessionController.login(testC1Nurse1, getCampus(0));
	}
	
	public void registerInit(ScheduleEvent event){
		
		// geldige registratie
		hospital.getScheduler().schedule(event, tomorrow);
		event.getStart().execute();
		event.getStop().execute();
		sessionController.login(testC1Nurse1, getCampus(0));
		testPatient2=nurseController.registerNewPatient("patient2");
		
		
	}
		
	/**
	 * We testen of we medical test results kunnen registreren.
	 */
	@Test
	public void testRegisterBloodAnalysisResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		BloodAnalysis testBloodAnalysis1=new BloodAnalysis(testC1Patient1, "Focus", 3);
		registerInit(testBloodAnalysis1);
		BloodAnalysisResult result=nurseController.registerBloodAnalysisResult(testBloodAnalysis1, 30, 620, 700, 120);
		assertTrue(testBloodAnalysis1.getResult()==result);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1RegisterBloodAnalysisResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		BloodAnalysis testBloodAnalysis2 = new BloodAnalysis(testPatient2, "STD's", 4);
		registerInit(testBloodAnalysis2);
		BloodAnalysisResult result = nurseController.registerBloodAnalysisResult(testBloodAnalysis2, 30, 620, 700, 120);
		assertTrue(testBloodAnalysis2.getResult() == result);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2RegisterBloodAnalysisResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		BloodAnalysis testBloodAnalysis2 = new BloodAnalysis(testC1Patient1, "", 4);
		registerInit(testBloodAnalysis2);
		BloodAnalysisResult result = nurseController.registerBloodAnalysisResult(testBloodAnalysis2, 30, 620, 700, 120);
		assertTrue(testBloodAnalysis2.getResult() == result);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3RegisterBloodAnalysisResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		BloodAnalysis testBloodAnalysis2 = new BloodAnalysis(testC1Patient1, "STD's", -1);
		registerInit(testBloodAnalysis2);
		BloodAnalysisResult result = nurseController.registerBloodAnalysisResult(testBloodAnalysis2, 30, 620, 700, 120);
		assertTrue(testBloodAnalysis2.getResult() == result);
	}
		
	/**
	 * We testen of we een Ultrasound scan resultaat kunnen registreren.
	 */
	@Test
	public void testRegisterUltrasoundScanResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		UltrasoundScan testUltrasoundScan1=new UltrasoundScan(testC1Patient1, "Arm", true, true);
		registerInit(testUltrasoundScan1);
		UltrasoundScanResult result=nurseController.registerUltrasoundScanResult(testUltrasoundScan1, "Test", ScanMatter.MALIGNANT);
		assertTrue(testUltrasoundScan1.getResult()==result);
	}
	
	@Test
	public void testRegisterXRayScanResult() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		XRayScan testXRayScan1=new XRayScan(testC1Patient1, "Arm", 12, 12);
		registerInit(testXRayScan1);
		XRayScanResult result=nurseController.registerXRayScanResult(testXRayScan1, "Test", 15);
		assertTrue(testXRayScan1.getResult()==result);
	}
	
	/**
	 * We testen of we medication results kunnen registreren.
	 */
	@Test
	public void testRegisterMedicationResult() {
		// geldige registratie
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.register();
		
		List<MedicationItemType> medicationItemTypes = new ArrayList<MedicationItemType>();
		medicationItemTypes.add(MedicationItemType.VITAMINS);
		medicationItemTypes.add(MedicationItemType.SLEEPING_TABLETS);
		
		Medication testMedication1 = new Medication(testDiagnosis, "ARX-7 Medicatie", true, medicationItemTypes);
		testMedication1.store();
		
		registerInit(testMedication1);
		nurseController.registerMedicationResult(testMedication1, true, "Test");
	}
	
	/**
	 * We testen of we een geldige cast result kunnen registeren.
	 */
	@Test
	public void testRegisterCastResult() {
		// geldige registratie
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.register();
		
		Cast testCast1 = new Cast(testDiagnosis, "ARX-7 Medicatie", 15);
		testCast1.store();
		registerInit(testCast1);
		nurseController.registerCastResult(testCast1, "Test");
	}
	
	/**
	 * We testen of we een geldige surgery result kunnen registreren.
	 */
	@Test
	public void testRegisterSurgeryResult() {
		// geldige registratie
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.register();
		
		Surgery testSurgery1 = new Surgery(testDiagnosis, "ARX-7 Medicatie");
		testSurgery1.store();
		registerInit(testSurgery1);
		nurseController.registerSurgeryResult(testSurgery1, "Test", "Test");
	}
	
	@Test 
	public void testInvalid1RegisterSurgeryResult() {
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.register();
		
		Surgery testSurgery1 = new Surgery(testDiagnosis, "ARX-7 Medicatie");
		testSurgery1.store();
		registerInit(testSurgery1);
		try {
			nurseController.registerSurgeryResult(null, "Test", "Test");
			fail("Something went wrong");
		} catch (NullPointerException e) {}
	}
	
	@Test 
	public void testInvalid2RegisterSurgeryResult() {
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, new Patient("Freddy"), "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		try {
			testDiagnosis.register();
			fail("Something went wrong");
		} catch (IllegalOperationException e) {
		
			Surgery testSurgery1 = new Surgery(testDiagnosis, "ARX-7 Medicatie");
			testSurgery1.store();
			registerInit(testSurgery1);
			try {
				nurseController.registerSurgeryResult(null, "Test", "Test");
				fail("Something went wrong");
			} catch (NullPointerException f) {}
		}
	}
	
	
	/**
	 * We testen of unfinishedMedicalTests werkt.
	 */
	@Test
	public void getUnfinishedMedicalTestsTest() {
	
		String bAFocus="STD's";
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.orderBloodAnalysis("STD's", 4);
		sessionController.login(testHospitalAdmin, getCampus(0));
		TimeStamp advanceDay = TimeStamp.addedToTimeStamp(TimeDuration.days(1), 
			this.hospital.getHospitalTime().getTime());
		adminController.advanceTime(advanceDay);
		sessionController.login(testC1Nurse1, getCampus(0));
		BloodAnalysis foundBlAn=(BloodAnalysis) nurseController.getUnfinishedMedicalTests().get(0);
		assertSame(testC1Patient1,foundBlAn.getPatient());
		assertSame(bAFocus,foundBlAn.getFocus());
	}
	
	/**
	 * We testen of unfinishedTreatments werkt.
	 */
	@Test
	public void getUnfinishedTreatmentsTest() {
		Diagnosis testDiagnosis = new Diagnosis((Doctor) testC1Doctor1, testC1Patient1, "Eten van foute voeding.");
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		testDiagnosis.register();
		
		List<MedicationItemType> medicationItemTypes = new ArrayList<MedicationItemType>();
		medicationItemTypes.add(MedicationItemType.VITAMINS);
		medicationItemTypes.add(MedicationItemType.SLEEPING_TABLETS);
		String medDescription="ARX-7 Medicatie";
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.prescribeMedication(testDiagnosis, medDescription, true, medicationItemTypes);		
		sessionController.login(testHospitalAdmin, getCampus(0));
		TimeStamp advanceDay = TimeStamp.addedToTimeStamp(TimeDuration.days(1), 
			this.hospital.getHospitalTime().getTime());
		adminController.advanceTime(advanceDay);
		sessionController.login(testC1Nurse1, getCampus(0));
		Medication foundMed=(Medication) nurseController.getUnfinishedTreatments().get(0);
		assertSame(testDiagnosis,foundMed.getDiagnosis());
		assertSame(medDescription,foundMed.getDescription());
		assertSame(testC1Patient1,foundMed.getPatient());
		assertSame(true,foundMed.isSensitive());
	}
	
	@Test
	public void testGetDoctors() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.addDoctor("Steven");
		adminController.addDoctor("Tom");
		sessionController.login(testC1Nurse1, getCampus(0));
		assertEquals(nurseController.getDoctors().size(), 7);
	}
}
