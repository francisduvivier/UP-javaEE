package system.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.IOperation;
import client.IPatient;
import client.IStaffMember;

import system.campus.Campus;
import system.exceptions.IllegalAccessException;
import system.exceptions.IllegalOperationException;
import system.exceptions.InsufficientWarehouseItemsException;
import system.medicaltests.MedicalTest;
import system.patients.Patient;
import system.repositories.PatientRepository;
import system.repositories.PatientType;
import system.repositories.StaffType;
import system.results.PatientOperation;
import system.results.Result;
import system.results.medicaltestresults.BloodAnalysisResult;
import system.results.medicaltestresults.ScanMatter;
import system.results.medicaltestresults.UltrasoundScanResult;
import system.results.medicaltestresults.XRayScanResult;
import system.results.treatmentresults.CastResult;
import system.results.treatmentresults.MedicationResult;
import system.results.treatmentresults.SurgeryResult;
import system.scheduling.Appointment;
import system.scheduling.ScheduledItem;
import system.staff.Doctor;
import system.staff.Nurse;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.treatments.Treatment;
import system.warehouse.stock.condition.HasEnoughFoodCondition;
import annotations.SystemAPI;

/**
 * Deze klasse is een controller voor het controlleren van operaties die
 * uitgevoerd mogen worden door een ziekenhuisadministrator. In de huidige
 * versie van het programma kan een nurse patienten en resultaten van een
 * PatientOperation registeren.
 * @Invar de SessionController is niet null
 */
@SystemAPI
public class NurseController extends StaffController{

	private Patient selectedPatient;

	/**
	 * Constructor voor een NurseController.
	 * 
	 * @param sessionController
	 *            De sessionController van de sessie waarin de NurseController
	 *            ook gebruikt wordt.
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public NurseController(SessionController sessionController) {
		super(sessionController);
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		this.selectedPatient = null;
	}

	/**
	 * Registreren van BloodAnalysis Result. Maakt de BloodAnalysisResult aan
	 * met de opgegeven eigenschappen en registreert die dan.
	 * 
	 * @param bloodAnalysis
	 * @param amountOfBloodWithdrawn
	 * @param redCellCount
	 * @param whiteCellCount
	 * @param plateletCount
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public BloodAnalysisResult registerBloodAnalysisResult(IOperation bloodAnalysis,
			int amountOfBloodWithdrawn, int redCellCount, int whiteCellCount,
			int plateletCount) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		BloodAnalysisResult result = new BloodAnalysisResult(
				amountOfBloodWithdrawn, redCellCount, whiteCellCount,
				plateletCount);
		this.registerResult(bloodAnalysis, result);
		return result;
	}

	/**
	 * Registreren van Ultrasoundscan Result.Maakt de UltrasoundscanResult aan
	 * met de opgegeven eigenschappen en registreert die dan.
	 * 
	 * @param ultrasoundScan
	 * @param scanInformation
	 * @param scanMatter
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public UltrasoundScanResult registerUltrasoundScanResult(IOperation ultrasoundScan,
			String scanInformation, ScanMatter scanMatter) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		UltrasoundScanResult result = new UltrasoundScanResult(scanInformation,
				scanMatter);
		this.registerResult(ultrasoundScan, result);
		return result;
	}

	/**
	 * Registreren van XRayScan Result. Maakt de XrayScanResult aan met de
	 * opgegeven eigenschappen en registreert die dan.
	 * 
	 * @param xRayScan
	 * @param abnormalities
	 * @param nbImagesTaken
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public XRayScanResult registerXRayScanResult(IOperation xRayScan, String abnormalities,
			int nbImagesTaken) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		XRayScanResult result = new XRayScanResult(abnormalities, nbImagesTaken);
		this.registerResult(xRayScan, result);
		return result;
	}

	/**
	 * Registreren van Medication Result.Maakt een MedicationResult aan met de
	 * opgegeven eigenschappen en registreert die dan.
	 * 
	 * @param medication
	 * @param abnormalReaction
	 * @param report
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public MedicationResult registerMedicationResult(IOperation medication,
			boolean abnormalReaction, String report) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		MedicationResult result = new MedicationResult(report, abnormalReaction);
		this.registerResult(medication, result);
		return result;
	}

	/**
	 * Registreren van Cast Result.Maakt een CastResult aan met de opgegeven
	 * eigenschappen en registreert die dan.
	 * 
	 * @param cast
	 * @param report
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public CastResult registerCastResult(IOperation cast, String report) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		CastResult result = new CastResult(report);
		this.registerResult(cast, result);
		return result;
	}

	/**
	 * Registreren van Surgery Result.Maakt een SurgeryResult aan met de
	 * opgegeven eigenschappen en registreert die dan.
	 * 
	 * 
	 * @param surgery
	 * @param report
	 * @param specialAftercare
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public SurgeryResult registerSurgeryResult(IOperation surgery, String report,
			String specialAftercare) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		SurgeryResult result = new SurgeryResult(report, specialAftercare);
		this.registerResult(surgery, result);
		return result;
	}

	/**
	 * Dit is de implementatie van het registregen van een Result. Registreren
	 * betekent dat een Result word gebonden aan een PatientOperation zodat er
	 * vanuit de PatientOperation kan achterhaald worden wat het Result ervan
	 * is.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	private void registerResult(IOperation evaluateItem, Result result) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		if (evaluateItem == null)
			throw new NullPointerException("Evaluatable is null.");
		if (!getBigHospital().getAllResources(PatientType.STAYING_PATIENT).
				contains(((PatientOperation) evaluateItem).getPatient()))
			throw new IllegalOperationException(
					"Patient staat niet in de patient repository");

		((PatientOperation) evaluateItem).setResult(result);
	}

	/**
	 * Geeft een lijst van de ingelogde nurse haar Treatments waarvoor nog geen
	 * resultaten zijn.
	 * 
	 * @param nurse
	 * @return onafgewerkte treatment lijst
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<Treatment> getUnfinishedTreatments() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		Nurse nurse = (Nurse) sessionController.getCurrentUser();
		return Collections.unmodifiableList(nurse.getOpenTreatments());
	}

	/**
	 * Geeft een lijst van de ingelogde nurse haar MedicalTests waarvoor nog
	 * geen resultaten zijn.
	 * 
	 * @param nurse
	 * @return onafgewerkte treatment lijst
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<MedicalTest> getUnfinishedMedicalTests() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		Nurse nurse = (Nurse) sessionController.getCurrentUser();
		return Collections.unmodifiableList(nurse.getOpenMedicalTests());
	}

	/**
	 * @return De PatientRepository die bij de getBigHospital() hoort.
	 */
	private PatientRepository getPatientRepository() {
		return getCurrentCampus().getPatientRepository();
	}

