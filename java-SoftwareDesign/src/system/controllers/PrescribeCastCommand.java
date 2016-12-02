package system.controllers;

import system.controllers.DoctorController.PrescribeTreatmentController;
import system.patients.Diagnosis;
import system.scheduling.Priority;
import system.treatments.Cast;
/**
 *Deze commando klasse dient voor het voorschrijven van een soort Treatment, namelijk een Cast.
 */
class PrescribeCastCommand extends PrescribeTreatmentCommand{	
	/**
	 * Constructor voor PrescribeCastCommand.
	 * 
	 * @param prescribeTreatmentController
	 * 		Een instantie van PrescribeTreatmentController
	 * @param diagnosis
	 * 		Diagnose tot betrekking
	 * @param bodyPart
	 * 		Lichaamsdeel waar gips rond moet
	 * @param durationInDays
	 * 		Hoeveel dagen de gips gedrage moet worden
	 */
	PrescribeCastCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, String bodyPart, int durationInDays) {
		super(prescribeTreatmentController, new Cast(diagnosis, bodyPart, durationInDays));
		}
	
	/**
	 * Zelfde met priority
	 */
	PrescribeCastCommand(PrescribeTreatmentController prescribeTreatmentController,
			Diagnosis diagnosis, Priority priority, String bodyPart, int durationInDays) {
		super(prescribeTreatmentController, new Cast(diagnosis, priority, bodyPart, durationInDays));
		}
}
