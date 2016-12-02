package system.controllers;

import java.util.ArrayList;
import java.util.List;

import client.ICampus;
import client.IScheduleResource;
import client.IStaffMember;

import system.campus.Campus;
import system.campus.CampusId;
import system.exceptions.IllegalAccessException;
import system.machines.Identifier;
import system.medicaltests.MedicalTest;
import system.repositories.MachineRepository;
import system.repositories.MachineType;
import system.repositories.StaffRepository;
import system.repositories.StaffType;
import system.scheduling.ScheduleResource;
import system.staff.Nurse;
import system.staff.Shift;
import system.staff.StaffMember;
import system.time.Time;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Treatment;
import annotations.SystemAPI;

/**
 * Deze klasse is een controller voor het controlleren van operaties 
 * die uitgevoerd mogen worden door een ziekenhuisadministrator.
 * In de huidige versie van het programma kan deze gebruiker 
 * ziekenhuispersoneel en -gereedschap toevoegen en de tijd
 * vooruit laten gaan. 
 * 
 * q
 * @author SWOP Team 10
 */
@SystemAPI
public class AdministratorController extends StaffController{

	/**
	 * Constructor voor AdministratorController
	 * 
	 * @param sessionController
	 *            de sessionController van de sessie waarin de
	 *            AdminstratorController ook gebruikt wordt
	 */
	@SystemAPI
	public AdministratorController(SessionController sessionController){
		super(sessionController);
 	}

	/**
	 * Methode om te verzekeren dat de ingelogde gebruiker
	 * een hospital administrator is.
	 * 
	 * @return 	true als de user access in de sessie controller nog klopt.
	 * 			|	result = sessionController.hasValidAccess(StaffType.HOSPITAL_ADMINISTRATOR)
	 */
	@Override
	protected boolean hasValidAccess() {
		return sessionController.hasValidAccess(StaffType.HOSPITAL_ADMINISTRATOR);
	}
	
	/**
	 * Methode om te kijken of de gegeven naam niet leeg of null is.
	 * @param 	name
	 * 			De string die moet gecheckt worden.
	 * @return	true als de naam niet null of leeg is.
	 * 			|	result = !(name.equals("") || name == null)
	 */
	private boolean isValidName(String name) {
		return !(name.equals("") || name == null);
	}

	/**
	 * Methode om te kijken of de gegeven machineType toegelaten is.
	 * @param 	machineType
	 * 			De MachineType die moet gecheckt worden.
	 * @return	true als de machineType niet null is.
	 * 			|	result = !(machineType == null)
	 */
	private boolean isValidMachineType(MachineType machineType) {
		return !(machineType == null);
	}
	
	/**
	 * Zoekt op of een bepaalde naam al voor komt bij het personeel van de
	 * hospital.
	 * 
	 * @param name
	 *            De op te zoeken naam naam.
	 * @return true als er al een staffmember in de hospital bestaat met de
	 *         opgegeven naam, anders false.
	 */
	@SystemAPI
	public boolean isExistingStaffMemberName(String name) throws IllegalAccessException {
		for(Campus campus: getBigHospital().getCampuses())				
				for (StaffMember otherStaffMember : campus.getStaffRepository().getStaff()) {
			if (name.equals(otherStaffMember.getName()))
				return true;
		}
		for (StaffMember otherStaffMember : getHospitalStaffRepository().getStaff()) {
			if (name.equals(otherStaffMember.getName()))
				return true;
		}		return false;
	}
	
	/**
	 * Voeg een dokter met bijbehorende naam toe aan het systeem.
	 * 
	 * @param name
	 *            naam van de dokter
	 * @param 	switcher
	 * 			Als true dan zal deze dokter een switcher zijn. D.w.z. hij reist
	 * 			tussen de campussen.
	 * @throws IllegalAccessException
	 * 				Als de ingelogde gebruiker geen hospital administrator is
	 * @throws IllegalArgumentException
	 * 				Als er al een dokter met opgegeven naam in het systeem staat
	 * @throws NullPointerException
	 * 				Als een null naam, of als naam een lege string, wordt meegegeven
	 * @return doctor
	 * 				De toegevoegde dokter
	 */
	@SystemAPI
	public IStaffMember addDoctor(String name, boolean switcher) throws NullPointerException, IllegalArgumentException, IllegalAccessException{
		this.addStaffExceptionThrower(name);
		StaffMember doctor = sessionController.getBigHospital().getStaffRepository().addStaffMember(StaffType.DOCTOR,name);
		
		if (switcher) {
			for (ICampus campus : sessionController.getCampuses()) {
				addShift(doctor,campus,9,0,17,0);
			}
		}

		return doctor;
	}
	
