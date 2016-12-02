package system.controllers;

import java.util.ArrayList;

import system.controllers.DoctorController.DiagnosisController;
import system.patients.Diagnosis;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 * Deze commando klasse dient voor het weigeren van een diagnose door een tweede
 * dokter.
 */
class DenyDiagnosisCommand extends DiagnosisCommand{
	private final Diagnosis newDiagnosis;

	/**
	 * De constructor van DenyDiagnoseCommand.
	 * 
	 * @param 	diagnosisController
	 * 			De controller voor de diagnose
	 * @param 	diagnosis
	 * 			De diagnose om te ontkennen
	 * @param 	newDiagnosis
	 * 			De nieuwe diagnose
	 */
	DenyDiagnosisCommand(DiagnosisController diagnosisController,
			Diagnosis diagnosis, Diagnosis newDiagnosis) {
		super(diagnosisController,diagnosis);
		this.newDiagnosis = newDiagnosis;
	}

	/**
	 * Uitvoeren van herevaluatie diagnose: gegevens diagnose vervangen door nieuwe gegevens.
	 */
	@Override
	public void execute() {
		diagnosisController.denyDiagnosis(this.diagnosis);
		diagnosisController.enterDiagnosis(this.newDiagnosis);
	}

	/**
	 * Ongedaan maken reeds uitgevoerde herevaluaties diagnosissen.
	 */
	@Override
	public void undo() {
		this.treatmentList = new ArrayList<Treatment>();
		for (Treatment treatment : this.newDiagnosis.getTreatments())
			this.treatmentList.add(treatment);
		diagnosisController.unregisterDiagnosis(this.newDiagnosis);
		diagnosisController.unDenyDiagnosis(this.diagnosis);
	}

	/**
	 * Herdoen van herevaluatie diagnosissen.
	 */
	@Override
	public void redo() {
		diagnosisController.denyDiagnosis(this.diagnosis);
		diagnosisController.reregisterDiagnosis(this.newDiagnosis, this.treatmentList);
	}
	
	/**
	 * Een toString voor DenyDiagnosisCommand.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Deny Diagnosis: " + this.diagnosis.toString() + " | Opposing "+this.newDiagnosis.toString();
	}
	
}
