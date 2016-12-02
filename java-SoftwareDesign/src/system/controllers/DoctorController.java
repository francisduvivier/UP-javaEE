package system.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import client.IDiagnosis;
import client.IPatient;
import client.IResult;
import client.ISchedule;
import client.IStaffMember;

import system.campus.Campus;
import system.controllers.SessionController.CommandInvoker;
import system.epidemic.EpidemicThreat;
import system.exceptions.IllegalAccessException;
import system.exceptions.IllegalOperationException;
import system.exceptions.InsufficientWarehouseItemsException;
import system.exceptions.ReschedulingException;
import system.exceptions.SchedulingException;
import system.medicaltests.MedicalTest;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.patients.Patient.PatientFile;
import system.repositories.PatientRepository;
import system.repositories.StaffType;
import system.scheduling.Appointment;
import system.scheduling.Priority;
import system.scheduling.Schedule;
import system.scheduling.ScheduledItem;
import system.staff.Doctor;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.treatments.Treatment;
import system.util.BoundedStack;
import system.warehouse.MedicationItemType;
import annotations.SystemAPI;

/**
 * constructor voor een Doctorcontroller
 * 
 * @Invar sessionController is niet null;
 */
@SystemAPI
public class DoctorController extends StaffController {
	private final DiagnosisController diagnosisController;
	private final OrderMedicalTestController orderMedicalTestController;
	private final PrescribeTreatmentController prescribeTreatmentController;
	private final CommandInvoker invoker;
	private Patient selectedPatient;

	/**
	 * Constructor voor een DoctorController.
	 * 
	 * @param sessionController
	 *            De sessionController van de sessie waarin de DoctorController
	 *            ook gebruikt wordt.
	 */
	@SystemAPI
	public DoctorController(SessionController sessionController) {
		super(sessionController);
	
		this.diagnosisController = new DiagnosisController();
		this.prescribeTreatmentController = new PrescribeTreatmentController();
		this.orderMedicalTestController = new OrderMedicalTestController();
		this.invoker = sessionController.getCommandInvoker();
		this.selectedPatient = null;
	}

	/**
	 * Methode om te verzekeren dat de ingelogde gebruiker
	 * een dokter is.
	 * 
	 * @return 	true als de user access in de sessie controller nog klopt.
	 * 			|	result = sessionController.hasValidAccess(StaffType.DOCTOR)
	 */
	@Override
	protected boolean hasValidAccess() {
		return sessionController.hasValidAccess(StaffType.DOCTOR);
	}
	
	private void exceptionThrower() throws IllegalAccessException {
		if (!hasValidAccess())
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
	}
	
