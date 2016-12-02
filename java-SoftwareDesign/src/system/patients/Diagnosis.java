package system.patients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.IDiagnosis;

import system.epidemic.EpidemicThreat;
import system.epidemic.LowEpidemicThreat;
import system.exceptions.IllegalOperationException;
import system.staff.Doctor;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een diagnose van een patient voor.
 * 
 * Tijdens het maken van het systeem, merkte we dat we diagnose eigenlijk ook
 * beter met states hadden behandelt. Hierbij zou de state Approved, Denied,
 * Registerd, Cancelled, ... kunnen toegevoegd worden analoog aan Treatment,
 * MedicalTest, en Appointment. Dit is echter werk voor de volgende iteratie.
 * 
 * @invar description != null & description != ""
 * @invar proposingDoctor != null
 * @invar patient != null
 * 
 * @author SWOP Team 10
 */
public class Diagnosis implements IDiagnosis {
	/**
	 * De beschrijving van de diagnose
	 */
	private final String description;
	/**
	 * De dokter die de diagnose opmaakt of voorstelt
	 */
	private final Doctor proposingDoctor; 
	/**
	 * De dokter die de diagnose moeten goedkeuren
	 */
	private Doctor approvingDoctor=null;
	/**
	 * De patient waarop de diagnose betrekking heeft
	 */
	private final Patient patient;
	/**
	 * Variabele die aangeeft of de diagnose goedgekeurd dient te worden
	 */
	private boolean needsApproval=false;
	/**
	 * De behandelingen die op basis van de diagnose aan de patient gegeven worden
	 */
	private final ArrayList<Treatment> treatmentList;
	
	/**
	 * Boolean om te zien of deze diagnose ongedaan is gemaakt
	 */
	private boolean isCancelled;
	
	private final EpidemicThreat threat;

	/**
	 * Constructor voor Diagnosis indien geen tweede opinie vereist is
	 * 
	 * @param doctor
	 *        De dokter die de diagnose opmaakt
	 * @param patient
	 *        De patient waarop de diagnose betrekking heeft
	 * @param description
	 *        De beschrijving van de diagnose
	 * @pre description != null && description != ""
	 * 		De beschrijving moet verschillend zijn van null en de lege string
	 * @post getDescription() == description
	 * @pre doctor != null
	 * @post getProposingDoctor() == doctor
	 * @pre patient != null
	 * @post getPatient() == patient
	 * @throws NullPointerException
	 *         Als de waarde van een van de drie parameters null of een lege string is
	 */
	public Diagnosis(Doctor doctor, Patient patient, String description) throws NullPointerException {
		this(doctor,patient,description,new LowEpidemicThreat());
	}

	/**
	 * Constructor voor Diagnosis indien een tweede opinie vereist is
	 * 
	 * @param doctor
	 *        De dokter die de diagnose voorstelt
	 * @param secondDoctor
	 *        De dokter die de diagnose moet goedkeuren
	 * @param patient
	 *        De patient waarop de diagnose betrekking heeft
	 * @param description
	 *        De beschrijving van de diagnose
	 * @post getDescription() == description
	 * @pre doctor != null
	 * @post getProposingDoctor() == doctor
	 * @pre secondDoctor != null
	 * @post getApprovingDoctor() == secondDoctor
	 * @pre patient != null
	 * @post getPatient() == patient
	 * @throws NullPointerException
	 *         Als de waarde van een van de vier parameters null of een lege string is
	 */
	public Diagnosis(Doctor doctor, Doctor secondDoctor, Patient patient,
			String description,EpidemicThreat threat) throws NullPointerException {
		this(doctor, patient, description, threat);
		if (secondDoctor == null)
			throw new NullPointerException("second doctor is null.");
		this.approvingDoctor = secondDoctor;
		this.needsApproval = true;
	}
	
	public Diagnosis(Doctor doctor, Doctor secondDoctor, Patient patient,
			String description) throws NullPointerException {
		this(doctor, patient, description);
		if (secondDoctor == null)
			throw new NullPointerException("second doctor is null.");
		this.approvingDoctor = secondDoctor;
		this.needsApproval = true;
	}
	
	public Diagnosis(Doctor doctor, Patient patient,
			String description,EpidemicThreat threat) throws NullPointerException {
		if (description == null)
			throw new NullPointerException("description is null.");
		if (description.equals(""))
			throw new IllegalArgumentException("description heeft nul tekens.");
		this.description = description;
		if (doctor == null)
			throw new NullPointerException("doctor is null.");
		this.proposingDoctor = doctor;
		if (patient == null)
			throw new NullPointerException("patient is null.");
		this.patient = patient;
		this.treatmentList = new ArrayList<Treatment>();
		this.threat = threat;
	}

