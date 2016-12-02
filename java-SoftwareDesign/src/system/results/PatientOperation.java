package system.results;

import client.IOperation;
import system.patients.Patient;

/**
 * Deze interface stelt een actie die men op een patient kan uitvoeren, voor.
 * Met een actie wordt een medische test of een behandeling bedoeld.
 * 
 * @author SWOP Team 10
 */
public interface PatientOperation extends IOperation {
	/**
	 * Setter voor het resultaat van een PatientOperation
	 * 
	 * @param result 
	 * 	      Het resultaat dat toegekend moet worden aan de PatientOperation
	 */
	public void setResult(Result result);
	
	/**
	 * Getter voor het resultaat van een PatientOperation
	 * 
	 * @return result
	 *         Het resultaat van de PatientOperation
	 */
	public Result getResult();
	
	/**
	 * Methode die aangeeft of de PatientOperation een resultaat nodig heeft
	 * 
	 * @return true
	 *         Als de PatientOperation een resultaat nodig heeft
	 *         false
	 *         Als de PatientOperation geen resultaat nodig heeft
	 */
	public boolean needsResult();
	
	/**
	 * Methode die aangeeft of de PatientOperation afgelopen is met resultaat
	 * 
	 * @return true
	 * 		   Als de PatientOperation afgelopen is met resultaat
	 * 		   false
	 * 		   Als de PatientOperation niet afgelopen is of een resultaat nodig heeft
	 */
	public boolean isFinished();
	
	/**
	 * Methode die de patient teruggeeft op dewelke de PatientOperation van toepassing is
	 */
	public Patient getPatient();
}