	/**
	 * voor een nieuwe diagnose, die geen tweede opinie nodig heeft, in voor de
	 * geselecteerde patient. De patientFile van de Patient moet geopend zijn
	 * door de ingelogde dokter.
	 * 
	 * @param description
	 *            De beschrijving van de diagnose die ingevoerd moet worden.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void enterDiagnosis(String description) {
		this.enterDiagnosis(description,1);
	}
	
	/**
	 * voor een nieuwe diagnose, die geen tweede opinie nodig heeft, in voor de
	 * geselecteerde patient. De patientFile van de Patient moet geopend zijn
	 * door de ingelogde dokter.
	 * 
	 * @param description
	 *            De beschrijving van de diagnose die ingevoerd moet worden.
	 * @param threat
	 *            De bedreiging van de diagnose die ingevoerd moet worden.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void enterDiagnosis(String description, int threat) {
		this.exceptionThrower();

		invoker.executeCommand(new EnterDiagnosisCommand(diagnosisController,
				this.getDoctor(), this.getCurrentPatient(), description,
				new EpidemicThreat(threat)));
	}

	/**
	 * voer een nieuwe diagnose, die WEL een tweede opinie nodig heeft, in voor
	 * de geselecteerde patient. De patientFile van de Patient moet geopend zijn
	 * door de ingelogde dokter
	 * 
	 * @param approvingDoctor
	 *            De tweede dokter die de diagnose zal moeten goedkeuren.
	 * @param description
	 *            De beschrijving van de diagnose die ingevoerd moet worden.
	 *            
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void enterDiagnosis(IStaffMember approvingDoctor, String description) {
		this.enterDiagnosis(approvingDoctor,description,1);
	}
	
	/**
	 * voer een nieuwe diagnose, die WEL een tweede opinie nodig heeft, in voor
	 * de geselecteerde patient. De patientFile van de Patient moet geopend zijn
	 * door de ingelogde dokter
	 * 
	 * @param approvingDoctor
	 *            De tweede dokter die de diagnose zal moeten goedkeuren.
	 * @param description
	 *            De beschrijving van de diagnose die ingevoerd moet worden.       
	 * @param threat
	 *            De bedreiging van de diagnose die ingevoerd moet worden.
	 *            
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void enterDiagnosis(IStaffMember approvingDoctor, String description, int threat) {
		this.exceptionThrower();

		invoker.executeCommand(new EnterDiagnosisCommand(diagnosisController,
				this.getDoctor(), (Doctor) approvingDoctor, this.getCurrentPatient(),
				description, new EpidemicThreat(threat)) );
	}

	/**
	 * laat de ingelogde dokter een diagnose,die hij moet herzien, goedkeuren.
	 * 
	 * @param	diagnosis
	 * 			De goed te keuren diagnose
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void approveDiagnosis(IDiagnosis diagnosis) {
		this.exceptionThrower();

		invoker.executeCommand(new ApproveDiagnosisCommand(diagnosisController,
				(Diagnosis) diagnosis));
	}

	/**
	 * Laat de ingelogde dokter een diagnose, die hij moet herzien, afkeuren. In
	 * dit geval moet er ook een bechrijving gegeven worden voor een nieuwe
	 * diagnose.
	 * 
	 * @param	diagnosis
	 * 			De te ontkennen diagnose
	 * @param	newDetails
	 * 			De nieuwe detailgegevens van de diagnose
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void denyDiagnosis(IDiagnosis diagnosis, String newDetails) {
		this.exceptionThrower();
		
		invoker.executeCommand(new DenyDiagnosisCommand(diagnosisController,
				(Diagnosis) diagnosis, ((Diagnosis) diagnosis).opposingDiagnosis(newDetails)));
	}

	/**
	 * Hiermee kan de ingelogde dokter een Cast voorschijven voor de
	 * geselecteerde Patient. De details voor de nieuwe treatment, en de
	 * diagnose waarbij de treatment hoort, moeten meegegeven worden als
	 * argumenten.
	 * 
	 * @param	diagnose
	 * 			De diagnose behorende bij de cast
	 * @param	bodyParty
	 * 			Het lichaamsdeel waar de cast voor is
	 * @param	durationInDays
	 * 			De duratie van het dragen van de cast
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeCast(IDiagnosis diagnosis, String bodyPart,
			int durationInDays) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeCastCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, bodyPart,
				durationInDays));
	}
	
	/**
	 * Hiermee kan de ingelogde dokter een Cast voorschijven voor de
	 * geselecteerde Patient. De details voor de nieuwe treatment, en de
	 * diagnose waarbij de treatment hoort, moeten meegegeven worden als
	 * argumenten. 
	 * Met priority.
	 * 
	 * @param	diagnose
	 * 			De diagnose behorende bij de cast
	 * @param	bodyParty
	 * 			Het lichaamsdeel waar de cast voor is
	 * @param	durationInDays
	 * 			De duratie van het dragen van de cast
	 * @param	priority
	 * 			De prioriteit van de cast
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeCast(IDiagnosis diagnosis, Priority priority, String bodyPart,
			int durationInDays) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeCastCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, priority, bodyPart,
				durationInDays));
	}

	/**
	 * Hiermee kan de ingelogde dokter Medication voorschijven voor de
	 * geselecteerde Patient.De details voor de nieuwe treatment, en de diagnose
	 * waarbij de treatment hoort, moeten meegegeven worden als argumenten.
	 * 
	 * @param	diagnose
	 * 			De diagnose horende bij de medicatie
	 * @param	description
	 * 			De beschrijving horende bij de medicatie
	 * @param	sensitive
	 * 			Of de medicatie gevoelig is
	 * @param 	medicationItems
	 * 			De lijst van medicatie items
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeMedication(IDiagnosis diagnosis, String description,
			boolean sensitive, List<MedicationItemType> medicationItems) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeMedicationCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, description,
				sensitive, medicationItems));
	}
	
	/**
	 * Hiermee kan de ingelogde dokter Medication voorschijven voor de
	 * geselecteerde Patient.De details voor de nieuwe treatment, en de diagnose
	 * waarbij de treatment hoort, moeten meegegeven worden als argumenten.
	 * Met priority.
	 * 
	 * @param	diagnose
	 * 			De diagnose horende bij de medicatie
	 * @param	description
	 * 			De beschrijving horende bij de medicatie
	 * @param	sensitive
	 * 			Of de medicatie gevoelig is
	 * @param 	medicationItems
	 * 			De lijst van medicatie items
	 * @param	priority
	 * 			De prioriteit van de medicatie
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeMedication(IDiagnosis diagnosis, Priority priority,
			String description, boolean sensitive, List<MedicationItemType> medicationItems) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeMedicationCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, priority, description,
				sensitive, medicationItems));
	}

	/**
	 * Hiermee kan de ingelogde dokter een Surgery voorschijven voor de
	 * geselecteerde Patient. De details voor de nieuwe treatment, en de
	 * diagnose waarbij de treatment hoort, moeten meegegeven worden als
	 * argumenten.
	 * 
	 * @param	diagnosis
	 * 			De diagnose horende bij de operatie
	 * @param 	description
	 * 			De beschrijving van de operatie
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeSurgery(IDiagnosis diagnosis, String description) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeSurgeryCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, description));
	}
	
	/**
	 * Hiermee kan de ingelogde dokter een Surgery voorschijven voor de
	 * geselecteerde Patient. De details voor de nieuwe treatment, en de
	 * diagnose waarbij de treatment hoort, moeten meegegeven worden als
	 * argumenten.
	 * Met priority.
	 * 
	 * @param	diagnosis
	 * 			De diagnose horende bij de operatie
	 * @param 	description
	 * 			De beschrijving van de operatie
	 * @param 	priority
	 * 			De prioriteit van de operatie
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void prescribeSurgery(IDiagnosis diagnosis, Priority priority, String description) {
		this.exceptionThrower();

		invoker.executeCommand(new PrescribeSurgeryCommand(
				prescribeTreatmentController, (Diagnosis) diagnosis, priority, description));
	}


	/**
	 * vraag om een BloodAnalysis voor de geselteerde Patient. De details voor
	 * de medische test moeten meegegeven worden als argumenten.
	 * 
	 * @param 	focus
	 * 			De focus van de bloed analyse
	 * @param	numberOfAnalyses
	 * 			Aantal analysen
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void orderBloodAnalysis(String focus, int numberOfAnalyses) {
		this.exceptionThrower();

		invoker.executeCommand(new OrderBloodAnalysisCommand(
				this.orderMedicalTestController, this.getCurrentPatient(),
				focus, numberOfAnalyses));
	}
	
	/**
	 * vraag om een BloodAnalysis voor de geselteerde Patient. De details voor
	 * de medische test moeten meegegeven worden als argumenten.
	 * Met priority.
	 * 
	 * @param 	focus
	 * 			De focus van de bloed analyse
	 * @param	numberOfAnalyses
	 * 			Aantal analysen
	 * @param 	priority
	 * 			De prioriteit
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void orderBloodAnalysis(Priority priority, String focus, int numberOfAnalyses) {
		this.exceptionThrower();

		invoker.executeCommand(new OrderBloodAnalysisCommand(
				this.orderMedicalTestController, this.getCurrentPatient(), priority,
				focus, numberOfAnalyses));
	}

	/**
	 * vraag om een UltraSoundScan voor de geselteerde Patient. De details voor
	 * de medische test moeten meegegeven worden als argumenten.
	 * 
	 * @param 	focus
	 * 			De focus van de ultrasound scan
	 * @param 	recordVideo
	 *  		Of er video wordt opgenomen
	 * @param 	recordImages
	 * 			Of er afbeeldingen worden opgenomen  
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void orderUltrasoundScan(String focus, boolean recordVideo,
			boolean recordImages) {
		this.exceptionThrower();
		
		invoker.executeCommand(new OrderUltrasoundScanCommand(
				orderMedicalTestController, this.getCurrentPatient(), focus,
				recordVideo, recordImages));
	}
	
	/**
	 * vraag om een UltraSoundScan voor de geselteerde Patient. De details voor
	 * de medische test moeten meegegeven worden als argumenten.
	 * Met priority.
	 * 
	 * @param 	focus
	 * 			De focus van de ultrasound scan
	 * @param 	recordVideo
	 *  		Of er video wordt opgenomen
	 * @param 	recordImages
	 * 			Of er afbeeldingen worden opgenomen
	 * @param 	priority
	 * 			De prioriteit
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void orderUltrasoundScan(Priority priority, String focus, boolean recordVideo,
			boolean recordImages) {
		this.exceptionThrower();
		
		invoker.executeCommand(new OrderUltrasoundScanCommand(
				orderMedicalTestController, this.getCurrentPatient(), priority, focus,
				recordVideo, recordImages));
	}

	/**
	 * vraag om een XRayScan voor de geselteerde Patient. De details voor de
	 * medische test moeten meegegeven worden als argumenten.
	 * 
	 * @param	bodyParty
	 * 			Het lichaamsdeel waar de X-Ray scan van toepassing is
	 * @param	numberOfImagesNeeded
	 * 			Aantal afbeeldingen nodig
	 * @param	zoomlevel
	 * 			Het inzoomlevel van de scan
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void orderXRayScan(String bodyPart, int numberOfImagesNeeded,
			int zoomlevel) {
		this.exceptionThrower();

		invoker.executeCommand(new OrderXRayScanCommand(
				orderMedicalTestController, this.getCurrentPatient(),
				bodyPart, numberOfImagesNeeded, zoomlevel));
	}
	
	/**
	 * vraag om een XRayScan voor de geselteerde Patient. De details voor de
	 * medische test moeten meegegeven worden als argumenten.
	 * Met priority.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als een onbevoegde staff member de methode uitvoert
	 */
	@SystemAPI
	public void orderXRayScan(Priority priority, String bodyPart, int numberOfImagesNeeded,
			int zoomlevel) {
		this.exceptionThrower();

		invoker.executeCommand(new OrderXRayScanCommand(
				orderMedicalTestController, this.getCurrentPatient(), priority,
				bodyPart, numberOfImagesNeeded, zoomlevel));
	}