	/**
	 * Voeg een dokter met bijbehorende naam toe aan het systeem.
	 * 
	 * @param name
	 *            naam van de dokter
	 * 			tussen de campussen.
	 * @throws IllegalAccessException
	 * 				Als de ingelogde gebruiker geen hospital administrator is
	 * @throws IllegalArgumentException
	 * 				Als er al een dokter met opgegeven naam in het systeem staat
	 * @throws NullPointerException
	 * 				Als een null naam, of als naam een lege string, wordt meegegeven
	 * @return doctor
	 * 				De toegevoegde dokter
	 */
	@SystemAPI
	public IStaffMember addDoctor(String name) 
			throws NullPointerException, IllegalArgumentException, IllegalAccessException{
		this.addStaffExceptionThrower(name);

		StaffMember doctor = sessionController.getBigHospital().getStaffRepository().addStaffMember(StaffType.DOCTOR,name);

		return doctor;
	}

	/**
	 * Voeg een dokter met bijbehorende naam en shift toe aan het systeem.
	 * 
	 * @pre	Het begin van de shift is voor het einde.
	 * 		Een shift die over 00:00 loopt wordt niet ondersteund.	
	 * 		|	(startingShiftHour < endingShiftHour) || (startingShiftHour == 
	 * 		|		endingShiftHour && startingShiftMinute < endingShiftMinute)
	 * @param 	name
	 *        	naam van de dokter
	 * @param 	staffMember
	 * 			De staffmember die een shift moet krijgen.
	 * @param	campus
	 * 			De campus waar de shift doorgaat.
	 * @param 	startingShiftHour
	 * 			Het beginuur van de shift.
	 * @param 	startingShiftMinute
	 * 			De beginminuut van de shift.
	 * @param 	endingShiftHour
	 * 			Het einduur van de shift.
	 * @param 	endingShiftMinute
	 * 			De eindminuut van de shift.
	 * @throws 	IllegalAccessException
	 * 			Als de ingelogde gebruiker geen hospital administrator is
	 * @throws 	IllegalArgumentException
	 * 			Als er al een dokter met opgegeven naam in het systeem staat
	 * @throws 	NullPointerException
	 * 			Als een null naam, of als naam een lege string, wordt meegegeven
	 * @return 	doctor
	 * 			De toegevoegde dokter
	 */
	@SystemAPI
	public IStaffMember addDoctor(String name, Campus campus, 
			int startingShiftHour, int startingShiftMinute,
			int endingShiftHour, int endingShiftMinute) 
		throws NullPointerException, IllegalArgumentException, IllegalAccessException {
		this.addStaffExceptionThrower(name);

		StaffMember doctor = sessionController.getBigHospital().getStaffRepository().addStaffMember(StaffType.DOCTOR,name);
		
		addShift(doctor, campus, startingShiftHour, startingShiftMinute, endingShiftHour, endingShiftMinute);

		return doctor;
	}

	/**
	 * Voeg een verpleegster met bijbehorende naam toe aan het systeem.
	 * 
	 * @param name
	 *            naam van de verpleegster
	 * @throws IllegalAccessException
	 *             Als de ingelogde gebruiker geen hospital administrator is.
	 * @throws IllegalArgumentException
	 * 				Als er al een verpleegster met opgegeven naam in het systeem staat
	 * @throws NullPointerException
	 * 				Als een null naam, of als naam een lege string, wordt meegegeven
	 * @return nurse
	 * 				De toegevoegde verpleegster
	 */
	@SystemAPI
	public IStaffMember addNurse(String name) throws IllegalAccessException, NullPointerException, IllegalArgumentException{
		this.addStaffExceptionThrower(name);

		return getCurrentCampusStaffRepository().addStaffMember(StaffType.NURSE,name);
	}
	
