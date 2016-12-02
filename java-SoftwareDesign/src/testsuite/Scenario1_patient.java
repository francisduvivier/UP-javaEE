package testsuite;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import client.IDiagnosis;

import system.exceptions.InsufficientWarehouseItemsException;
import system.medicaltests.BloodAnalysis;
import system.medicaltests.MedicalTest;
import system.medicaltests.UltrasoundScan;
import system.medicaltests.XRayScan;
import system.patients.Patient;
import system.results.medicaltestresults.ScanMatter;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.treatments.Medication;
import system.treatments.Surgery;
import system.treatments.Treatment;
import system.warehouse.MedicationItemType;


/**
 * Scenario 1: patient registreren, afspraak maken, diagnose maken, 2e mening vragen, medische tests/treatments bestellen, 
 * patienten ontslaan, resultaten verwerken, ...
 * 
 * @author Groep 10
 *
 */

public class Scenario1_patient extends TestWithControllers {

	/**
	 * Logt de nurse van de superklasse in.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testC1Nurse1, getCampus(0));
	}
	
	@Test
	public void scenario1() {
		// registreren nieuwe patient
        Patient testPatient2 = nurseController.registerNewPatient("Test");
                
        // afspraak regelen
        nurseController.scheduleAppointment(testC1Doctor1);
        
        // dokter consulteert patient file en geeft diagnose op
        IDiagnosis testDiagnosis = enterDiagnosis(testPatient2);
        
        // dokter schrijft een treatment voor en bestelt een medical test
        doctorController.prescribeCast(testDiagnosis, "Arm", 15);
        doctorController.orderXRayScan("Arm", 15, 3);
        
        // hospital admin zet de tijd vooruit zodat resultaten kunnen geregistreerd worden
        advanceTime();
        
        // nurse registreert resultaten
        registerResultsPatient2();
        
        // dokter ontslaat patient
        dischargePatient2();
        
        // nurse registreert nieuwe patient en plant afspraak
        sessionController.login(testC1Nurse1, sessionController.getCampuses().get(0));
        Patient testPatient3 = nurseController.registerNewPatient("TestPatient3");
        nurseController.scheduleAppointment(testC1Doctor1);
        
        // dokter logt in, opent patient file van nieuwe patient en vult diagnose in en vraagt tweede opinie
        enterDiagnosisWithSecondOpinion(testPatient3);
        IDiagnosis testDiagnosis2 = doctorController.getDiagnoses().get(0);
        List<MedicationItemType> medList = new ArrayList<MedicationItemType>();
        medList.add(MedicationItemType.MISC);
        
        // dokter schrijft medicatie voor en bestelt een bloedanalyse
        doctorController.prescribeMedication(testDiagnosis2, "Anti allergie", true, medList);
        doctorController.orderBloodAnalysis("White cells", 3);
        assertEquals(doctorController.getDiagnoses().size(), 1);
        
        // een tweede dokter keurt de diagnose af voor testPatient3
        denyDiagnosis(testPatient3, testDiagnosis2);  
        
        // dokter vult een nieuwe diagnose in voor testPatient3
        enterDiagnosisWithSecondOpinion(testPatient3);
        
        // een tweede dokter keurt de diagnose goed voor testPatient3
        IDiagnosis testDiagnosis2b = doctorController.getDiagnoses().get(0);
        approveDiagnosis(testPatient3, testDiagnosis2b);

        registerAgain(testPatient2);
        
        // acties uitvoeren op testPatient2
        sessionController.login(testC1Doctor1, getCampus(0));
        doctorController.consultPatientFile(testPatient2);
        assertEquals(doctorController.getResults().size(), 2);
        
        // medical tests bestellen
        doctorController.orderBloodAnalysis("Test", 5);
        doctorController.orderUltrasoundScan("test", true, false);
        doctorController.orderXRayScan("Test", 50, 10);
        
        assertEquals(doctorController.getDiagnoses().size(), 1);
        doctorController.enterDiagnosis("Vanalles");
        assertEquals(doctorController.getDiagnoses().size(), 2);
        IDiagnosis testDiagnosis3 = doctorController.getDiagnoses().get(1);
        
        // treatments voorschrijven
        doctorController.prescribeCast(testDiagnosis3, "test", 15);
        List<MedicationItemType> medicationItems = new ArrayList<MedicationItemType>();
        doctorController.prescribeMedication(testDiagnosis3, "Vanalles", false, medicationItems);
        doctorController.prescribeSurgery(testDiagnosis3, "Test");
        
        sessionController.logOut();
        
        sessionController.login(testC2Nurse1, getCampus(1));
        List<MedicalTest> medicalTests = nurseController.getScheduledMedicalTests();
        
        BloodAnalysis testBloodAnalysis = (BloodAnalysis) medicalTests.get(0);
        UltrasoundScan testUltrasoundScan = (UltrasoundScan) medicalTests.get(1);
        XRayScan testXRayScan = (XRayScan) medicalTests.get(2);
        
        assertEquals(nurseController.getScheduledMedicalTests().size(), 3);
        
        List<Treatment> treatments = nurseController.getScheduledTreatments();
        Cast testCast = (Cast) treatments.get(0);
        Medication testMedication = (Medication) treatments.get(1);
        Surgery testSurgery = (Surgery) treatments.get(2);
        
        assertEquals(nurseController.getScheduledTreatments().size(),3);

        sessionController.login(testC1Doctor1, getCampus(0));
        
        doctorController.consultPatientFile(testPatient2);        
        // enkele diagnosissen ingeven
        for (int i = 0; i < 50; i++) {
        	doctorController.enterDiagnosis(""+i);
        }
        assertEquals(doctorController.getDiagnoses().size(), 52);
        // ongedaan maken enterDiagnosis testen (dokter bedenkt zich)
        for (int i = 0; i < 10; i++) {
        	doctorController.undoCommand(i);
        }
        assertEquals(doctorController.getDiagnoses().size(), 42);
        // redo'en enterDiagnosis (dokter was toch correct)
        for (int i = 0; i < 3; i++) {
        	doctorController.redoCommand(i);
        }
        assertEquals(doctorController.getDiagnoses().size(), 45);
        
        assertEquals(doctorController.getResults().size(), 2);
        
        advanceTime();
        
        // nurse registreert resultaten van medical tests
        registerMedicalTestResultsPatient3(testBloodAnalysis, testUltrasoundScan,
				testXRayScan, testPatient2);
        
        // nurse registreert resultaten van treatments
        registerTreatmentResultsPatient3(testCast, testMedication, testSurgery);
        
        // dokter ontslaat testPatient3
        dischargePatient3(testPatient2);       
	}
	
	/**
	 * Nurse registreert te veel patienten en het eten is op;
	 * een exception wordt gegooid.
	 */
	@Test (expected = InsufficientWarehouseItemsException.class)
	public void testPatientOverload() {
	    sessionController.login(testC1Nurse1, getCampus(0));
	    for (int i = 0; i < 20; i++) 
	    	nurseController.registerNewPatient("nieuwe patient, nr. " + i);
	}

