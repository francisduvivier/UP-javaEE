package system.controllers;

import system.controllers.DoctorController.PrescribeTreatmentController;
import system.patients.Diagnosis;
import system.scheduling.Priority;
import system.treatments.Surgery;
/**
 *Deze commando klasse dient voor het voorschrijven van een soort Treatment, namelijk een Surgery.
 */
class PrescribeSurgeryCommand extends PrescribeTreatmentCommand{	
	/**
	 * Constructor van PrescribeSurgeryCommand.
	 * 
	 * @param prescribeTreatmentController
	 * 		Een instantie van PrescribeTreatmentController
	 * @param diagnosis
	 * 		Diagnose tot betrekking
	 * @param description
	 * 		Beschrijving van de operatie
	 */
	PrescribeSurgeryCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, String description) {
		super(prescribeTreatmentController, new Surgery(diagnosis, description));
		}
	
	/**
	 * Zelfde met Priority
	 */
	PrescribeSurgeryCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, Priority priority, String description) {
		super(prescribeTreatmentController, new Surgery(diagnosis, priority, description));
		}
}
