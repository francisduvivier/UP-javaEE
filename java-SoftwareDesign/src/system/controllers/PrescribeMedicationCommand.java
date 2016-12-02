package system.controllers;

import java.util.List;

import system.controllers.DoctorController.PrescribeTreatmentController;
import system.patients.Diagnosis;
import system.scheduling.Priority;
import system.treatments.Medication;
import system.warehouse.MedicationItemType;

/**
 *Deze commando klasse dient voor het voorschrijven van een soort Treatment, namelijk Medication.
 */
class PrescribeMedicationCommand extends PrescribeTreatmentCommand {
	/**
	 * Constructor van PrescribeMedicationCommand
	 * 
	 * @param prescribeTreatmentController
	 * 		Een instantie van PrescribeTreatmentController
	 * @param diagnosis
	 * 		Diagnose tot betrekking
	 * @param description
	 * 		Beschrijving van de medicatie
	 * @param sensitive
	 * 		Gevoelig reactie op medicatie
	 * @param medicationItems
	 * 		Subtype van de medicatie
	 */
	PrescribeMedicationCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, String description, boolean sensitive, List<MedicationItemType> medicationItems) {
		super(prescribeTreatmentController, new Medication(diagnosis,description,sensitive, medicationItems));
		}
	/**
	 *	Zelfde met priority.
	 */
	PrescribeMedicationCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, Priority priority, String description, boolean sensitive, 
			List<MedicationItemType> medicationItems) {
		super(prescribeTreatmentController, new Medication(diagnosis, priority, description,sensitive, medicationItems));
		}
}
