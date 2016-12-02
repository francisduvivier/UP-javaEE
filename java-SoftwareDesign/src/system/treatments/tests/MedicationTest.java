package system.treatments.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.patients.Diagnosis;
import system.patients.Patient;
import system.scheduling.Priority;
import system.staff.Doctor;
import system.treatments.Medication;
import system.warehouse.MedicationItemType;


public class MedicationTest {

	private Medication testMedication1;
	private Diagnosis testDiagnosis1;
	private Doctor testDoctor1;
	private Patient testPatient1;
	
	@Before
	public void testInit() {
		testDoctor1 = new Doctor("Alice");
		testPatient1 = new Patient("David");
		testDiagnosis1 =  new Diagnosis(testDoctor1, testPatient1, "Hoofdpijn");
	}
	
	@Test
	public void testMedicationContructor() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, "Dafalgan", true, medicationItemTypeList);
		assertEquals(testMedication1.getDescription(), "Dafalgan");
		assertEquals(testMedication1.isSensitive(), true);
		assertEquals(testMedication1.getPatient(), testPatient1);
		assertEquals(testMedication1.getDiagnosis(), testDiagnosis1);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1MedicationConstructor() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(null, "Dafalgan", true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2MedicationConstructor() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, null, true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3MedicationConstructor() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, "", true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4MedicationConstructor() {
		testMedication1 = new Medication(testDiagnosis1, "Aspirin", true, null);
	}
	
	@Test
	public void testMedicationContructor2() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, Priority.NORMAL, "Dafalgan", true, medicationItemTypeList);
		assertEquals(testMedication1.getDescription(), "Dafalgan");
		assertEquals(testMedication1.isSensitive(), true);
		assertEquals(testMedication1.getPatient(), testPatient1);
		assertEquals(testMedication1.getDiagnosis(), testDiagnosis1);
		assertEquals(testMedication1.getPriority(), Priority.NORMAL);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid1MedicationConstructor2() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(null, Priority.NORMAL, "Dafalgan", true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2MedicationConstructor2() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, Priority.NORMAL, null, true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3MedicationConstructor2() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, Priority.NORMAL, "", true, medicationItemTypeList);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid4MedicationConstructor2() {
		testMedication1 = new Medication(testDiagnosis1, Priority.NORMAL, "Aspirin", true, null);
	}
	
	@Test
	public void testToString() {
		List<MedicationItemType> medicationItemTypeList = new ArrayList<MedicationItemType>();
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		medicationItemTypeList.add(MedicationItemType.ASPIRIN);
		testMedication1 = new Medication(testDiagnosis1, Priority.NORMAL, "Dafalgan", true, medicationItemTypeList);
		assertNotSame(testMedication1.toString().length(), 0);
	}
	
}
