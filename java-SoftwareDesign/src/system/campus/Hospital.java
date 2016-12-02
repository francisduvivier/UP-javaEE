package system.campus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import client.IHospital;

import system.patients.Patient;
import system.repositories.HospitalStaffRepository;
import system.repositories.ResourceRepository;
import system.repositories.ResourceType;
import system.repositories.StaffRepository;
import system.scheduling.EventHandler;
import system.scheduling.NormalScheduler;
import system.scheduling.ScheduleResource;
import system.time.Time;
import system.time.TimeDuration;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een ziekenhuis voor met een aantal campussen.
 *
 */
@SystemAPI
public class Hospital implements IHospital {
	private final StaffRepository staffRepository;
	@SystemAPI
	
	/**
	 * Variabele die het aantal ziekenhuizen (campussen) voorstelt.
	 */
	private final int nbHospitals=2;
	
	/**
	 * EventHandler houdt een tijdslijn van gebeurtenissen bij 
	 * om ze te kunnen verwerken als de tijd vooruitgaat
	 */
	private final EventHandler eventHandler;
	 /**
	 * De campussen van de hospital. Er is een bidirectionele binding hiertussen
	 * maar deze kan niet inconsistent worden omdat zowel omdat de velden bij
	 * beide klassen final zijn en er in de contructor gechecked word of de
	 * binding klopt.
	 */
	private final Campus campuses[];
	/**
	 * Variabele die het planningsysteem voorstelt
	 */
	private final NormalScheduler scheduler;
	/**
	 * Variabele die de tijd van het ziekenhuis voorstelt
	 */
	private final Time hospitalTime;
	/**
	 * Een object met methodes om de verplaatsingstijd tussen 2 campussen te berekenen.
	 */
	private CampusTravelTimes travelTime;
	
	/**
	 * De constructor van Hospital.
	 */
	@SystemAPI
	public Hospital(){
		this.hospitalTime = new Time(2011,10,8,8,0);
		this.campuses=new Campus[nbHospitals];
		this.scheduler = new NormalScheduler(this);
		initiateCampusesAndTravelTimes();
		this.eventHandler=new EventHandler(this);
		this.staffRepository= new HospitalStaffRepository();
	}
	
	public int getNbHospitals() {
		return nbHospitals;
	}

	/**
	 * Genereert de campussen en de traveltimes.
	 */
	private void initiateCampusesAndTravelTimes() {
		this.campuses[0]=new Campus(this);
		this.campuses[1]=new Campus(this);
		HashMap<Object, Integer> weightHashmap=new HashMap<Object, Integer>();
		weightHashmap.put(new CampusId(campuses[0]), (int)TimeDuration.minutes(15).getMilliseconds());
		List<HashMap<Object,Integer>> weightHashMaps=new ArrayList<HashMap<Object,Integer>>();
		weightHashMaps.add(weightHashmap);
		this.travelTime=new CampusTravelTimes(getCampuses(), weightHashMaps);
	}

	/**
	 * Getter voor de tijd van het ziekenhuis
	 * 
	 * @return hospitalTime
	 *         De tijd van het ziekenhuis
	 */
	public Time getHospitalTime() {
		return hospitalTime;
	}
	
	/**
	 * geeft een lijst met de campussen van dit hospital erin terug
	 * @return een lijst met campussen
	 */
	public List<Campus> getCampuses(){
		List<Campus> unmodifiableList=new ArrayList<Campus>(Arrays.asList(campuses));
		return unmodifiableList;
	}
	
	/**
	 * Methode om een campus object op te vragen uit de collectie van campussen met zijn identifier.
	 * 
	 * @param id
	 * 			De unieke identifier van de campus
	 * @return campus
	 * 			De campus overeenkomstig met de identifier
	 */
	public Campus getCampusById(CampusId id) {
		for (Campus campus : campuses)
			if (campus.equals(id))
				return campus;
		return null;
	}
	
	/**
	 * Geeft de hoeveelheid tijd die nodig is om te reizen tussen 2 campussen
	 * @param id1 & id2 de Id's van de campussen waar het over gaat.
	 * @return De hoeveelheid tijd in milliseconden
	 */
	public TimeDuration calcTravelTime(CampusId id1, CampusId id2){
		return this.travelTime.calculateTravelTime(id1, id2);
	}
	