	/**
	 * Methode om te zien of deze diagnose ongedaan is gemaakt
	 * @return 	true
	 * 			Diagnose is ongedaan gemaakt
	 * 			false
	 * 			Diagnose is niet ongedaan gemaakt
	 */
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	/**
	 * Methode om de diagnose aan het patientendossier van de patient toe te voegen
	 * 
	 * @throws IllegalOperationException
	 *         Als het niet mogelijk is om de diagnose aan het patientendossier van de patient toe te voegen.
	 *         Zie de methode canBeRegistered() voor meer uitleg.
	 */
	public void register() 
			throws IllegalOperationException {
		if (!this.canBeRegistered())
			throw new IllegalOperationException("This Diagnosis can not be registered");
		
		this.patient.getPatientFile().addDiagnosis(this);
		if (this.approvingDoctor != null)
			this.approvingDoctor.addSecondOpinionDiagnosis(this);
	}

	/**
	 * Methode om de diagnose uit het patientendossier van de patient te verwijderen
	 * @throws 	IllegalOperationException
	 * 			Als de diagnose niet ongedaan kan gemaakt worden.
	 */
	public void unregister() throws IllegalOperationException {
		if (!this.canBeUndone())
			throw new IllegalOperationException("This Diagnosis has finished treatments");
		if (!this.needsApproval() && this.approvingDoctor != null)
			throw new IllegalOperationException("This Diagnosis has been approved by a second doctor");
		
		this.patient.getPatientFile().removeDiagnosis(this);
		if (this.approvingDoctor != null)
			this.approvingDoctor.removeSecondOpinionDiagnosis(this);

		this.isCancelled = true;
	}
	
	/**
	 * Methode om een diagnose opnieuw toe te voegen.
	 */
	public void reregister() {		
		if (this.approvingDoctor != null)
			this.approvingDoctor.addSecondOpinionDiagnosis(this);
		 
		this.patient.getPatientFile().addDiagnosis(this);
		
		this.isCancelled = false;
	}

	/**
	 * Methode om de diagnose goed te keuren
	 * 
	 * @throws IllegalOperationException
	 * 		   Als de diagnose niet goed- of afgekeurd kan worden
	 */
	public void approve() throws IllegalOperationException{
		if (!this.canBeApprovedOrDenied())
			throw new IllegalOperationException("this diagnosis can not be approved!");
		
		this.approvingDoctor.removeSecondOpinionDiagnosis(this);
		
		this.needsApproval = false;
	}
	
	/**
	 * Methode om de diagnose te verwerpen
	 * en een nieuwe diagnose voor te stellen
	 * 
	 * @param newDescription
	 *        De beschrijving van de nieuwe diagnose
	 *        
	 * @throws 	IllegalOperationException
	 * 			Als de diagnose niet kan weerlegd worden.
	 */
	public void deny() throws IllegalOperationException {
		if (!this.canBeApprovedOrDenied())
			throw new IllegalOperationException("this diagnosis can not be denied!");
		
		this.approvingDoctor.removeSecondOpinionDiagnosis(this);
		this.patient.getPatientFile().removeDiagnosis(this);

		this.needsApproval = false;		
	}
	
	/**
	 * Methode om de diagnose te verwerpen
	 * en een nieuwe diagnose voor te stellen
	 * 
	 * @param newDescription
	 *        De beschrijving van de nieuwe diagnose
	 */
	public void undeny() {
		this.approvingDoctor.addSecondOpinionDiagnosis(this);
		this.patient.getPatientFile().addDiagnosis(this);
		
		this.needsApproval = true;		
	}
	
	public Diagnosis opposingDiagnosis(String newDescription) {
		return new Diagnosis(this.approvingDoctor, this.proposingDoctor, this.patient, newDescription);
	}
	
	/**
	 * Deze diagnose niet meer goedkeuren.
	 * 
	 * @throws 	IllegalOperationException
	 * 			Als de goedkeuring niet meer ongedaan kan worden gemaakt
	 */
	public void unapprove() throws IllegalOperationException {
		if (!this.canBeUndone())
			throw new IllegalOperationException("This Diagnosis has finished treatments");
		
		this.approvingDoctor.addSecondOpinionDiagnosis(this);
		
		this.needsApproval = true;
	}
	
	/**
	 * Deze diagnose opnieuw goedkeuren na het ongdedaan maken.
	 */
	public void reapprove() {
		this.approvingDoctor.removeSecondOpinionDiagnosis(this);
		
		this.needsApproval = false;
	}
	
	/**
	 * Getter voor de beschrijving van de diagnose
	 * 
	 * @return description
	 *         De beschrijving van de diagnose
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Getter voor de patient waarop de diagnose betrekking heeft
	 * 
	 * @return patient
	 *         De patient waarop de diagnose betrekking heeft
	 */
	public Patient getPatient() {
		return this.patient;
	}

