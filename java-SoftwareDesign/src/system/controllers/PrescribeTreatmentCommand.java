package system.controllers;

import system.controllers.DoctorController.PrescribeTreatmentController;
import system.treatments.Treatment;


/**
 * Deze commando klasse dient als superklasse van een commando klasse voor het
 * voorschrijven van een bepaalde Treatment.
 */
abstract class PrescribeTreatmentCommand implements Command {
	protected final PrescribeTreatmentController prescribeTreatmentController;
	private final Treatment treatment;
	
	/**
	 * Constructor van PrescribeTreatmentCommand.
	 * 
	 * @param prescribeTreatmentController
	 * 		Een instantie van PrescribeTreatmentController
	 */
	protected PrescribeTreatmentCommand(
			PrescribeTreatmentController prescribeTreatmentController,
			Treatment treatment) {
		this.prescribeTreatmentController = prescribeTreatmentController;
		this.treatment = treatment;
	}
	
	/**
	 * Uitvoeren voorschrijven treatment.
	 */
	@Override
	public void execute() {
		prescribeTreatmentController.prescribeTreatment(this.treatment);
	}

	/**
	 * Ongedaan maken reeds voorgeschreven treatment.
	 */
	@Override
	public void undo() {
		prescribeTreatmentController.unPrescribeTreatment(this.treatment);
	}

	/**
	 * Herdoen van voorschreven treatment die ongedaan gemaakt werd.
	 */
	@Override
	public void redo() {
		prescribeTreatmentController.rePrescribeTreatment(this.treatment);
	}
}
