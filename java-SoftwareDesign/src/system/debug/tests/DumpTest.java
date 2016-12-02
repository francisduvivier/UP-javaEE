package system.debug.tests;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import system.debug.Dump;
import system.time.TimeStamp;
import system.treatments.Surgery;
import testsuite.TestWithControllers;

public class DumpTest extends TestWithControllers {

	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testHospitalAdmin, getCampus(0));
	}
	
	@Test
	public void testWriteDumpHtml() {
		sessionController.login(testC1Nurse1, getCampus(0));
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Test");
		doctorController.prescribeSurgery(testC1Patient1.getPatientFile().getDiagnoses().get(0), "Test");
		doctorController.orderBloodAnalysis("Arm", 5);
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(new TimeStamp(2014,5,5,5,5));
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.registerSurgeryResult((Surgery)testC1Patient1.getPatientFile().getDiagnoses().get(0).getTreatments().get(0), "test", "test");
		Dump dump = new Dump(hospital);
		dump.writeDumpHtml("file");
	}
	
	@After
	public void deleteFile() {
		File file = new File("file.html");
		file.delete();
	}

}
