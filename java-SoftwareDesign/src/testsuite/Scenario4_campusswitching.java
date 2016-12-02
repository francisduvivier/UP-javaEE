package testsuite;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.patients.Patient;
import system.scheduling.Appointment;
import system.time.TimeDuration;
import system.time.TimeStamp;

/**
 * Scenario 4 test het wisselen tussen campussen van zowel dokters als patienten.
 * Rekening houden met meerdere campussen per hospital is een uitbreiding van iteratie 3.
 * 
 * We testen in dit scenario 2 cases.
 * - In het eerste geval zal de dokter zich verplaatsen van campus 1 naar campus 2
 * om 12u. Zijn eerste shift vindt dus op de eerste campus plaats, en zijn tweede shift
 * op de tweede campus.
 * - In het tweede case heeft de dokter slechts één voorkeur, namelijk blijven één shift
 * op campus 1. De patients die geregistreerd zijn op campus 2 zullen dus naar campus 1
 * moeten gaan.
 * 
 * Voor een meer actievere 'switchende' dokter verwijzen we naar scenario 3
 * waar dit meer uitbundig getest wordt.
 **/

public class Scenario4_campusswitching extends TestWithControllers {

	private Patient testC1Patient2, testC1Patient3, testC1Patient4, testC1Patient5, testC1Patient6, testC1Patient7, testC1Patient8,
					testC1Patient9, testC1Patient10, testC1Patient11, testC1Patient12, testC1Patient13, testC1Patient14,
					testC2Patient2, testC2Patient3, testC2Patient4, testC2Patient5, testC2Patient6, testC2Patient7, testC2Patient8,
					testC2Patient9, testC2Patient10, testC2Patient11, testC2Patient12, testC2Patient13, testC2Patient14;
	private TimeStamp supposedScheduleItemStart;
	
	@Before
	public void testInit() {
		super.testInit();		
	}

	/**
	 * In dit scenario is er een dokter die als voorkeur heeft dat hij de eerste
	 * helft van de dag of campus1 zit en de 2de helft van de dag op campus 2
	 * hier wordt er van de 15 minuten enkels iets gemerkt wanneer de dokter zich naar de andere campus verplaatst
	 */
	@Test
	public void scenario4_1() {
		registerPatients();
		
		List<Appointment> sortedAppointmentsC1 = scheduleAppointmentsForCampus1();
		List<Appointment> sortedAppointmentsC2 = scheduleAppointmentsForCampus2();
		
		supposedScheduleItemStart=sessionController.getTime();
		supposedScheduleItemStart = TimeStamp.addedToTimeStamp(
				TimeDuration.hours(1), supposedScheduleItemStart);
		
		// De eerste 6 patienten hebben een afspraak van 30 min op campus 1 (shift 1 is van 9u-12u op campus 1),
		// de laatste 2 patienten hebben een afspraak van 30 min op campus 2 (shift 2 is van 12u-17u op campus 2).
		// De voorlaatste (zevende) patient zijn afspraak vindt plaats om 12u15 (kwartier reistijd).
		for (Appointment appointment: sortedAppointmentsC1) {
			assertEquals(supposedScheduleItemStart,appointment.getScheduledPeriod().getBegin());
			if (sortedAppointmentsC1.indexOf(appointment) == 5) // KWARTIER REISTIJD OM TWAALF UUR!
				supposedScheduleItemStart=TimeStamp.addedToTimeStamp(TimeDuration.minutes(15), supposedScheduleItemStart);
			supposedScheduleItemStart=TimeStamp.addedToTimeStamp(TimeDuration.minutes(30), supposedScheduleItemStart);
		}
		
		// De 4 afspraken gaan verder op de vorige afspraken van campus 2.
		// De eerste van de vier vindt plaats om 13u15, net na de vorige afspraak
		for (Appointment appointment: sortedAppointmentsC2) {
			assertEquals(supposedScheduleItemStart,appointment.getScheduledPeriod().getBegin());
			supposedScheduleItemStart=TimeStamp.addedToTimeStamp(TimeDuration.minutes(30), supposedScheduleItemStart);
		}
		
	}
		