	/**
	 * @return Een lijst van de geregistreerde patienten in de Hospital
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<Patient> getRegisteredPatients() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		return getPatientRepository().getRegisteredPatients();
	}
	
	/**
	 * voegt een patient toe aan de patientRepository. De patient wordt ook
	 * geregistreed op de campus waar de nurse zich bevindt.
	 * 
	 * @param name
	 */
	@SystemAPI
	public Patient registerNewPatient(String name) {
		registerNewPatientExceptionThrower(name, getCurrentCampus());
		Patient patient = new Patient(name);
		this.getPatientRepository().addPatient(patient);
		selectedPatient = patient;
		return patient;
	}
	/**
	 * Checks whether a patient can be registered on a given campus
	 * @param 	name
	 * 			Naam van de patient
	 * @param 	campus
	 * 			De betreffende campus
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	NullPointerException
	 * 			Als de naam null is of de lege string
	 * @throws	IllegalArgumentException
	 * 			Als er al een patient is met dezelfde naam
	 * @throws	InsufficientWarehouseItemsException
	 * 			Als er te weinig eten is in het warenhuis
	 */
	@SystemAPI
	public void registerNewPatientExceptionThrower(String name, Campus campus){
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		if (name.equals("") || name == null)
			throw new NullPointerException("Name is null.");
		if (duplicatePatientName(name))
			throw new IllegalArgumentException(
					"Naam staat al in patient repository.");
		if (!campus.getWarehouse().getStockList().conditionIsTrue(new HasEnoughFoodCondition()))
			throw new InsufficientWarehouseItemsException("Not enough food.");
	}
	/**
	 * Kijk of een gegeven naam al voorkomt voor een Patient in de Hospital.
	 * 
	 * @param 	name
	 *          De op te zoeken naam
	 * @return 	True als de naam al voorkomt voor een patient in dit Hospital,
	 *         	anders false.
	 *         
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public boolean duplicatePatientName(String name) {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		for (Patient patient : getPatientRepository().getRegisteredPatients()) {
			if (name.equals(patient.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Geselecteerde patient aanmelden in het ziekenhuis. Deze methode kan enkel
	 * opgeroepen worden voor een patient die al ooit toegevoegd is en momenteel
	 * ingesteld is als ontlagen.
	 * 
	 * @param patient
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	NullPointerException
	 * 			Als de patient null is
	 * @throws	IllegalOperationException
	 * 			Opgegeven patient staat niet in lijst van geregistreerde patienten
	 * @throws	IllegalArgumentException
	 * 			Als er al een patient is met dezelfde naam
	 * @throws	InsufficientWarehouseItemsException
	 * 			Als er te weinig eten is in het warenhuis
	 */
	@SystemAPI
	public void registerPatient(IPatient patient) throws NullPointerException,
			IllegalOperationException, InsufficientWarehouseItemsException, IllegalArgumentException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		if (patient == null)
			throw new NullPointerException("Patient is null.");
		