	/**
	 * Ongedaan maken van een commando.
	 * 
	 * @param 	index
	 *        	Het te verwijderen commando.
	 * @throws	IllegalAccessException
	 * 			Als een onbevoegde staff member de methode uitvoert
	 */
	@SystemAPI
	public void undoCommand(int index) {
		this.exceptionThrower();

		invoker.undoCommand(index);
	}

	/**
	 * Herdoet een commando dat eerder ongedaangemaakt was. De index kan van 0
	 * tot 4 gaan.
	 * 
	 * @param 	index
	 * 			Indexnummer van de command dat ongedaan moet worden
	 * 
	 * @throws	IllegalAccessException
	 * 			Als een onbevoegde staff member de methode uitvoert
	 */
	@SystemAPI
	public void redoCommand(int index) throws IllegalAccessException {
		this.exceptionThrower();

		invoker.redoCommand(index);
	}

	/**
	 * @return een BoundedStack van Commando's die uitgevoerd zijn geweest.
	 */
	@SystemAPI
	public BoundedStack<Command> getUndoCommands() {
		this.exceptionThrower();
		return invoker.getPreviousCommands();
	}

	/**
	 * @return geeft een BoundedStack terug van Commando's die ongedaan gemaakt
	 *         zijn geweest.
	 */
	@SystemAPI
	public BoundedStack<Command> getRedoCommands() {
		this.exceptionThrower();
		return invoker.getNextCommands();
	}