	/**
	 * In dit scenario is er een dokter die als voorkeur heeft dat hij op campus
	 * 1 blijft. Dit is zo bij de overgeerfde doktor testC1Doctor1 van
	 * TestWithControllers. Hier heeft de 15 minuten reistijd geen effect omdat
	 * de patienten deze moeten doen en een appointment max ten vroegste een uur
	 * later gescheduled worden. Deze dokter zal niet wijken van campus 1.
	 */
	@Test
	public void scenario4_2() {
		registerPatients_2();
		sessionController.login(testC1Doctor3, getCampus(0)); // deze doctor heeft shift 9u-17u op campus 1
		List<Appointment> sortedAppointmentsC1 = scheduleAppointmentsForCampus1_2();
		List<Appointment> sortedAppointmentsC2 = scheduleAppointmentsForCampus2_2();
		
		supposedScheduleItemStart=sessionController.getTime();
		supposedScheduleItemStart = TimeStamp.addedToTimeStamp(TimeDuration.hours(1), sessionController.getTime());
		
		// De afspraken van sortedAppointmentsC1 vinden plaats tot 16u
		for (Appointment appointment: sortedAppointmentsC1) {
			assertEquals(supposedScheduleItemStart,appointment.getScheduledPeriod().getBegin());
			supposedScheduleItemStart=TimeStamp.addedToTimeStamp(TimeDuration.minutes(30), supposedScheduleItemStart);
		}
		
		// De eerste twee afspraken van sortedAppointmentsC2 sluiten aan
		// de overige afspraken beginnen de volgende dag op campus 1 om 9u
		for (Appointment appointment: sortedAppointmentsC2) {
			if(supposedScheduleItemStart.get(Calendar.HOUR_OF_DAY) == 17) { // einde van de dag bereikt -> verzetten tijd
				supposedScheduleItemStart=sessionController.getTime();
				supposedScheduleItemStart = TimeStamp.addedToTimeStamp(TimeDuration.days(1), sessionController.getTime());
				supposedScheduleItemStart = TimeStamp.addedToTimeStamp(TimeDuration.hours(1), supposedScheduleItemStart);
			}
			
			assertEquals(supposedScheduleItemStart,appointment.getScheduledPeriod().getBegin());
			supposedScheduleItemStart=TimeStamp.addedToTimeStamp(TimeDuration.minutes(30), supposedScheduleItemStart);
		}
		
	}
	
	/**
	 * Plant afspraken voor 8 campus 1 patienten.
	 * De afspraken worden in een lijst gestopt.
	 * 
	 * @return Lijst met afspraken van alle patienten
	 */
	private List<Appointment> scheduleAppointmentsForCampus1() {
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(testC1Patient1); patients.add(testC1Patient2);
		patients.add(testC1Patient3); patients.add(testC1Patient4);
		patients.add(testC1Patient5); patients.add(testC1Patient6);
		patients.add(testC1Patient7); patients.add(testC1Patient8);
		
		List<Appointment> sortedAppointments = new ArrayList<Appointment>();
		for (Patient patient:patients) {
			sessionController.login(testC1Nurse1, getCampus(0));
			nurseController.selectRegisteredPatient(patient);
			nurseController.scheduleAppointment(testC1Doctor2);
			sessionController.login(testC1Doctor2, getCampus(0));
			doctorController.consultPatientFile(patient);
			sortedAppointments.addAll(doctorController.getPatientAppointments());
		}
		
		return sortedAppointments;
	}
	
	/**
	 * Plant afspraken voor 4 campus 2 patienten.
	 * De afspraken worden in een lijst gestopt.
	 * 
	 * @return Lijst met afspraken van alle patienten
	 */
	private List<Appointment> scheduleAppointmentsForCampus2() {
		
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(testC2Patient1); patients.add(testC2Patient2);
		patients.add(testC2Patient3); patients.add(testC2Patient4);
		
		List<Appointment> sortedAppointments = new ArrayList<Appointment>();
		for (Patient patient:patients) {
			sessionController.login(testC2Nurse1, getCampus(1));
			nurseController.selectRegisteredPatient(patient);
			nurseController.scheduleAppointment(testC1Doctor2);
			sessionController.login(testC1Doctor2, getCampus(0));
			doctorController.consultPatientFile(patient);
			sortedAppointments.addAll(doctorController.getPatientAppointments());
		}
		
		return sortedAppointments;
	}

	/**
	 * Plant afspraken voor 8 campus 1 patienten.
	 * De afspraken worden in een lijst gestopt.
	 * 
	 * @return Lijst met afspraken van alle patienten
	 */
	private List<Appointment> scheduleAppointmentsForCampus1_2() {
		
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(testC1Patient1); patients.add(testC1Patient2);
		patients.add(testC1Patient3); patients.add(testC1Patient4);
		patients.add(testC1Patient5); patients.add(testC1Patient6);
		patients.add(testC1Patient7); patients.add(testC1Patient8);
		patients.add(testC1Patient9); patients.add(testC1Patient10);
		patients.add(testC1Patient11); patients.add(testC1Patient12);
		patients.add(testC1Patient13); patients.add(testC1Patient14);
		
		List<Appointment> sortedAppointments = new ArrayList<Appointment>();
		for (Patient patient:patients) {
			sessionController.login(testC1Nurse1, getCampus(0));
			nurseController.selectRegisteredPatient(patient);
			nurseController.scheduleAppointment(testC1Doctor3);
			sessionController.login(testC1Doctor3, getCampus(0));
			doctorController.consultPatientFile(patient);
			sortedAppointments.addAll(doctorController.getPatientAppointments());
		}
		
		return sortedAppointments;
	}
	