	/**
	 * Voeg een shift toe voor een gegeven staffmember. 
	 * 
	 * @pre	Het begin van de shift is voor het einde.
	 * 		Een shift die over 00:00 loopt wordt niet ondersteund.	
	 * 		|	(startingShiftHour < endingShiftHour) || (startingShiftHour == 
	 * 		|		endingShiftHour && startingShiftMinute < endingShiftMinute)
	 * @param 	staffMember
	 * 			De staffmember die een shift moet krijgen.
	 * @param	campus
	 * 			De campus waar de shift doorgaat.
	 * @param 	startingShiftHour
	 * 			Het beginuur van de shift.
	 * @param 	startingShiftMinute
	 * 			De beginminuut van de shift.
	 * @param 	endingShiftHour
	 * 			Het einduur van de shift.
	 * @param 	endingShiftMinute
	 * 			De eindminuut van de shift.
	 */
	@SystemAPI
	public void addShift(IStaffMember staffMember, ICampus campus, 
			int startingShiftHour, int startingShiftMinute,
			int endingShiftHour, int endingShiftMinute) {
		if (!((startingShiftHour < endingShiftHour) || (startingShiftHour == 
			endingShiftHour && startingShiftMinute < endingShiftMinute)))
			throw new IllegalArgumentException("het begin van de shift valt voor het einde");
		Campus newCampus = (Campus) campus;
		((StaffMember)staffMember).addShift(new CampusId(newCampus),
				new Shift(new TimePeriod(new TimeStamp(1,1,1,startingShiftHour,startingShiftMinute),
						new TimeStamp(1,1,1,endingShiftHour,endingShiftMinute))));
	}
	
	/**
	 * Voeg een Nurse toe met een gegeven shift. 
	 * 
	 * @pre	Het begin van de shift is voor het einde.
	 * 		Een shift die over 00:00 loopt wordt niet ondersteund.	
	 * 		|	(startingShiftHour < endingShiftHour) || (startingShiftHour == 
	 * 		|		endingShiftHour && startingShiftMinute < endingShiftMinute)
	 * @param 	staffMember
	 * 			De staffmember die een shift moet krijgen.
	 * @param 	startingShiftHour
	 * 			Het beginuur van de shift.
	 * @param 	startingShiftMinute
	 * 			De beginminuut van de shift.
	 * @param 	endingShiftHour
	 * 			Het einduur van de shift.
	 * @param 	endingShiftMinute
	 * 			De eindminuut van de shift.
	 */
	@SystemAPI
	public IStaffMember addNurse(String name, int startingShiftHour, int startingShiftMinute,
			int endingShiftHour, int endingShiftMinute) {
		IStaffMember nurse = addNurse(name);
		
		addShift(nurse,getCurrentCampus(),
				startingShiftHour,startingShiftMinute,endingShiftHour,endingShiftMinute);
		
		return nurse;
	}

	/**
	 * Voeg een warehouse manager toe
	 * 
	 * @param name
	 *            naam van de warehouse manager
	 * @return 
	 * @throws IllegalAccessException
	 *             Als de ingelogde gebruiker geen hospital administrator is.
	 */
	@SystemAPI
	public IStaffMember addWarehouseManager(String name) throws IllegalAccessException, NullPointerException, IllegalArgumentException {
		this.addStaffExceptionThrower(name);

		return getCurrentCampusStaffRepository().addStaffMember(StaffType.WAREHOUSE_MANAGER,name);

	}

	/**
	 * Voeg een machine toe
	 * 
	 * @param ID
	 *            het identificatienummer van de machine
	 * @param floor
	 *            verdiepingsnummer van de machine
	 * @param room
	 *            kamernummer van de machine
	 * @param machineType
	 *            type van de machine
	 * @throws IllegalAccessException
	 */
	@SystemAPI
	public void addMachine(int ID, int floor, int room,
			MachineType machineType) throws NullPointerException, IllegalArgumentException, IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		if(!this.isValidMachineType(machineType))
			throw new IllegalArgumentException("Het argument 'machineType' is ongeldig.");
		