		selectedPatient = (Patient) patient;
		if (!getPatientRepository().getRegisteredPatients().contains(selectedPatient))
			throw new IllegalOperationException(
					"Patient doesn't appear in the patient repository.");
		if (!selectedPatient.isDischarged())
			throw new IllegalArgumentException(
					"De opgegeven patient is niet ontslaan");
		if (!getCurrentCampus().getWarehouse().getStockList().conditionIsTrue(new HasEnoughFoodCondition()))
			throw new InsufficientWarehouseItemsException("Not enough food.");

		getPatientRepository().addPatient(selectedPatient);
	}

	/**
	 * Methode om te verzekeren dat de ingelogde gebruiker
	 * een nurse is.
	 * 
	 * @return 	true als de user access in de sessie controller nog klopt.
	 * 			|	result = sessionController.hasValidAccess(StaffType.NURSE)
	 */
	@Override
	protected boolean hasValidAccess() {
		return sessionController.hasValidAccess(StaffType.NURSE);
	}

	/**
	 * @return Een lijst van de dokters van deja Hospital.
	 */
	@SystemAPI
	public List<IStaffMember> getDoctors() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		List<IStaffMember> doctors = new ArrayList<IStaffMember>();
		doctors.addAll(this.getBigHospital().getStaffRepository().getStaffMembers(StaffType.DOCTOR));
		
		return Collections.unmodifiableList(doctors);
	}

	/**
	 * Plan een voor de selectedPatient met een gegeven Dokter.
	 * 
	 * @param	doctor
	 * 			De dokter met een afspraak gepland wordt
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	NullPointerException
	 * 			Als er geen geselecteerde patient is
	 */
	@SystemAPI
	public void scheduleAppointment(IStaffMember doctor) throws NullPointerException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		if (this.selectedPatient == null)
			throw new NullPointerException();

		Appointment appointment = new Appointment(this.selectedPatient, (Doctor) doctor);

		this.getBigHospital().getScheduler()
				.schedule(
						appointment,
						TimeStamp.addedToTimeStamp(
								TimeDuration.hours(1),
								getBigHospital().getHospitalTime().getTime()));

	}

	/**
	 * Deze methode dient voor het selecteren van een patient die al
	 * geregistreerd is. Op deze manier is het mogelijk om een appointment te
	 * maken voor een patient die al aanwezig is in het ziekenhuis.
	 * 
	 * @pre De patient is geregistreerd (en dus aanwezig) in het ziekenhuis
	 * @param patient
	 * 
	 * @throws	IllegalArgumentException
	 * 			Als de patient niet in het ziekenhuis aanwezig is
	 */
	@SystemAPI
	public void selectRegisteredPatient(IPatient patient) {
		if (getPatientRepository().getNonDischargedPatients().contains(patient))
			selectedPatient = (Patient)patient;
		else
			throw new IllegalArgumentException(
					"De patient moet aanwezig zijn in het ziekenhuis");
	}

	/**
	 * Geeft alle medische testen die ooit op de planning van de actieve nurse
	 * hebben gestaan terug, in volgorde van uitvoering.
	 * @return	een List<MedicalTest> met alle MedicalTest objecten van de
	 * 			Schedule van de Nurse
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<MedicalTest> getScheduledMedicalTests() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		Nurse nurse = (Nurse) sessionController.getCurrentUser();

		List<MedicalTest> medicalTests = new ArrayList<MedicalTest>();
		
		for (ScheduledItem<MedicalTest> item : nurse.getSchedule().getScheduleByClass(MedicalTest.class))
			medicalTests.add(item.getScheduleEvent());
		
		return medicalTests;
	}
	
	/**
	 * Geeft alle behandelingen die ooit op de planning van de actieve nurse
	 * hebben gestaan terug, in volgorde van uitvoering.
	 * @return	een List<Treatment> met alle Treatment objecten van de
	 * 			Schedule van de Nurse
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<Treatment> getScheduledTreatments() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		Nurse nurse = (Nurse) sessionController.getCurrentUser();

		List<Treatment> treatments = new ArrayList<Treatment>();
		
		for (ScheduledItem<Treatment> item : nurse.getSchedule().getScheduleByClass(Treatment.class))
			treatments.add(item.getScheduleEvent());
		
		return treatments;
	}
	/**
	 * Deze methode geeft alle campussen waarop het mogelijk is om een patient
	 * te registreren met de opgegeven naam.
	 * 
	 * @param name
	 *            de naam van de patient waarvoor men wilt nakijken waar hij
	 *            geregistreerd kan worden.
	 * @return De mogelijke campussen.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<Campus> getCampusesWithFood(String name){
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		List<Campus> possibleCampusList=new ArrayList<Campus>();
		for (Campus campus : getBigHospital().getCampuses()) {
			if (campus.getWarehouse().getStockList().conditionIsTrue(new HasEnoughFoodCondition()))
				possibleCampusList.add(campus);
		}
		return possibleCampusList;
	}
}
