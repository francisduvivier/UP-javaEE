package system.campus;

import java.util.ArrayList;
import java.util.List;

import client.ICampus;

import system.epidemic.EpidemicHandler;
import system.exceptions.IllegalOperationException;
import system.patients.Patient;
import system.repositories.CampusStaffRepository;
import system.repositories.MachineRepository;
import system.repositories.PatientRepository;
import system.repositories.ResourceRepository;
import system.repositories.ResourceType;
import system.repositories.StaffRepository;
import system.scheduling.ScheduleResource;
import system.staff.StaffMember;
import system.time.TimeStamp;
import system.util.Alarm;
import system.util.Resetter;
import system.warehouse.Warehouse;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockList;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een campus voor.
 * 
 * @author SWOP Team 10
 */
public class Campus implements ICampus {
	/**
	 * Collectie van het ziekenhuispersoneel
	 */
	private final StaffRepository staffRepository;
	/**
	 * Collectie van de machines van het ziekenhuis
	 */
	private final MachineRepository machineRepository;
	/**
	 * Collectie van de patienten van het ziekenhuis
	 */
	private final PatientRepository patientRepository;
	/**
	 * Variabele die het magazijn van het ziekenhuis voorstelt
	 */
	private final Warehouse warehouse;
	/**
	 * De conditie van de Campus. Dit is een predikaat dat meegegeven kan worden
	 * aan de PatientRepository (deze accepteert een instansie van de interface
	 * Condition en checkt deze voordat ze handelingen doet op haar patientenlijst)
	 */
	private final CampusCondition condition;

	/**
	 * Deze resetter kan megegeven worden aan EpidemicHandler. Campus houdt geen
	 * verbinding met EpidemicHandler, deze is immers een controllerende entiteit
	 * die overkoepelend is voor het hele campus systeem. Wanneer een epidemie
	 * voorbij is, moet de telling van het aantal ziektes gereset worden. Dit
	 * kan gemakkelijk door deze resetter mee te geven in de constructor van
	 * de EpidemicHandler. Die EpidemicHandler kan de juiste resetfunctie 'meegeven'
	 * door gebruik te maken van een private inner klasse die handeld op zijn lijst
	 * en Resetable is. Hierdoor is er geen koppeling tussen Campus en EpidemicHandler
	 * maar kan er toch gereset worden vanuit de Campus.
	 */
	private final Resetter diseaseResetter;
	
	/**
	 * De huidige status van de campus
	 */
	private CampusState state;

	/**
	 * De id van deze campus
	 */
	private final CampusId id;

	/**
	 * De campussen van de hospital. Er is een bidirectionele binding hiertussen
	 * maar deze kan niet inconsistent worden omdat zowel omdat de velden bij
	 * beide klassen final zijn en er in de contructor gechecked word of de
	 * binding klopt.
	 */
	private final Hospital hospital;

	/**
	 * Initialisatie van het ziekenhuis
	 * 
	 * @param hospital
	 *            Het ziekenhuis waartoe de campus behoort.
	 */
	public Campus(Hospital hospital) {
		this.hospital = hospital;
		this.staffRepository = new CampusStaffRepository();
		this.machineRepository = new MachineRepository();
		this.condition = new CampusCondition();
		this.diseaseResetter = new Resetter();
		this.patientRepository = new PatientRepository(this.condition,
				new EpidemicHandler(new CampusAlarm(), hospital
						.getHospitalTime(), this.diseaseResetter),
				hospital.getHospitalTime());
		this.state = new DefaultCampusState();
		this.id = new CampusId(this);
		this.warehouse = new Warehouse(new StockList(this));

		for (Stock stock : warehouse.getStockList().getStocks()) {
			stock.addObserver(getBigHospital().getScheduler());
		}

	}

	/**
	 * Getter voor de verzameling van ziekenhuispersoneel
	 * 
	 * @return staffRepository Collectie van ziekenhuispersoneel
	 */
	public StaffRepository getStaffRepository() {
		return staffRepository;
	}