	/**
	 * @return een lijst van diagnoses die een tweede opinie nodig hebben van de
	 *         ingelogde Doctor
	 *         
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public List<IDiagnosis> getSecondOpinionDiagnoses() {
		this.exceptionThrower();
		
		List<IDiagnosis> diagnoses = new ArrayList<IDiagnosis>();
		diagnoses.addAll(diagnosisController.getSecondOpinionDiagnosis(this.getDoctor()));
		
		return Collections.unmodifiableList(diagnoses);
	}

	/**
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * 
	 * @return de Doctor die ingelogd is.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als een onbevoegde staff member de methode uitvoert
	 */
	@SystemAPI
	public Doctor getDoctor() {
		this.exceptionThrower();
		
		return (Doctor) this.sessionController.getCurrentUser();
	}

	/**
	 * Geeft de geopenede PatientFile van de actieve Doctor.
	 * 
	 * @return	patientFile
	 * 			De geopende PatientFile van de actieve Doctor.
	 */
	private PatientFile getOpenedPatientFile() {
		this.exceptionThrower();
		return this.getDoctor().getOpenPatientFile();
	}
	
	/**
	 * Kijkt na of de PatientFile niet null is en geopend. 
	 * 
	 * @param 	patientFile
	 * 			De PatientFile die moet worden nagekeken.
	 * @return	true als de PatientFile open en niet null is.
	 * 			result == !(patientFile == null || patientFile.isClosed)
	 */
	private boolean isOpenPatientFile(PatientFile patientFile) {
		this.exceptionThrower();
		return !(patientFile == null || patientFile.isClosed());
	}

	/**
	 * Consulteert een patient file en haalt eerst de gegeven patient uit de
	 * patientRepository
	 * 
	 * @param 	patient
	 * 			De patient waarvan we de file consulteren
	 * @return Results selectedPatient
	 */
	@SystemAPI
	public void consultPatientFile(IPatient patient) {
		this.exceptionThrower();
		this.setSelectedPatient((Patient) patient);

		if (this.isOpenPatientFile(this.getOpenedPatientFile())) 
			this.getOpenedPatientFile().setClosed(true);

		this.getCurrentPatient().getPatientFile().setClosed(false);
		this.getDoctor().setLastOpenedPatientFile(
				this.getCurrentPatient().getPatientFile());
	}