	/**
	 * We registreren een patient en plannen een afspraak met de dokter
	 * @param testPatient2
	 * 		  	Patient die we registreren en waarvoor we een afspraak plannen
	 */
	private void registerAgain(Patient testPatient2) {
		sessionController.login(testC1Nurse1, getCampus(0));
        // ontslagen patient 2 opnieuw registeren
        nurseController.registerPatient(testPatient2);
        nurseController.scheduleAppointment(testC2Doctor1);
	}
	
	/**
	 * We loggen in met een dokter en ontslaan een patient,
	 * vervolgens sluiten we zijn patient file.
	 * We kijken ook of we wel de juiste patient hadden geselecteerd
	 * en hoeveel resultaten over hem vergaard zijn
	 * @param testPatient2
	 * 			Patient die we ontslaan
	 */
	private void dischargePatient3(Patient testPatient2) {
		sessionController.login(testC1Doctor1, getCampus(0));
        assertEquals(doctorController.getSelectedPatient(), testPatient2);
        assertEquals(doctorController.getResults().size(), 8);
        doctorController.dischargePatient();
        doctorController.closePatientFile();
	}

	/**
	 * De dokter ontslaat een patient en sluit zijn patient file.
	 * We kijken alvorens eerst hoeveel actieve (=niet ontslaan)
	 * patienten er zijn en vervolgens hoeveel er nog over zijn.
	 */
	private void dischargePatient2() {
		sessionController.login(testC1Doctor1, sessionController.getCampuses().get(0));
		assertEquals(doctorController.getNonDischargedPatients().size(), 3); // zijn al 2 patients in superklasse
        doctorController.dischargePatient();
        doctorController.closePatientFile();
        assertEquals(doctorController.getNonDischargedPatients().size(), 2);
	}
	
	/**
	 * Een nurse registreert enkele treatment resultaten voor de
	 * geselecteerde patient. We zien ook dat het aantal unfinished tests
	 * correct is afgenomen.
	 * @param testCast
	 * 			Cast (treatment) wat we linken aan het resultaat
	 * @param testMedication
	 * 			Medication (treatment) wat we linken aan het resultaat
	 * @param testSurgery
	 * 			Surgery (treatment) wat we linken aan het resultaat
	 */
	private void registerTreatmentResultsPatient3(Cast testCast,
			Medication testMedication, Surgery testSurgery) {
		sessionController.login(testC1Nurse1, getCampus(0));
		assertEquals(nurseController.getUnfinishedTreatments().size(), 0);

        nurseController.registerCastResult(testCast, "test");
        nurseController.registerMedicationResult(testMedication, false, "test");
        nurseController.registerSurgeryResult(testSurgery, "Test", "test");
        
        assertEquals(nurseController.getUnfinishedTreatments().size(), 0);
	}
	
