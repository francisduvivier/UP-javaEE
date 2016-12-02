package system.controllers;

import java.util.ArrayList;

import system.controllers.DoctorController.DiagnosisController;
import system.patients.Diagnosis;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 *Deze commando klasse dient voor het goedkeuren van een diagnose door een tweede dokter.
 *Enkel het goedkeuren wordt hier behandeld, dus niet het afkeuren.
 */
public class ApproveDiagnosisCommand extends DiagnosisCommand {
	/**
	 * Constructor van ApproveDiagnosisCommand
	 * 
	 * @param diagnosisController
	 * 		An instance of DiagnosisController
	 * @param diagnosis
	 * 		Goed te keuren diagnose
	 */
	ApproveDiagnosisCommand(DiagnosisController diagnosisController, Diagnosis diagnosis) {
		super(diagnosisController,diagnosis);
	}
	
	/**
	 * Uitvoeren van goedkeuren diagnose.
	 */
	@Override
	public void execute() {
		diagnosisController.approveDiagnosis(this.diagnosis);
	}

	/**
	 * Ongedaan maken van eerder goedgekeurde diagnose.
	 */
	@Override
	public void undo() {
		this.treatmentList = new ArrayList<Treatment>();
		for (Treatment treatment : this.diagnosis.getTreatments())
			this.treatmentList.add(treatment);
		diagnosisController.unapproveDiagnosis(diagnosis);
	}

	/**
	 * Herdoen van goedkeuren diagnose.
	 */
	@Override
	public void redo() {
		diagnosisController.reapproveDiagnosis(diagnosis, this.treatmentList);
	}

	/**
	 * toString() methode van de klasse ApproveDiagnosisCommand. 
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Approve Diagnosis: "+diagnosis.toString();
	}

}