	/**
	 * Sluit de patient file van de behandelde patient
	 */
	@SystemAPI
	public void closePatientFile() {
		this.exceptionThrower();
		this.getCurrentPatient().getPatientFile().setClosed(true);
		this.selectedPatient = null;
	}

	/**
	 * Methode om de details van een diagnose te krijgen.
	 * 
	 * @param 	diagnosis
	 *          Diagnose waarvan we de details willen hebben
	 * @return 	details
	 */
	@SystemAPI
	public String getDiagnosisDetails(IDiagnosis diagnosis) {
		this.exceptionThrower();
		return diagnosisController.getDiagnosisDetails((Diagnosis) diagnosis);
	}

	/**
	 * Ontslaan van patient
	 */
	@SystemAPI
	public void dischargePatient() {
		this.exceptionThrower();
		for (Campus campus : getBigHospital().getCampuses()) {
			if (campus.getPatientRepository().getNonDischargedPatients().contains(selectedPatient))
				campus.getPatientRepository().dischargePatient(selectedPatient);
		}
	}

	/**
	 * Haalt resultaten op van een gegeven patient zijn patient file
	 * 
	 * @return Results patientFile
	 * 
	 * @throws 	NullPointerException
	 * 			Als er geen patient is
	 */
	@SystemAPI
	public List<IResult> getResults() {
		this.exceptionThrower();
		if (this.getCurrentPatient() == null)
			throw new NullPointerException("Patient is null.");

		PatientFile patientFile = this.getCurrentPatient().getPatientFile();
		
		List<IResult> results = new ArrayList<IResult>();
		results.addAll(patientFile.getResults());
		
		return Collections.unmodifiableList(results);
	}

	/**
	 * @return Lijst van patienten die nog niet ontslaan zijn uit het ziekenhuis
	 */
	@SystemAPI
	public List<IPatient> getNonDischargedPatients() {
		this.exceptionThrower();
		List<IPatient> patients = new ArrayList<IPatient>();
		patients.addAll(getBigHospital().getAllNonDischargedPatients());
		
		return Collections.unmodifiableList(patients);
	}
	
	/**
	 * @return De geselecteerde patient
	 */
	private Patient getCurrentPatient() {
		this.exceptionThrower();
		return this.selectedPatient;
	}

	/**
	 * 
	 * @return de geselecteerde patient
	 */
	@SystemAPI
	public IPatient getSelectedPatient() {
		this.exceptionThrower();
		return this.selectedPatient;
	}

	/**
	 * 	Kijkt na of de meegegeven patient geldig is.
	 * @param 	patient
	 * 			Na te kijken patient.
	 * @return	True als de patient niet null is en in het hospital geregistreerd is.
	 * 			|	result == !(patient==null || !this.getPatientRepository().getRegisteredPatients().contains(patient))
	 */
	private boolean isValidPatient(Patient patient) {
		return !(patient==null || !getBigHospital().getAllRegisteredPatients().contains(patient));
	}
	
	/**
	 * Methode om van een patient de geselecteerde patient te maken.
	 * 
	 * @param patient
	 *            De te selecteren patient
	 */
	private void setSelectedPatient(Patient patient) {
		this.exceptionThrower();
		if (!isValidPatient(patient))
			throw new IllegalArgumentException("De meegegeven patient is niet geldig.");

		this.selectedPatient = patient;
	}

	/**
	 * 
	 * @return Repository van de dokters
	 */
	@SystemAPI
	public List<IStaffMember> getDoctors() {
		this.exceptionThrower();
		List<IStaffMember> doctors = new ArrayList<IStaffMember>();
		doctors.addAll(getBigHospital().getStaffRepository().getStaffMembers(StaffType.DOCTOR));
		
		return Collections.unmodifiableList(doctors);
	}

	/**
	 * 
	 * @return Lijst van diagnosissen van geselecteerde patient
	 */
	@SystemAPI
	public List<IDiagnosis> getDiagnoses() {
		List<IDiagnosis> diagnoses = new ArrayList<IDiagnosis>();
		diagnoses.addAll(this.getCurrentPatient().getPatientFile().getDiagnoses());
		
		return Collections.unmodifiableList(diagnoses);
	}

	/**
	 * 
	 * @return Tijdschema van geselecteerde patient
	 */
	@SystemAPI
	public ISchedule getPatientSchedule() {
		return (this.getCurrentPatient().getSchedule());
	}
	/**
	 * 
	 * @return Tijdschema van geselecteerde patient
	 */
	@SystemAPI
	public Iterator<ScheduledItem<Appointment>> getAppointmentsOfPatient() {
		return (((Schedule) this.getPatientSchedule()).getScheduleByClass(Appointment.class).iterator());
	}
	
