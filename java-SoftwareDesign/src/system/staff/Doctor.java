package system.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import system.patients.Diagnosis;
import system.patients.Patient.PatientFile;
import system.repositories.StaffType;
import annotations.SystemAPI;

/**
 * 
 * Deze klasse is een subklasse van StaffMember
 * en stelt een dokter voor.
 * 
 * @author SWOP Team 10
 */
public class Doctor extends StaffMember {
	/**
	 * Lijst van alle diagnoses waarvoor de dokter een tweede opinie moet geven 
	 */
	private List<Diagnosis> secondOpinionDiagnoses;
	/**
	 * Variabele die een object van de inner klasse PatientFile bijhoudt
	 */
	private PatientFile lastOpenedPatientFile;
	
	
	/**
	 * Constructor van Doctor.
	 * 
	 * @param name
	 *        De naam van de dokter
	 */
	public Doctor(String name) {
		super(name, StaffType.DOCTOR);
		this.secondOpinionDiagnoses = new ArrayList<Diagnosis>();
	}

	/**
	 * Getter voor de lijst van diagnoses waarvoor de dokter een tweede opinie moet geven
	 * @return secondOpinionDiagnoses
	 *         De lijst van diagnoses waarvoor de dokter een tweede opinie moet geven
	 */
	public List<Diagnosis> getSecondOpinionDiagnoses() {
		return Collections.unmodifiableList(secondOpinionDiagnoses);
	}

	/**
	 * Methode om aan te geven dat de dokter voor de opgegeven diagnose een tweede opinie moet geven 
	 * 
	 * @param diagnosis
	 *        De diagnose waarvoor de dokter een tweede opinie moet geven
	 * @throws NullPointerException
	 *         Als de opgegeven diagnose null is
	 */
	public void addSecondOpinionDiagnosis(Diagnosis diagnosis) throws NullPointerException {
		if (diagnosis == null)
			throw new NullPointerException("Diagnosis is null.");
		secondOpinionDiagnoses.add(diagnosis);
	}

	/**
	 * Methode om aan te geven dat de dokter voor de opgegeven diagnose een tweede opinie heeft gegeven
	 * 
	 * @param diagnosis
	 *        De diagnose waarvoor de dokter een tweede opinie heeft gegeven
	 * @throws NullPointerException
	 *         Als de opgegeven diagnose null is
	 * @throws IllegalArgumentException
	 *         Als de opgegeven diagnose niet meer bestaat  
	 */
	public void removeSecondOpinionDiagnosis(Diagnosis diagnosis) throws NullPointerException, IllegalArgumentException {
		if (diagnosis == null)
			throw new NullPointerException("Diagnosis is null.");
		if (!secondOpinionDiagnoses.remove(diagnosis))
			throw new IllegalArgumentException();
	}
	
	/**
	 * Een toString voor Doctor.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Doctor: " + super.getName();
	}
	
	/**
	 * Setter voor het laatst geopende patientendossier
	 * 
	 * @param patientFile
	 *        Het laatst geopende patientendossier
	 * @throws NullPointerException
	 *         Als het opgegevene patientendossier null is
	 */
	public void setLastOpenedPatientFile(PatientFile patientFile) throws NullPointerException {
		if (patientFile == null)
			throw new NullPointerException();

		this.lastOpenedPatientFile=patientFile;	
	}
	
	/**
	 * Getter voor het laatst geopende patientendossier
	 * 
	 * @return lastOpenedPatientFile
	 *         Als de dokter een patientendossier heeft geopend
	 *         null
	 *         Als de dokter nog geen patientendossier heeft geopend
	 *         null
	 *         Als de dokter geen patientendossier open heeft staan
	 */
	public PatientFile getOpenPatientFile() {
		if (lastOpenedPatientFile == null)
			return null;
		if(lastOpenedPatientFile.isClosed())
			return null;
		return lastOpenedPatientFile;
	}
}