	/**
	 * Geeft het object terug waarmee de reistijd tussen twee campussen berekend worden
	 * @return
	 */
	public CampusTravelTimes getCampusTravelTimes() {
		return travelTime;
	}

	/**
	 * Getter voor de verzameling van ziekenhuispersoneel
	 * 
	 * @return staffRepository
	 *         Collectie van ziekenhuispersoneel
	 */
	public StaffRepository getStaffRepository() {
		return staffRepository;
	}
	
	/**
	 * Getter voor het planningssysteem
	 * 
	 * @return scheduler
	 *         Het planningssysteem
	 */
	public NormalScheduler getScheduler() {
		return scheduler;
	}
	
	/**
	 * Nodig voor het plannen
	 * Methode die een collectie van alle resourceRepositories teruggeeft
	 * van de hospital tezamen met de opgegeven campus
	 * @param	campus
	 * 			Campus waarvan we de resource repositories willen
	 * @return resourceRepositories
	 *         Collectie bestaande uit :
	 *         personeel,
	 *         machines en 
	 *         patienten
	 */
	private List<ResourceRepository> getResourceRepositories(Campus campus) {
		List<ResourceRepository> resourceRepositories = new ArrayList<ResourceRepository>();
		resourceRepositories.add(staffRepository);
		resourceRepositories.add(campus.getStaffRepository());
		resourceRepositories.add(campus.getMachineRepository());
		resourceRepositories.add(campus.getPatientRepository());
		return resourceRepositories;
	}
	
	/**
	 * Nodig voor het plannen
	 * Methode die een collectie van resources van een bepaald type teruggeeft
	 * van de hospital tezamen met de opgegeven campus
	 * 
	 * @param	type
	 * 			Type resource waarvan we een lijst willen
	 * @param	campus
	 * 			De campus waarvan we een lijst willen
	 * 
	 * @return resources
	 *         Collectie van resources van het opgegeven type
	 */
	public List<ScheduleResource> getResources(ResourceType type, Campus campus) {
		List<ResourceRepository> resourceRepositories = this.getResourceRepositories(campus);
		List<ScheduleResource> resources = new ArrayList<ScheduleResource>();
		
		for (ResourceRepository resourceRepository : resourceRepositories) 
			resources.addAll(resourceRepository.getResources(type));
		
		return resources;
	}
	
	
	/**
	 * Nodig om resultaten te registreren.
	 * Methode die een collectie van alle resources van een bepaald type teruggeeft over alle campussen.
	 * 
	 * @param type
	 * 			Resourcetype waarvan we een collectie willen
	 * @return allResources
	 * 			Collectie van resources van opgegeven type overheen alle campussen
	 */
	public List<ScheduleResource> getAllResources(ResourceType type) {
		List<ScheduleResource> allResources = new ArrayList<ScheduleResource>();
		
		for (Campus campus : getCampuses()) 
			allResources.addAll(getResources(type,campus));
		
		return allResources;
	}
	
	/**
	 * Methode die een collectie van alle geregistreerde patienten teruggeeft overheen alle campussen.
	 * 
	 * @return result
	 * 			Collectie van alle geregistreerde patienten overheen alle campussen
	 */
	public List<Patient> getAllRegisteredPatients() {
		List<Patient> result = new ArrayList<Patient>();
		for (Campus campus : getCampuses())
			result.addAll(campus.getPatientRepository().getRegisteredPatients());
		return result;
	}
	
	/**
	 * Methode die een collectie van alle niet-ontslane patienten teruggeeft overheen alle campussen.
	 * 
	 * @return result
	 * 			Collectie van alle niet-ontslane patienten overheen alle campussen
	 */
	public List<Patient> getAllNonDischargedPatients() {
		List<Patient> result = new ArrayList<Patient>();
		for (Campus campus : getCampuses())
			result.addAll(campus.getPatientRepository().getNonDischargedPatients());
		return result;
	}
	/**
	 * Getter voor de instantie van EventHandler, 
	 * een klasse die een tijdslijn van gebeurtenissen bij 
	 * om ze te kunnen verwerken als de tijd vooruitgaat
	 * 
	 * @return eventHandler
	 *         object van EventHandler
	 */
	public EventHandler getEventHandler() {
		return eventHandler;
	}
		

}
