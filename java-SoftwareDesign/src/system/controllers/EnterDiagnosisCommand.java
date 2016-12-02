package system.controllers;

import java.util.ArrayList;

import system.controllers.DoctorController.DiagnosisController;
import system.epidemic.EpidemicThreat;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.staff.Doctor;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 * Deze commando klasse dient voor het invoeren van een nieuwe diagnose voor een
 * patient.
 */
class EnterDiagnosisCommand extends DiagnosisCommand {

	/**
	 * Constructor voor EnterDiagnosisCommand.
	 * 
	 * @param	diagnosisController
	 * 			Een instantie van DiagnosisController
	 * @param 	doctor
	 * 			Dokter die de diagnose nam
	 * @param 	patient
	 * 			Respectievelijke patient
	 * @param 	description
	 * 			Beschrijving van de diagnose
	 */
	EnterDiagnosisCommand(DiagnosisController diagnosisController,
			Doctor doctor, Patient patient, String description,EpidemicThreat threat) {
		super(diagnosisController,new Diagnosis(doctor, patient, description,threat));
	}

	/**
	 * Constructor voor EnterDiagnosisCommand wanneer tweede opinie
	 * tot betrekking is.
	 * 
	 * @param 	diagnosisController
	 * 			Een instantie van DiagnosisController
	 * @param 	doctor
	 * 			Dokter die de diagnose nam
	 * @param 	secondDoctor
	 * 			Dokter waaraan tweede opinie gevraagd wordt
	 * @param 	patient
	 * 			Respectievelijke patient
	 * @param 	description
	 * 			Beschrijving vna de diagnose
	 */
	EnterDiagnosisCommand(DiagnosisController diagnosisController,
			Doctor doctor, Doctor secondDoctor, Patient patient,
			String description, EpidemicThreat threat) {
		super(diagnosisController,new Diagnosis(doctor, secondDoctor, patient,
				description, threat));
	}

	/**
	 * Uitvoeren van registreren diagnose.
	 */
	@Override
	public void execute() {
		diagnosisController.enterDiagnosis(this.diagnosis);
	}

	/**
	 * Ongedaan maken reeds geregistreerde diagnose.
	 */
	@Override
	public void undo() {
		this.treatmentList = new ArrayList<Treatment>();
		for (Treatment treatment : this.diagnosis.getTreatments())
			this.treatmentList.add(treatment);
		diagnosisController.unregisterDiagnosis(this.diagnosis);
	}

	/**
	 * Herdoen van registratie die ongedaan is gemaakt.
	 */
	@Override
	public void redo() {
		diagnosisController.reregisterDiagnosis(this.diagnosis, this.treatmentList);
	}
	
	/**
	 * De toString methode voor de klasse EnterDiagnosisCommand.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Enter Diagnosis: " + diagnosis.toString();
	}
}