		getMachineRepository().addMachine(new Identifier(ID), floor, room, machineType);
	}

	/**
	 * @return De StaffRepository van de hospital
	 */
	@SystemAPI
	public StaffRepository getCurrentCampusStaffRepository() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		return this.getCurrentCampus().getStaffRepository();
	}
	
	public StaffRepository getHospitalStaffRepository() throws IllegalAccessException {
		if (!this.hasValidAccess())
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		return this.getBigHospital().getStaffRepository();
	}

	/**
	 * @return De MachineRepository van de hospital
	 */
	@SystemAPI
	public MachineRepository getMachineRepository() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		return this.getCurrentCampus().getMachineRepository();
	}

	/**
	 * Spoel de tijd vooruit en voer alle geplande events uit die tussen het
	 * huidige en de het gegeven tijdstip gebeuren.
	 * 
	 * @param now
	 * 			De nieuwe huidige tijd waar men naartoe wil.
	 */
	@SystemAPI
	public void advanceTime(TimeStamp now) throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		Time time = this.getBigHospital().getHospitalTime();
		time.setTime(now);
	}
	
	/**
	 * Methode om verpleegsters met openstaande medische
	 * tests aan een lijst toe te voegen.
	 * 
	 * @return Lijst verpleegsters met openstaande medische tests
	 */
	@SystemAPI
	public List<IStaffMember> getMedicalTestNurses() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		List<IStaffMember> nurses = new ArrayList<IStaffMember>();
		for (StaffMember staffMember : this.getCurrentCampus().getStaffRepository().getStaffMembers(StaffType.NURSE)) {
			Nurse nurse = (Nurse) staffMember;
			List<MedicalTest> tests = nurse.getOpenMedicalTests();
			if (!tests.isEmpty()) {
				nurses.add(nurse);
			}
		}
		return nurses;
	}
	
	/**
	 * Methode om verpleegsters met openstaande treatments
	 * aan een lijst toe te voegen.
	 * 
	 * @return Lijst verpleegsters met openstaande treatments
	 */
	@SystemAPI
	public List<IStaffMember> getTreatmentNurses() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		List<IStaffMember> nurses = new ArrayList<IStaffMember>();
		for (StaffMember staffMember : this.getCurrentCampus().getStaffRepository().getStaffMembers(StaffType.NURSE)) {
			Nurse nurse = (Nurse) staffMember;
			List<Treatment> tests = nurse.getOpenTreatments();
			if (!tests.isEmpty()) {
				nurses.add(nurse);
			}
		}
		return nurses;
	}
	
	/**
	 * Deze methode gaat alle uitzonderingen na en werpt een exception als dat moet
	 * 
	 * @param name
	 * 			naam van het personeelslid
	 * @throws IllegalAccessException
	 * 			geen toegang voor het toevoegen
	 * @throws NullPointerException
	 * 			de naam is niet toegelaten
	 * @throws IllegalArgumentException
	 * 			de naam bestaat al
	 */
	private void addStaffExceptionThrower(String name) throws IllegalAccessException, NullPointerException, IllegalArgumentException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		if (!this.isValidName(name))
			throw new NullPointerException("De gekozen naam is niet toegelaten.");
		if (this.isExistingStaffMemberName(name))
			throw new IllegalArgumentException(
			"Deze naam bestaat al.");
	}
	
	/**
	 * Vraagt of de campus in lockdown is.
	 * 
	 * @return
	 * 			waar als de campus in lockdown is
	 */
	@SystemAPI
	public boolean campusIsInLockdown() {
		return getCurrentCampus().isInLockdown();
	}
	
	/**
	 * Maakt dat de campus terug open is en dat de ziektes opnieuw ge•nitialiseerd worden.
	 */
	@SystemAPI
	public void openCampusAfterLockdown() {
		getCurrentCampus().open();
		getCurrentCampus().resetDiseases();
	}
	
	/**
	 * Geeft een lijst van personen die de campus hebben verlaten en sinds een eerste
	 * bedreigende diagnose aanwezig zijn geweest op de campus.
	 * 
	 * @return
	 * 			een lijst van personeelsleden en pati‘nten in bedreiging
	 * 
	 * @throws IllegalOperationException
	 * 			als de campus niet in lockdown is
	 */
	@SystemAPI
	public List<IScheduleResource> getListOfPeopleWhoLeft() {
		if (getCurrentCampus().isInLockdown()) {
			List<IScheduleResource> returnList = new ArrayList<IScheduleResource>();
			
			for (ScheduleResource resource : getCurrentCampus().getListfPeopleWhoLeft()) 
				returnList.add((IScheduleResource) resource);
			
			return returnList;
		} else 
			return null;
	}
}
