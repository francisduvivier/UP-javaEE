package system.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import system.medicaltests.MedicalTest;
import system.repositories.StaffType;
import system.scheduling.ScheduledItem;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van StaffMember
 * en stelt een verpleegster voor.
 * 
 * @author SWOP Team 10
 */
public class Nurse extends StaffMember {
	
	/**
	 * Constructor voor Nurse
	 * 
	 * @param name
	 *        De naam van de verpleegster
	 */
	public Nurse(String name) {
		super(name, StaffType.NURSE);
	}

	/**
	 * Een toString voor Nurse.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Nurse: " + super.toString();
	}

	/**
	 * Methode die de beeindigde behandelingen teruggeeft
	 * waarvoor de verpleegster een resultaat moet ingegeven
	 * 
	 * @return openTreatments
	 *         De beeindigde behandelingen zonder resultaat en waaraan de verpleegster toegekend is
	 */
	public List<Treatment> getOpenTreatments() {
		List<Treatment> openTreatments = new ArrayList<Treatment>();
		SortedSet<ScheduledItem<Treatment>> treatments = this.getSchedule()
				.getScheduleByClass(Treatment.class);

		for (ScheduledItem<Treatment> item : treatments)
			if (item.getScheduleEvent().needsResult())
				openTreatments.add(item.getScheduleEvent());

		return openTreatments;
	}
	
	/**
	 * Methode die de beeindigde medische tests teruggeeft
	 * waarvoor de verpleegster een resultaat moet ingegeven
	 * 
	 * @return openMedicalTests
	 *         De beeindigde medische tests zonder resultaat en waaraan de verpleegster toegekend is
	 */
	public List<MedicalTest> getOpenMedicalTests() {
		List<MedicalTest> openMedicalTests = new ArrayList<MedicalTest>();
		SortedSet<ScheduledItem<MedicalTest>> medicalTests = this.getSchedule()
				.getScheduleByClass(MedicalTest.class);

		for (ScheduledItem<MedicalTest> item : medicalTests)
			if (item.getScheduleEvent().needsResult())
				openMedicalTests.add(item.getScheduleEvent());

		return openMedicalTests;
	}
}