	/**
	 * Plant afspraken voor 4 campus 2 patienten.
	 * De afspraken worden in een lijst gestopt.
	 * 
	 * @return Lijst met afspraken van alle patienten
	 */
	private List<Appointment> scheduleAppointmentsForCampus2_2() {
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(testC2Patient1); patients.add(testC2Patient2);
		patients.add(testC2Patient3); patients.add(testC2Patient4);
		patients.add(testC2Patient5); patients.add(testC2Patient6);
		patients.add(testC2Patient7); patients.add(testC2Patient8);
		patients.add(testC2Patient9); patients.add(testC2Patient10);
		patients.add(testC2Patient11); patients.add(testC2Patient12);
		patients.add(testC2Patient13); patients.add(testC2Patient14);
		
		List<Appointment> sortedAppointments = new ArrayList<Appointment>();
		
		for (Patient patient:patients) {
			sessionController.login(testC2Nurse1, getCampus(1));
			nurseController.selectRegisteredPatient(patient);
			nurseController.scheduleAppointment(testC1Doctor3);
			sessionController.login(testC1Doctor3, getCampus(1));
			doctorController.consultPatientFile(patient);
			sortedAppointments.addAll(doctorController.getPatientAppointments());
		}
		
		return sortedAppointments;
	}

	/**
	 * Registreert 7 extra patienten op campus 1 en
	 * registreert 3 extra patienten op campus 2
	 */
	public void registerPatients() {
		sessionController.login(testC1Nurse1, getCampus(0));
		testC1Patient2 = nurseController.registerNewPatient("Campus 1 patient 2");
		testC1Patient3 = nurseController.registerNewPatient("Campus 1 patient 3");
		testC1Patient4 = nurseController.registerNewPatient("Campus 1 patient 4");
		testC1Patient5 = nurseController.registerNewPatient("Campus 1 patient 5");
		testC1Patient6 = nurseController.registerNewPatient("Campus 1 patient 6");
		testC1Patient7 = nurseController.registerNewPatient("Campus 1 patient 7");
		testC1Patient8 = nurseController.registerNewPatient("Campus 1 patient 8");
		sessionController.login(testC2Nurse1, getCampus(1));
		testC2Patient2 = nurseController.registerNewPatient("Campus 2 patient 2");
		testC2Patient3 = nurseController.registerNewPatient("Campus 2 patient 3");
		testC2Patient4 = nurseController.registerNewPatient("Campus 2 patient 4");
	}
	
	/**
	 * Registreert 13 extra patienten op campus 1 en
	 * registreert 13 extra patienten op campus 2
	 */
	public void registerPatients_2() {
		sessionController.login(testC1Nurse1, getCampus(0));
		testC1Patient2 = nurseController.registerNewPatient("Campus 1 patient 2");
		testC1Patient3 = nurseController.registerNewPatient("Campus 1 patient 3");
		testC1Patient4 = nurseController.registerNewPatient("Campus 1 patient 4");
		testC1Patient5 = nurseController.registerNewPatient("Campus 1 patient 5");
		testC1Patient6 = nurseController.registerNewPatient("Campus 1 patient 6");
		testC1Patient7 = nurseController.registerNewPatient("Campus 1 patient 7");
		testC1Patient8 = nurseController.registerNewPatient("Campus 1 patient 8");
		testC1Patient9 = nurseController.registerNewPatient("Campus 1 patient 9");
		testC1Patient10 = nurseController.registerNewPatient("Campus 1 patient 10");
		testC1Patient11 = nurseController.registerNewPatient("Campus 1 patient 11");
		testC1Patient12 = nurseController.registerNewPatient("Campus 1 patient 12");
		testC1Patient13 = nurseController.registerNewPatient("Campus 1 patient 13");
		testC1Patient14 = nurseController.registerNewPatient("Campus 1 patient 14");
		sessionController.login(testC2Nurse1, getCampus(1));
		testC2Patient2 = nurseController.registerNewPatient("Campus 2 patient 2");
		testC2Patient3 = nurseController.registerNewPatient("Campus 2 patient 3");
		testC2Patient4 = nurseController.registerNewPatient("Campus 2 patient 4");
		testC2Patient5 = nurseController.registerNewPatient("Campus 2 patient 5");
		testC2Patient6 = nurseController.registerNewPatient("Campus 2 patient 6");
		testC2Patient7 = nurseController.registerNewPatient("Campus 2 patient 7");
		testC2Patient8 = nurseController.registerNewPatient("Campus 2 patient 8");
		testC2Patient9 = nurseController.registerNewPatient("Campus 2 patient 9");
		testC2Patient10 = nurseController.registerNewPatient("Campus 2 patient 10");
		testC2Patient11 = nurseController.registerNewPatient("Campus 2 patient 11");
		testC2Patient12 = nurseController.registerNewPatient("Campus 2 patient 12");
		testC2Patient13 = nurseController.registerNewPatient("Campus 2 patient 13");
		testC2Patient14 = nurseController.registerNewPatient("Campus 2 patient 14");
	}
	
}