	/**
	 * Getter voor de verzameling van ziekenhuismachines
	 * 
	 * @return machineRepository Collectie van machines van het ziekenhuis
	 */
	public MachineRepository getMachineRepository() {
		return machineRepository;
	}

	/**
	 * Getter voor de verzameling van ziekenhuispatienten
	 * 
	 * @return patientRepository Collectie van patienten van het ziekenhuis
	 */
	public PatientRepository getPatientRepository() {
		return patientRepository;
	}

	/**
	 * Getter voor het magazijn van het ziekenhuis
	 * 
	 * @return warehouse Het magazijn van het ziekenhuis
	 */
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Nodig voor het plannen Methode die een collectie van alle resources van
	 * deze campus teruggeeft
	 * 
	 * @return resourceRepositories Collectie bestaande uit : personeel,
	 *         machines en patienten
	 */
	private List<ResourceRepository> getResourceRepositories() {
		List<ResourceRepository> resourceRepositories = new ArrayList<ResourceRepository>();
		resourceRepositories.add(staffRepository);
		resourceRepositories.add(machineRepository);
		resourceRepositories.add(patientRepository);
		return resourceRepositories;
	}

	/**
	 * Nodig voor het plannen Methode die een collectie van resources van een
	 * bepaald type teruggeeft van deze campus
	 * 
	 * @param type
	 *            Het resource type waarvan we een lijst willen
	 * 
	 * @return resources Collectie van resources van het opgegeven type
	 */
	public List<ScheduleResource> getResources(ResourceType type) {
		List<ResourceRepository> resourceRepositories = this
				.getResourceRepositories();
		List<ScheduleResource> resources = new ArrayList<ScheduleResource>();

		for (ResourceRepository resourceRepository : resourceRepositories)
			resources.addAll(resourceRepository.getResources(type));

		return resources;
	}

	/**
	 * Getter voor Hospital van de campus
	 * 
	 * @return de Hospital
	 */
	public Hospital getBigHospital() {
		return hospital;
	}

	/**
	 * Een toString voor campus.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Campus "
				+ Integer.toString(this.getBigHospital().getCampuses()
						.indexOf(this) + 1);
	}

	/**
	 * Een methode om te controleren of twee campussen gelijk zijn.
	 * 
	 * @return true Campussen zijn gelijk als hun campus id gelijk zijn
	 * @return false Campussen zijn niet gelijk als hun campus id niet gelijk
	 *         zijn
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof CampusId)
			return o.equals(this.id);
		if (o instanceof Campus) {
			Campus campus = (Campus) o;

			return campus.id.equals(this.id);
		}
		return false;
	}

	/**
	 * @return
	 * 			de conditie waarin de campus zich bevind (deze is gelinked met de
	 * 			status en moet altijd hetzelfde zijn). Dit is een soort predikaat dat
	 * 			voldaan moet zijn voor de patientrepository patienten mag toevoegen
	 * 			en dischargen.
	 */
	private CampusCondition getCondition() {
		return this.condition;
	}


	/**
	 * @return
	 * 			de status van deze campus
	 */
	private CampusState getState() {
		return this.state;
	}

	/**
	 * Verzet de status van de campus
	 * 
	 * @param state
	 * 			de status naarwelke deze campus moet verzet worden
	 */
	private void setState(CampusState state) {
		this.state = state;
	}

	/**
	 * Maakt de campus die open is gesloten
	 * 
	 * @param timeOfFirstDiagnosis
	 * 			tijdstip van de eerste diagnose waarvan een epidemie uitbreekt
	 * @throws IllegalOperationException
	 * 			als de campus niet open is
	 */
	public void lockdown(TimeStamp timeOfFirstDiagnosis) 
				throws IllegalOperationException {
		this.getState().lockdown(timeOfFirstDiagnosis);
	}

	/**
	 * Maakt de campus die in lockdown is terug open
	 * 
	 * @throws IllegalOperationException
	 * 			als de campus niet in lockdown is
	 */
	public void open() throws IllegalOperationException{
		this.getState().open();
	}