	/**
	 * @return Lijst van afspraken van geselecteerde patient
	 */
	@SystemAPI
	public List<Appointment> getPatientAppointments() {
		List<Appointment> appointments = new ArrayList<Appointment>();
		for (ScheduledItem<Appointment> item : ((Schedule) getPatientSchedule()).getScheduleByClass(Appointment.class))
			appointments.add(item.getScheduleEvent());
		return appointments;
	}
	/**
	 * Methode om toekomstige tijd te berekenen op basis van aantal uur.
	 * 
	 * @param nbOfHours
	 *            Aantal uur op te tellen
	 * @return Toekomstige tijd
	 * 
	 */
	private TimeStamp getFutureTime(int nbOfHours) {
		TimeStamp now = DoctorController.this.getBigHospital().getHospitalTime()
				.getTime();
		return TimeStamp.addedToTimeStamp(
				TimeDuration.hours(nbOfHours), now);
	}
	
	/**
	 * Dit is een inner klasse die methodes bevat voor het uivoeren van acties
	 * met diagnoses.
	 */
	class DiagnosisController {
		private DiagnosisController() {
		}

		/**
		 * 	Kijkt na of de gegeven diagnosis geldig is.
		 * @param 	diagnosis
		 * 			Na te kijken diagnosis.
		 * @return	True als diagnosis niet null is.
		 * 			|	result == !(diagnosis == null)
		 */
		private boolean isValidDiagnosis(Diagnosis diagnosis) {
			return !(diagnosis == null);
		}
		
		/**
		 * Voert een diagnose met de beschrijving, de dokter die de diagnose nam
		 * voor welke pati�nt, en de referentie van een tweede dokter voor een
		 * eventuele tweede mening. Deze methode mag enkel opgeroepen worden in
		 * EnterDiagnosisCommand
		 * 
		 * @throws 	NullPointerException
		 * 			Als de diagnose ongeldig is
		 */
		void enterDiagnosis(Diagnosis diagnosis) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");
			
			diagnosis.register();
		}

		/**
		 * Verwijderen van diagnose uit een patient file.
		 * 
		 * @param diagnosis
		 *            Te verwijderen diagnose
		 *            
		 * @throws	NullPointerException
		 * 			Als de diagnose ongeldig is
		 */
		void unregisterDiagnosis(Diagnosis diagnosis) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");

