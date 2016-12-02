package system.controllers;

import java.util.List;

import system.controllers.DoctorController.DiagnosisController;
import system.patients.Diagnosis;
import system.treatments.Treatment;


/**
 * Deze superklasse dient voor het samennemen van commando klassen die een
 * operatie met een diagnose uitvoeren.
 * @Invar De Diagnosis mag niet null zijn
 * @Invar De DiagnosisController mag niet null zijn.
 * @author SWOP Team 10
 */
public abstract class DiagnosisCommand implements Command {
protected final Diagnosis diagnosis;
protected final DiagnosisController diagnosisController;
protected List<Treatment> treatmentList;
	/**
	 * @Pre diagnosisController\=null
	 * @Pre diagnosis\=null
	 * @param diagnosisController
	 * @param diagnosis
	 */
	protected DiagnosisCommand(DiagnosisController diagnosisController,
		Diagnosis diagnosis){
		if (diagnosisController==null) throw new NullPointerException("De DiagnosisController is null");
		if (diagnosis==null) throw new NullPointerException("De Diagnosis is null");
		this.diagnosis=diagnosis;
		this.diagnosisController=diagnosisController;
	}
}