	/**
	 * Geeft een lijst terug van mensen die de campus hebben verlaten sinds de
	 * uitbraak en in tussentijd nog aanwezig zijn geweest.
	 * 
	 * @return
	 * 			een lijst van mensen die mogelijk risico lopen als de campus in
	 * 			lockdown zit
	 * @throws IllegalOperationException
	 * 			als de campus niet in lockdown zit
	 */
	public List<ScheduleResource> getListfPeopleWhoLeft() 
				throws IllegalOperationException {
		return this.getState().getListOfPeopleWhoLeft();
	}

	/**
	 * Boolean om te zien of deze campus in lockdown is
	 * 
	 * @return
	 * 			waar als deze campus in lockdown is
	 */
	public boolean isInLockdown() {
		return this.getState().isInLockdown();
	}
	
	/**
	 * Gebruikt de resetter om de getelde ziektes opnieuw te initialiseren. Dit
	 * kan gebruikt worden wanneer de administrator zegt dat een epidemie voorbij
	 * is.
	 */
	public void resetDiseases() {
		this.diseaseResetter.reset();
	}

	/**
	 * 
	 * De status van de campus.
	 *
	 */
	private interface CampusState {
		/**
		 * Deze methode plaatst de Campus in een lockdown toestand en doet het nodige.
		 * Werkt enkel in de open status van de campus.
		 * 
		 * @param timeOfFirstDiagnosis
		 * 			het tijdstip van de eerste diagnose van deze epidemie
		 * 
		 * @throws IllegalOperationException
		 * 			als de campus niet in de DefaultCampusState status zit
		 */
		void lockdown(TimeStamp timeOfFirstDiagnosis) throws IllegalOperationException;

		/**
		 * Deze methode plaatst de Campus in een open toestand en doet het nodige.
		 * Werkt enkel in de gesloten status van de campus.
		 * 
		 * @throws IllegalOperationException
		 * 			als de campus niet in de LockdownCampusState status zit
		 */
		void open() throws IllegalOperationException;

		/**
		 * Geeft een lijst van pati‘nten en personeelsleden terug die sinds de 
		 * eerste diagnose op deze campus hebben gezeten maar nu niet meer op de
		 * campus zitten.
		 * 
		 * @return
		 * 			een lijst van pati‘nten en personeelsleden die gevaar lopen voor besmetting
		 * @throws IllegalOperationException
		 * 			als de campus niet in de LockdownCampusState status zit
		 */
		List<ScheduleResource> getListOfPeopleWhoLeft() throws IllegalOperationException;

		/**
		 * 
		 * @return
		 * 			waar als deze campus in de LockdownCampusState status zit
		 */
		boolean isInLockdown();
	}

	/**
	 * De gewone (open) campus status
	 *
	 */
	private class DefaultCampusState implements CampusState {

		/**
		 * Verandert de status naar lockdown en haalt alle stocks leeg
		 */
		@Override
		public void lockdown(TimeStamp timeOfFirstDiagnosis) {
			Campus.this.getCondition().lockDown();
			Campus.this.setState(new LockdownCampusState(timeOfFirstDiagnosis));

			for (Stock stock : warehouse.getStockList().getStocks()) {
				stock.cancelAllOrders();
			}
		}

		/**
		 * Gaat niet, geeft IllegalOperationException
		 */
		@Override
		public void open() {
			throw new IllegalOperationException("Campus already open");
		}

		/**
		 * Gaat niet, geeft IllegalOperationException
		 */
		@Override
		public List<ScheduleResource> getListOfPeopleWhoLeft() {
			throw new IllegalOperationException("Campus not in lockdown");
		}

		/**
		 * Geeft vals terug
		 */
		@Override
		public boolean isInLockdown() {
			return false;
		}
	}

	/**
	 * 
	 * De gesloten campus status
	 *
	 */
	private class LockdownCampusState implements CampusState {
		/**
		 * Het tijdstip van de eerste diagnose die deze epidemie startte
		 */
		private final TimeStamp timeOfEpidemicStart;
		