	/**
	 * Een nurse registreert enkele medical test resultaten.
	 * We zien ook het aantal onafgewerkte tests correct afnemen.
	 * @param testBloodAnalysis
	 * 			Blood Analyse (medical test) wat we linken aan het resultaat
	 * @param testUltrasoundScan
	 * 			Ultrasound Scan (medical test) wat we linken aan het resultaat
	 * @param testXRayScan
	 * 			X-Ray Scan (medical test) wat we linken aan het resultaat
	 */
	private void registerMedicalTestResultsPatient3(BloodAnalysis testBloodAnalysis,
			UltrasoundScan testUltrasoundScan, XRayScan testXRayScan, Patient patient) {
		sessionController.login(testC1Nurse1, getCampus(0));
        assertEquals(nurseController.getUnfinishedMedicalTests().size(), 1);
        sessionController.login(testC2Nurse1, getCampus(1));
        assertEquals(nurseController.getUnfinishedMedicalTests().size(), 3);
        nurseController.registerBloodAnalysisResult(testBloodAnalysis, 15, 15, 15, 15);
        assertEquals(nurseController.getUnfinishedMedicalTests().size(), 2);
        nurseController.registerUltrasoundScanResult(testUltrasoundScan, "test", ScanMatter.BENIGN);
        assertEquals(nurseController.getUnfinishedMedicalTests().size(), 1);
        nurseController.registerXRayScanResult(testXRayScan, "Test", 15);
        assertEquals(nurseController.getUnfinishedMedicalTests().size(), 0);
	}

	/**
	 * Een nurse registreert 2 resultaten: 1 treatment en 1 medical test
	 * @param cast
	 * 			De treatment die we registreren
	 * @param xRayScan
	 * 			De medical test die we registreren
	 */
	private void registerResultsPatient2() {
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerXRayScanResult((XRayScan) nurseController.getUnfinishedMedicalTests().get(0), "geen", 3); 
		nurseController.registerCastResult((Cast) nurseController.getUnfinishedTreatments().get(0), "genezen");
	}
	/**
	 * De hospital administrator zet de tijd vooruit zodat we de
	 * resultaten kunnen verwerken
	 */
	private void advanceTime() {
		sessionController.login(testHospitalAdmin, getCampus(0));
        adminController.advanceTime(TimeStamp.addedToTimeStamp(TimeDuration.days(1), sessionController.getTime()));
	}
	
	/**
	 * Een dokter consulteert een patient file, waarvan de diagnose is opgesteld door een andere
	 * dokter en verwerpt zijn diagnose
	 * @param testPatient3
	 * 			De patient wiens diagnose de dokter verwerpt
	 * @param testDiagnosis2
	 * 			De respectievelijke diagnose
	 */
	private void denyDiagnosis(Patient testPatient3, IDiagnosis testDiagnosis2) {
		sessionController.login(testC2Doctor1, getCampus(0));
        doctorController.consultPatientFile(testPatient3);
        doctorController.denyDiagnosis(testDiagnosis2, "wrong");
        assertEquals(doctorController.getDiagnoses().size(), 1);
	}
	
	/**
	 * Een dokter keurt een diagnose goed van een patient
	 * @param testPatient3
	 * 			De patient waarvan we de diagnose moeten goedkeuren
	 * @param testDiagnosis2b
	 * 			De diagnose die we goedkeuren
	 */
	private void approveDiagnosis(Patient testPatient3, IDiagnosis testDiagnosis2b) {
		sessionController.login(testC2Doctor1, getCampus(0));
        doctorController.consultPatientFile(testPatient3);
        doctorController.approveDiagnosis(testDiagnosis2b);
        assertEquals(doctorController.getDiagnoses().size(), 2);
	}

	/**
	 * Een dokter geeft een diagnose in en duidt een andere dokter
	 * aan waarvan hij een tweede opinie eist
	 * @param testPatient3
	 * 			Patient die een diagnose krijgt
	 */
	private void enterDiagnosisWithSecondOpinion(Patient testPatient3) {
		sessionController.login(testC1Doctor1, getCampus(0));
        doctorController.consultPatientFile(testPatient3);
        doctorController.enterDiagnosis(testC2Doctor1, "Allergie");
	}

	/**
	 * Een dokter consulteert een patient (file) en geeft
	 * een diagnose mee.
	 * @param testPatient2
	 * 			Te consulteren patient
	 * @return diagnose
	 * 			De ingevulde diagnose voor testPatient2
	 */
	private IDiagnosis enterDiagnosis(Patient testPatient2) {
		sessionController.login(testC1Doctor1, getCampus(0));
        doctorController.consultPatientFile(testPatient2);
        doctorController.enterDiagnosis("Test");
        IDiagnosis testDiagnosis = doctorController.getDiagnoses().get(0);
		return testDiagnosis;
	}
	
}
