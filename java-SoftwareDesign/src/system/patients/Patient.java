 package system.patients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import client.IPatient;

import system.medicaltests.MedicalTest;
import system.repositories.PatientType;
import system.repositories.ResourceType;
import system.results.Result;
import system.scheduling.Schedule;
import system.scheduling.ScheduleResource;
import system.time.TimeStamp;
import system.treatments.Treatment;
import system.util.Pair;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een patient voor.
 * 
 * @invar name != null && name != ""
 * 
 * @author SWOP Team 10
 */
public class Patient implements ScheduleResource, IPatient {
	/**
	 * De naam van de patient
	 */
	private final String name;
	/**
	 * Het type van de patient
	 */
	private PatientType patientType;
	/**
	 * Het patientendossier
	 */
	private final PatientFile patientFile;
	/**
	 * Het uurrooster van de patient
	 */
	private final Schedule schedule;
	
	private TimeStamp timeOfLastDischarge;

	/**
	 * Initialisatie van een nieuwe patient met een naam
	 * 
	 * @param name
	 *            De naam van de nieuwe patient
	 * @pre	name != null && name != ""
	 * 		De naam van de patient moet verschillend zijn van null of de lege string
	 * @post getName() == name
	 * @throws NullPointerException
	 *             Als de opgegeven naam null of een lege string is
	 */
	public Patient(String name) {
		this.schedule = new Schedule();
		if (name == null)
			throw new NullPointerException("Naam is null.");
		if (name.equals("")) {
			throw new IllegalArgumentException("Naam heeft nul tekens");
		}
		this.name = name;
		this.patientType = PatientType.STAYING_PATIENT;
		this.patientFile = new PatientFile();
		this.timeOfLastDischarge = null;
	}

	/**
	 * Getter voor de naam van de patient
	 * 
	 * @return name De naam van de patient
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter voor het patientendossier
	 * 
	 * @return patientFile Het patientendossier
	 */
	public PatientFile getPatientFile() {
		return patientFile;
	}

	/**
	 * Een toString voor Patient
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Patient: " + this.name;
	}

	/**
	 * Getter voor de resourceType van de patient
	 * 
	 * @return resourcetype van de patient
	 */
	@Override
	public ResourceType getResourceType() {
		return this.patientType;
	}

	/**
	 * Getter voor de schedule van de patient
	 */
	@Override
	public Schedule getSchedule() {
		return this.schedule;
	}

	/**
	 * Methode die aangeeft of de patient uit het ziekenhuis is ontslagen
	 * 
	 * @return true Als de patient uit het ziekenhuis is ontslagen false Als de
	 *         patient nog niet uit het ziekenhuis is ontslagen
	 */
	public boolean isDischarged() {
		if (this.patientType == PatientType.DISCHARGED_PATIENT)
			return true;
		else
			return false;
	}

	/**
	 * Methode die aangeeft of de patient uit het ziekenhuis ontslagen kan
	 * worden
	 * 
	 * @return true Als de patient uit het ziekenhuis ontslagen kan worden false
	 *         Als de patient niet uit het ziekenhuis ontslagen kan worden
	 */
	public boolean canBeDischarged() {
		for (Diagnosis diagnosis : getPatientFile().getDiagnoses()) {
			if (diagnosis.needsApproval())
				return false;
			for (Treatment treatment : diagnosis.getTreatments())
				if (!(treatment.isFinished() || treatment.needsResult()))
					return false;
		}
		for (MedicalTest medicalTest : patientFile.getMedicalTests())
			if (!(medicalTest.isFinished() || medicalTest.needsResult()))
				return false;

		return true;
	}

	/**
	 * Methode om aan te geven of de patient uit het ziekenhuis ontslagen kan
	 * worden
	 * @param timeOfDischarge TODO
	 */
	public void discharge(TimeStamp timeOfDischarge) {
		if (timeOfDischarge==null) 
			throw new NullPointerException("Time of discharge is null");
		this.patientType = PatientType.DISCHARGED_PATIENT;
		this.timeOfLastDischarge = timeOfDischarge;
	}
	
	public void register() {
		if (this.isDischarged()) 
			this.patientType = PatientType.STAYING_PATIENT;
	}
	
	public TimeStamp getTimeOfLastDischarge() {
		return this.timeOfLastDischarge;
	}
	
