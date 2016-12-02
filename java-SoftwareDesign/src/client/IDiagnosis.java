package client;

import annotations.SystemAPI;
import system.patients.Patient;

/**
 * Deze klasse stelt een diagnose van een patient voor.
 * 
 * Tijdens het maken van het systeem, merkte we dat we diagnose eigenlijk ook
 * beter met states hadden behandelt. Hierbij zou de state Approved, Denied,
 * Registerd, Cancelled, ... kunnen toegevoegd worden analoog aan Treatment,
 * MedicalTest, en Appointment. Dit is echter werk voor de volgende iteratie.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public interface IDiagnosis {
	/**
	 * Getter voor de patient waarop de diagnose betrekking heeft
	 * 
	 * @return patient
	 *         De patient waarop de diagnose betrekking heeft
	 */
	@SystemAPI
	Patient getPatient();

	/**
	 * Methode die aangeeft of de diagnose een tweede opinie vereist
	 * 
	 * @return needsApproval
	 *         true indien de diagnose een tweede opinie vereist
	 *         false indien de diagnose geen tweede opinie vereist 
	 */
	@SystemAPI
	boolean needsApproval();
}