	/**
	 * Methode die aangeeft of de diagnose een tweede opinie vereist
	 * 
	 * @return needsApproval
	 *         true indien de diagnose een tweede opinie vereist
	 *         false indien de diagnose geen tweede opinie vereist 
	 */
	public boolean needsApproval() {
		return this.needsApproval;
	}
	
	/**
	 * Methode die aangeeft of de diagnose een dokter heeft die de diagnose moet goedkeuren
	 * 
	 * @param doctor
	 *        De dokter die de diagnose moet goedkeuren
	 * @return true
	 *         Als de diagnose een dokter heeft die de diagnose moet goedkeuren
	 *         false
	 *         Als de diagnose geen dokter heeft die de diagnose moet goedkeuren
	 */
	public boolean hasAsApprovingDoctor(Doctor doctor) {
		return (this.approvingDoctor == doctor);
	}
	
	/**
	 * Een toString voor Diagnosis.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Diagnosis of " + this.getPatient() + " by "
				+ this.proposingDoctor;
	}

	/**
	 * Methode die alle behandelingen die bij de diagnose horen teruggeeft. 
	 * 
	 * @return treatmentList
	 *        De behandelingen horende bij de diagnose
	 */
	public List<Treatment> getTreatments() {
		return Collections.unmodifiableList(treatmentList);
	}

	/**
	 * Methode om een behandeling aan de lijst van behandelingen die bij de diagnose horen, toe te voegen.
	 * 
	 * @param treatment
	 *        De toe te voegen behandeling
	 * @throws IllegalArgumentException
	 *         Als de diagnose van de behandeling niet overeenkomt met deze diagnose
	 */
	public void addTreatment(Treatment treatment) throws IllegalArgumentException {
		if(treatment.getDiagnosis()!= this)
			throw new IllegalArgumentException("De diagnose van de treatment komt niet overeen met deze diagnose");
		this.treatmentList.add(treatment);
	}
	
	/**
	 * Methode om een behandeling uit de lijst van behandelingen die bij de diagnose horen, te verwijderen.
	 * 
	 * @param treatment
	 *        De te verwijderen behandeling
	 */
	public void removeTreatment(Treatment treatment){
		if (!treatmentList.contains(treatment))
			throw new IllegalArgumentException("Treatment zit niet in de treatment lijst.");
		this.treatmentList.remove(treatment);
	}
	
	/**
	 * Checkt of het mogelijk is om deze diagnosis te registeren/unregisteren. Dit moet gecheckt worden in de 
	 * Enter Diagnosis use case. 
	 * 
	 * @return	True wanneer de proposingDoctor een patient file heeft geopend, deze gelijk is aan de patient file
	 * 			van de patient en de patient niet ontslagen is.
	 * 			|	result == (proposingDoctor.getOpenPatientFile() != null &&
	 * 			|			   proposingDoctor.getOpenPatientFile().equals(getPatient().getPatientFile()) &&
	 * 			|			   getPatient().isDischarged())
	 */
	public boolean canBeRegistered() {
		return (this.proposingDoctor.getOpenPatientFile() != null &&
				this.proposingDoctor.getOpenPatientFile().equals(this.getPatient().getPatientFile()) &&
				!this.getPatient().isDischarged());

	}

	/**
	 * Checkt of het mogelijk is om deze diagnosis te approven/denyen. Dit moet gecheckt worden in de Approve Diagnosis
	 * use case.
	 * 
	 * @return	True wanneer de approvingDoctor een patient file heeft geopend, deze gelijk is aan de patient file 
	 * 			van de patient, de patient niet onstlagen is en de diagnose goedkeuring nodig heeft.
	 * 			|	result == (needsApproval() &&
	 * 			|			   approvingDoctor.getOpenPatientFile() != null &&
	 * 			|			   approvingDoctor.getOpenPatientFile().equals(getPatient().getPatientFile()) &&
	 * 			|			   getPatient().isDischarged())
	 */
	public boolean canBeApprovedOrDenied() {
		return 	(this.needsApproval() &&
				this.approvingDoctor.getOpenPatientFile() != null &&
				this.approvingDoctor.getOpenPatientFile().equals(this.getPatient().getPatientFile()) &&
				!this.getPatient().isDischarged()&&
				this.approvingDoctor.getSecondOpinionDiagnoses().contains(this));
	}
	
	/**
	 * Checkt of het mogelijk is om deze diagnose ongedaan te maken.
	 * 
	 * @return 	false
	 * 			Als er een treatment van de diagnose gedaan is
	 */
	public boolean canBeUndone() {
		for (Treatment treatment : this.treatmentList) 
			if (treatment.needsResult() || treatment.isFinished())
				return false;
		return true;
	}
	
	public EpidemicThreat getThreat() {
		return this.threat;
	}
}