		/**
		 * Maakt een nieuwe gesloten campus status aan
		 * 
		 * @param timeOfFirstDiagnosis
		 * 			Het gegeven tijdstip van de eerste diagnose die deze epidemie starttte
		 */
		private LockdownCampusState(TimeStamp timeOfFirstDiagnosis) {
			this.timeOfEpidemicStart = timeOfFirstDiagnosis;
		}

		/**
		 * Gaat niet, geeft IllegalOperationException
		 */
		@Override
		public void lockdown(TimeStamp timeOfFirstDiagnosis) {
			throw new IllegalOperationException("Campus already in lockdown");
		}

		/**
		 * Zorgt ervoor dat de status van de campus verandert naar open en
		 * vult alle stocks terug met de nodige elementen.
		 */
		@Override
		public void open() {
			Campus.this.getCondition().open();
			Campus.this.setState(new DefaultCampusState());
			
			for (Stock stock : warehouse.getStockList().getStocks())
				stock.fillStock();
		}

		/**
		 * Geeft een lijst van personen die sinds de eerste diagnose van deze
		 * epidemie de campus hebben verlaten en sinds de uitbraak aanwezig
		 * zijn geweest.
		 */
		@Override
		public List<ScheduleResource> getListOfPeopleWhoLeft() {
			List<ScheduleResource> returnList = new ArrayList<ScheduleResource>();

			getValidStaff(Campus.this.getBigHospital().getStaffRepository(),
					returnList);
			getValidStaff(Campus.this.getStaffRepository(), returnList);
			getValidPatients(returnList);

			return returnList;
		}

		/**
		 * Zet het personeel dat risico loopt na gegeven datum en niet meer op
		 * de campus zijn bij in de gegeven lijst
		 * 
		 * @param list
		 * 			De aan te vullen lijst
		 */
		private void getValidStaff(StaffRepository staff,
				List<ScheduleResource> list) {
			for (StaffMember member : staff.getStaff()) {
				TimeStamp now = Campus.this.getBigHospital().getHospitalTime()
						.getTime();

				if (member
						.wasOnCampus(Campus.this.id, this.timeOfEpidemicStart, now)
						&& !member.wasOnCampus(Campus.this.id, now, now))
					list.add(member);
			}
		}

		/**
		 * Zet de pati‘nten die risico lopen na gegeven datum en niet meer op
		 * de campus zijn bij in de gegeven lijst
		 * 
		 * @param list
		 * 			De aan te vullen lijst
		 */
		private void getValidPatients(List<ScheduleResource> list) {
			for (Patient patient : Campus.this.getPatientRepository()
					.getDischargedPatients()) {
				if (patient.getTimeOfLastDischarge().compareTo(
						this.timeOfEpidemicStart) >= 0)
					list.add(patient);
			}
		}
		
		/**
		 * Geeft waar terug
		 */
		@Override
		public boolean isInLockdown() {
			return true;
		}
	}

	/**
	 * 
	 * Een interne klasse die gebruikt kan worden om een alarm door te geven aan
	 * een andere klasse. Dit wordt in dit geval gebruikt om de de campus naar
	 * een lockdown te sturen en wordt meegegeven aan de EpidemicHandler. Zo
	 * hoeft de EpidemicHandler geen binding te hebben aan de campus maar heeft
	 * hij genoeg aan dit alarm om de campus het nodige te laten doen bij een epidemie.
	 *
	 */
	private class CampusAlarm implements Alarm {

		/**
		 * Als de campus nog niet in lockdown is, zorgt deze methode daarvoor.
		 * Er wordt een TimeStamp object meegegeven, dewelke de tijd van de
		 * eerste diagnose van deze epidemie bevat.
		 */
		@Override
		public void notifyAlarm(Object timeOfFirstDiagnosis) {
			if (!Campus.this.isInLockdown())
				Campus.this.lockdown((TimeStamp) timeOfFirstDiagnosis);
		}

		/**
		 * Deze methode opent de campus terug, als deze in lockdown is.
		 */
		@Override
		public void falseAlarm() {
			if (Campus.this.isInLockdown())
				Campus.this.open();
		}

	}
}