	/**
	 * Deze klasse stelt een patientendossier voor. De patientfile wordt nagekeken
	 * (is observable) volgens het Observer patroon. De observer van deze patientfile
	 * is niet gelinkt maar is in dit geval normaal de EpidemicHandler. Dit is een
	 * overkoepelende entiteit die zich bezighoudt met het tellen van diagnoses die
	 * als gelijk worden beschouwd.
	 * 
	 * @author SWOP Team 10
	 */
	public class PatientFile extends Observable {
		/**
		 * Variabele die aangeeft of het patientendossier gesloten is
		 */
		private boolean closed;
		/**
		 * De diagnoses horende bij de patient waarop het patientendossier
		 * betrekking heeft
		 * 
		 * @invar diagnoses != null
		 */
		private final List<Diagnosis> diagnoses;
		/**
		 * De medische tests horende bij de patient waarop het patientendossier
		 * betrekking heeft
		 */
		private final List<MedicalTest> medicalTests;

		/**
		 * Constructor voor PatientFile
		 */
		private PatientFile() {
			this.medicalTests = new ArrayList<MedicalTest>();
			this.diagnoses = new ArrayList<Diagnosis>();
		}

		/**
		 * Methode om een medische test aan het patientendossier toe te voegen
		 * 
		 * @param medicalTest
		 *            De toe te voegen medische test
		 */
		public void addMedicalTest(MedicalTest medicalTest) {
			medicalTests.add(medicalTest);
		}

		/**
		 * Methode om een medische test uit het patientendossier te verwijderen
		 * 
		 * @param medicalTest
		 *            De te verwijderen medische test
		 */
		public void removeMedicalTest(MedicalTest medicalTest) {
			medicalTests.remove(medicalTest);
		}

		/**
		 * Methode die aangeeft of het patientendossier gesloten is
		 * 
		 * @return closed true als het patientendossier gesloten is false als
		 *         het patientendossier open staat
		 */
		public boolean isClosed() {
			return closed;
		}

		/**
		 * Methode om het patientendossier te openen en te sluiten
		 * 
		 * @param closed
		 *            true om het patientendossier te sluiten false om het
		 *            patientendossier te openen
		 */
		public void setClosed(boolean closed) {
			this.closed = closed;
		}

		/**
		 * Methode die de resultaten van alle behandelingen en medische tests
		 * die uitgevoerd zijn op de patient waarop het patientendossier
		 * betrekking heeft, teruggeeft.
		 * 
		 * @return results 
		 *         De resultaten in het patientendossier
		 */
		public List<Result> getResults() {
			List<Result> results = new ArrayList<Result>();
			for (Diagnosis diagnosis : getDiagnoses())
				for (Treatment treatment : diagnosis.getTreatments())
					if (treatment.getResult() != null)
						results.add(treatment.getResult());

			for (MedicalTest medicalTest : getMedicalTests())
				if (medicalTest.getResult() != null)
					results.add(medicalTest.getResult());
			return results;
		}

		/**
		 * Getter voor alle diagnoses in het patientendossier
		 * 
		 * @return diagnoses 
		 *         De diagnoses in het patientendossier
		 */
		public List<Diagnosis> getDiagnoses() {
			return Collections.unmodifiableList(this.diagnoses);
		}

		/**
		 * Getter voor alle medische tests in het patientendossier
		 * 
		 * @return medicalTests
		 *         De medische tests in het patientendossierests
		 */
		public List<MedicalTest> getMedicalTests() {
			return Collections.unmodifiableList(this.medicalTests);
		}

		/**
		 * Methode om een diagnose aan het patientendossier toe te voegen
		 * 
		 * @param diagnosis
		 *        De toe te voegen diagnose
		 */
		void addDiagnosis(Diagnosis diagnosis) {
			this.diagnoses.add(diagnosis);
			this.setChanged();
			this.notifyObservers(new Pair<Diagnosis,ObserverAction>(diagnosis,ObserverAction.ADD));
		}

		/**
		 * Methode om een diagnose uit het patientendossier te verwijderen
		 * 
		 * @param diagnosis
		 *        De te verwijderen diagnose
		 */
		void removeDiagnosis(Diagnosis diagnosis) {
			this.diagnoses.remove(diagnosis);
			this.setChanged();
			this.notifyObservers(new Pair<Diagnosis,ObserverAction>(diagnosis,ObserverAction.REMOVE));
		}
	}
}
