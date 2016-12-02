package client;

import java.util.List;

import system.campus.Campus;
import system.patients.Patient;
import system.repositories.ResourceType;
import system.repositories.StaffRepository;
import system.scheduling.NormalScheduler;
import system.scheduling.ScheduleResource;
import system.time.Time;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een ziekenhuis voor met een aantal campussen.
 *
 */
@SystemAPI
public interface IHospital {
	/**
	 * @return
	 * 			Geeft het aantal hospitals terug
	 */
	@SystemAPI
	int getNbHospitals();
	
	/**
	 * Getter voor de verzameling van ziekenhuispersoneel
	 * 
	 * @return staffRepository
	 *         Collectie van ziekenhuispersoneel
	 */
	@SystemAPI
	StaffRepository getStaffRepository();
	
	/**
	 * geeft een lijst met de campussen van dit hospital erin terug
	 * @return een lijst met campussen
	 */
	@SystemAPI
	List<Campus> getCampuses();
	
	/**
	 * Methode die een collectie van alle geregistreerde patienten teruggeeft overheen alle campussen.
	 * 
	 * @return result
	 * 			Collectie van alle geregistreerde patienten overheen alle campussen
	 */
	@SystemAPI
	List<Patient> getAllRegisteredPatients() ;
	
	/**
	 * Methode die een collectie van alle niet-ontslane patienten teruggeeft overheen alle campussen.
	 * 
	 * @return result
	 * 			Collectie van alle niet-ontslane patienten overheen alle campussen
	 */
	@SystemAPI
	List<Patient> getAllNonDischargedPatients();
	
	/**
	 * Getter voor het planningssysteem
	 * 
	 * @return scheduler
	 *         Het planningssysteem
	 */
	@SystemAPI
	NormalScheduler getScheduler();
	
	/**
	 * Getter voor de tijd van het ziekenhuis
	 * 
	 * @return hospitalTime
	 *         De tijd van het ziekenhuis
	 */
	@SystemAPI
	Time getHospitalTime();
	
	/**
	 * Nodig om resultaten te registreren.
	 * Methode die een collectie van alle resources van een bepaald type teruggeeft over alle campussen.
	 * 
	 * @param type
	 * 			Resourcetype waarvan we een collectie willen
	 * @return allResources
	 * 			Collectie van resources van opgegeven type overheen alle campussen
	 */
	@SystemAPI
	List<ScheduleResource> getAllResources(ResourceType type);
}