			diagnosis.unregister();
			while (!diagnosis.getTreatments().isEmpty())
				DoctorController.this.prescribeTreatmentController
						.unPrescribeTreatment(diagnosis.getTreatments().get(0));
		}

		/**
		 * Terug registeren van een diagnosis die al eens geregisterd is.
		 * 
		 * @param 	diagnosis
		 *          Te herregisteren diagnose
		 * @param	treatmentList
		 * 			Lijst van treatments voor diagnose
		 * @throws 	NullPointerException
		 * 			Als de diagnose ongeldig is
		 * @throws	ReschedulingException
		 * 			Als er een planningsprobleem is
		 */
		void reregisterDiagnosis(Diagnosis diagnosis,
				List<Treatment> treatmentList) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");
			
			String exceptionmsg = "";

			diagnosis.reregister();

			for (Treatment treatment : treatmentList) {
				try {
					DoctorController.this.prescribeTreatmentController
							.rePrescribeTreatment(treatment);
				} catch (InsufficientWarehouseItemsException e) {
					exceptionmsg += e.getMessage() + "\n";
				} catch (ReschedulingException e) {
					exceptionmsg += e.getMessage() + "\n";
				}
			}

			if (!exceptionmsg.isEmpty())
				throw new ReschedulingException(exceptionmsg);
		}

		/**
		 * Goedkeuren van een diagnose en het vervolgens plannen van zijn
		 * behandelingen.
		 * 
		 * @param 	diagnosis
		 *          Goed te keuren diagnose
		 *            
		 * @throws 	NullPointerException
		 * 			Als de diagnose ongeldig is
		 * @throws	InsufficientWarehouseItemsException
		 * 			Als er niet genoeg items in het warenhuis zitten
		 */
		void approveDiagnosis(Diagnosis diagnosis) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");
			
			String exceptionmsg = "";
			
			diagnosis.approve();

			for (Treatment treatment : diagnosis.getTreatments()) {
				try {
					getBigHospital().getScheduler().schedule(treatment, getFutureTime(1));
				} catch (InsufficientWarehouseItemsException e) {
					exceptionmsg += e.getMessage();
				}
			}
				
			if (!exceptionmsg.isEmpty())
				throw new InsufficientWarehouseItemsException(exceptionmsg);
		}

		/**
		 * Een diagnose die ge-approved is unnaproven, en de Treatments ervan
		 * unprescriben. (als deze niet zijn uitgevoerd)
		 * 
		 * @param	diagnosis
		 *       	De diagnosis die opnieuw gedaan moet worden
		 *       
		 * @throws	NullPointerException
		 * 			Als de diagnose niet geldig is
		 */
		void unapproveDiagnosis(Diagnosis diagnosis) 
				throws IllegalOperationException {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");
			
			diagnosis.unapprove();

			while (!diagnosis.getTreatments().isEmpty())
				DoctorController.this.prescribeTreatmentController
						.unPrescribeTreatment(diagnosis.getTreatments().get(0));
		}

		/**
		 * Opnieuw uitvoeren van een diagnose die al eens uit- gevoerd is.
		 * 
		 * @param 	diagnosis
		 *          De diagnosis die opnieuw gedaan moet worden
		 * @param	treatmentList
		 * 			Lijst van treatments voor diagnose
		 *            
		 * @throws	NullPointerException
		 * 			Als de diagnose niet geldig is
		 * @throws	InsufficientWarehouseItemsException
		 * 			Als er te weining warenhuis items zijn
		 * @throws	ReschedulingException
		 * 			Als er een schedulingprobleem is
		 */
		void reapproveDiagnosis(Diagnosis diagnosis, List<Treatment> treatmentList) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");
			
			String exceptionmsg = "";

			diagnosis.reapprove();

			for (Treatment treatment : treatmentList) {
				try {
					DoctorController.this.prescribeTreatmentController
							.rePrescribeTreatment(treatment);
				} catch (InsufficientWarehouseItemsException e) {
					exceptionmsg += e.getMessage() + "\n";
				} catch (ReschedulingException e) {
					exceptionmsg += e.getMessage() + "\n";
				}
			}

			if (!exceptionmsg.isEmpty())
				throw new ReschedulingException(exceptionmsg);
		}

		/**
		 * Herevalueren van een diagnose.
		 * 
		 * @param diagnosis
		 *            Her te evalueren diagnose
		 * @throws	NullPointerException
		 * 			Als de diagnose niet geldig is
		 */
		void denyDiagnosis(Diagnosis diagnosis) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");

			diagnosis.deny();
		}
		
		/**
		 * Herherevalueren van een diagnose.
		 * 
		 * @param diagnosis
		 *            Her te evalueren diagnose
		 * @throws	NullPointerException
		 * 			Als de diagnose niet geldig is
		 */
		void unDenyDiagnosis(Diagnosis diagnosis) {
			if (!isValidDiagnosis(diagnosis))
				throw new NullPointerException("Diagnosis is niet geldig.");

			diagnosis.undeny();
		}
		
		/**
		 * @param 	doctor
		 * 			Te controleren dokter
		 * @return	true
		 * 			Als de dokter geen tweede opinie diagnoses heeft
		 */
		public boolean isValidDecidingDoctor(Doctor doctor) {
			return !(doctor == null ||
					 doctor.getSecondOpinionDiagnoses() == null);
		}
		
		/**
		 * Methode om tweede opinies over diagnosissen van een dokter te
		 * krijgen.
		 * 
		 * @param decidingDoctor
		 *            Dokter waarvan we de tweede opinies willen hebben
		 * @return Lijst met daarin de diagnosissen van de tweede opinies
		 * 
		 * @throws	NullPointerException
		 * 			Als de diagnose niet geldig is
		 */
		List<Diagnosis> getSecondOpinionDiagnosis(Doctor decidingDoctor) {
			if (!this.isValidDecidingDoctor(decidingDoctor)) 
				throw new NullPointerException("Doctor is niet geldig.");
			
			return decidingDoctor.getSecondOpinionDiagnoses();
		}

		/**
		 * 
		 * @param diagnosis
		 *            Diagnose waarvan we de details willen
		 * @return Details van de diagnose
		 */
		String getDiagnosisDetails(Diagnosis diagnosis) {
			return ((Diagnosis) diagnosis).getDescription();
		}

		/**
		 * 
		 * @param diagnosis
		 *            Diagnose waarvan we de details van de behandeling willen
		 * @return Details van de behandeling
		 */
		String getTreatmentDetails(Diagnosis diagnosis) {

			String treatmentDetails = "";
			for (Treatment treatment : diagnosis.getTreatments()) {
				treatmentDetails += treatment.toString();
			}
			return treatmentDetails;
		}

	}

	/**
	 * Deze klasse is een controller voor het voorschrijven van treatments. Hier
	 * kan men o.a. medicatie voorschrijven voor een gegeven pati�nt.
	 * Treatments kunnen ofwel enkel opgeslagen, ofwel opgeslagen en gepland
	 * worden, afhankelijk van of een diagnose al goedgekeurd is of niet. Als de
	 * treatment word opgeslagen, dan wordt de treatment enkel toegevoegd aan de
	 * lijst van treatments van een diagnose. Als de treatment goedgekeurd is,
	 * dan word hij niet diagnose gezet/verwijderd uit de diagnose, maar wel
	 * gescheduled en bij de nurse gezet.
	 */
	class PrescribeTreatmentController {

		/**
		 * Constructor voor PrescribeTreatmentController
		 * 
		 * @param hospital
		 */
		private PrescribeTreatmentController() {
		}

		// Mag alleen gebruikt worden door prescribeTreatmentCommand
		/**
		 * Methode om een behandeling van een diagnose voor te schrijven (als
		 * die niet meer moet goedgekeurd worden) en ze vervolgens te plannen.
		 * 
		 * @param treatment
		 *            Voor te schrijven behandeling
		 */
		void prescribeTreatment(Treatment treatment)
				throws InsufficientWarehouseItemsException, SchedulingException {
			Diagnosis diagnosis = treatment.getDiagnosis();
			treatment.store();
			if (!diagnosis.needsApproval()) {
				DoctorController.this.getBigHospital().getScheduler().schedule(
						treatment, getFutureTime(1));

			}
		}

		/**
		 * Methode om een behandeling ongedaan te maken als voorschrift.
		 * 
		 * @param treatment
		 *            Ongedaan te maken behandeling.
		 * @throws SchedulingException
		 */
		void unPrescribeTreatment(Treatment treatment) {
			DoctorController.this.getBigHospital().getScheduler().unschedule(treatment);
		}

		/**
		 * Methode om ongedaan gemaakte voorgeschreven behandeling te herdoen.
		 * 
		 * @param treatment
		 *            Te herdoene behandeling
		 *            
		 * @throws	InsufficientWArehouseItemsException
		 * 			Als er niet genoeg warehouse items zijn
		 * @throws	ReschedulingsException
		 * 			Als er een planningsprobleem is
		 */
		void rePrescribeTreatment(Treatment treatment)
				throws ReschedulingException,
				InsufficientWarehouseItemsException {
			String exceptionmsg = "";
			
			try {
				DoctorController.this.getBigHospital().getScheduler().reschedule(treatment);
			} catch (ReschedulingException e) {
				// Moest gerescheduled worden, vroeger plaats al bezet.
				exceptionmsg += e.getMessage() + "\n";
				try {
					this.prescribeTreatment(treatment);
				} catch (InsufficientWarehouseItemsException i) {
					exceptionmsg += i.getMessage();
				}
			}
			
			if (!exceptionmsg.isEmpty())
				throw new ReschedulingException(exceptionmsg);
		}
	}

	class OrderMedicalTestController {

		private OrderMedicalTestController() {
		}

		/**
		 * 
		 * @return Repository van de patienten
		 */
		public PatientRepository getPatientRepository() {
			return DoctorController.this.getCurrentCampus().getPatientRepository();
		}

		/**
		 * Methode om een medische test te bestellen en te plannen.
		 * 
		 * @param medicalTest
		 *            Te bestellen medische test
		 */
		void orderMedicalTest(MedicalTest medicalTest)
				throws SchedulingException {

			DoctorController.this.getBigHospital().getScheduler().schedule(medicalTest,
					getFutureTime(1));
		}

		/**
		 * Ongedaan maken bestellen van medische test.
		 * 
		 * @param medicalTest
		 *            Ongedaan te maken medische test
		 */
		void unOrderMedicalTest(MedicalTest medicalTest) {
			DoctorController.this.getBigHospital().getScheduler().unschedule(medicalTest);
		}
		
		/**
		 * Methode om ongedaan gemaakte voorgeschreven test te herdoen.
		 * 
		 * @param medicalTest
		 *            Te herdoene behandeling
		 * 
		 * @throws ReschedulingsException
		 * 		   Als er een planningsprobleem is
		 * @throws InsuffiencetWarehouseItemsException
		 * 		   Als er te weinig warehouse items zijn
		 */
		void reOrderMedicalTest(MedicalTest medicalTest)
				throws ReschedulingException,
				InsufficientWarehouseItemsException {
			String exceptionmsg = "";
			
			try {
				DoctorController.this.getBigHospital().getScheduler().reschedule(medicalTest);
			} catch (ReschedulingException e) {
				// Moest gerescheduled worden, vroeger plaats al bezet.
				exceptionmsg += e.getMessage() + "\n";
				try {
					this.orderMedicalTest(medicalTest);
				} catch (InsufficientWarehouseItemsException i) {
					exceptionmsg += i.getMessage();
				}
			}
			
			if (!exceptionmsg.isEmpty())
				throw new ReschedulingException(exceptionmsg);
		}
	}
	
}
