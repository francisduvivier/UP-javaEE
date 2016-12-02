package testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import system.medicaltests.MedicalTest;
import system.patients.Patient;
import system.scheduling.Appointment;
import system.scheduling.Priority;
import system.scheduling.ScheduleEvent;
import system.staff.Doctor;
import system.time.TimePeriod;
import system.time.TimeStamp;



/**
 * De nurse registreert 50 patienten en plant deze allemaal bij 1 dokter. Er word gekeken of
 * de dokter 50 opeenvolgende appointments heeft.
 * 
 **/
public class Scenario2_scheduling extends TestWithControllers{
	Patient testPatients[];
	Doctor testDoctors[];
	TimeStamp supposedAppointmentStart;

	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testC1Nurse1, getCampus(0));
		sessionController.login(testC1WarehouseManager1, getCampus(0));
	}
	
	@Test 
	public void scenario2_1() {
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController.selectRegisteredPatient(testC1Patient1);
		// testC1Patient1 zal een afspraak hebben van 9 tot 9:30 op campus 1
		nurseController.scheduleAppointment(testC1Doctor1);
		TimePeriod equals1 = new TimePeriod(
				new TimeStamp(2011,10,8,9,0),
				new TimeStamp(2011,10,8,9,30));
		// testC2Doctor1 is een switcher en zal tegen het einde van de afspraak
		// van testC1Patient1 naar campus 1 komen en dus is de volgende afspraak van
		// 9:30 tot 10:00.
		nurseController.scheduleAppointment(testC2Doctor1);
		TimePeriod equals2 = new TimePeriod(
				new TimeStamp(2011,10,8,9,30),
				new TimeStamp(2011,10,8,10,0));
		// testC2Doctor2 is geen SWITCHER dus testC1Patient1 moet reizen naar campus 2
		// Dit kost hem 15 minuten. De volgende afspraak is van 10:15 tot 10:45.
		nurseController.scheduleAppointment(testC2Doctor2);
		TimePeriod equals3 = new TimePeriod(
				new TimeStamp(2011,10,8,10,15),
				new TimeStamp(2011,10,8,10,45));
		// Zelfde verhaal als hierboven (testC1Doctor2 is geen switcher),testC1Patient1 
		// moet terug naar campus 1 reizen. Afspraak van 11:00 tot 11:30.
		nurseController.scheduleAppointment(testC1Doctor2);
		TimePeriod equals4 = new TimePeriod(
				new TimeStamp(2011,10,8,11,0),
				new TimeStamp(2011,10,8,11,30));
		sessionController.logOut();
		
		sessionController.login(testC2Doctor1, getCampus(1));
		doctorController.consultPatientFile(testC1Patient1);
		List<Appointment> appointments = doctorController.getPatientAppointments();
		ScheduleEvent appointment1 = appointments.get(0),
					  appointment2 = appointments.get(1),
					  appointment3 = appointments.get(2),
					  appointment4 = appointments.get(3);
		assertEquals(appointment1.getScheduledPeriod(),equals1);
		assertEquals(appointment2.getScheduledPeriod(),equals2);
		assertEquals(appointment3.getScheduledPeriod(),equals3);
		assertEquals(appointment4.getScheduledPeriod(),equals4);
	}
	
	@Test
	public void scenario2_2() {
		Random rand = new Random();
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		for (int i = 0; i < 50; i++) {
			double chance = rand.nextDouble();
			// add some random priority scheduleEvents
			doctorController.orderBloodAnalysis((chance>0.3&&chance<0.6?Priority.URGENT:(chance<=0.3?Priority.NORMAL:Priority.NO_PRIORITY)),
					"test"+i,2);
		}
		boolean hadNormal = false;
		boolean illegal = false;
		sessionController.login(testC1Nurse1, getCampus(0));
		for (MedicalTest test : nurseController.getScheduledMedicalTests()) {
			if (hadNormal && test.toString().contains("URGENT"))
				illegal = true;
			if (test.toString().contains("NORMAL")) 
				hadNormal = true;
		}
		assertFalse(illegal);
	}
}
