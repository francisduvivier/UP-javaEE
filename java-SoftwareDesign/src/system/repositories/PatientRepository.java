package system.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import system.epidemic.EpidemicHandler;
import system.exceptions.IllegalOperationException;
import system.patients.Patient;
import system.scheduling.ScheduleResource;
import system.time.Time;
import system.util.Condition;


/**
 * Deze klasse houdt alle patienten van het ziekenhuis bij
 * 
 * @author SWOP Team 10
 */
public class PatientRepository implements ResourceRepository {
	/**
	 * Lijst die alle machines van het ziekenhuis bijhoudt
	 */
	private List<Patient> registeredPatients;
	/**
	 * De conditie van de campus.
	 */
	private final Condition condition;
	/**
	 * Een observer die kan meegeven worden aan de PatientFile van elke Patient
	 */
	private final Observer observer;
	
	private final Time time;
		
	/**
	 * Constructor van PatientRepository
	 * 
	 * @param	condition
	 * 				conditie (be•nvloedt door externen) die nagekeken wordt bij bepaalde handelingen
	 * @param	epidemicHandler
	 * 				de epidemiehandelaar moet als observer bewaart worden omdat deze als observer 
	 * 				aan elke nieuwe patientfile van een nieuwe patient moet meegegeven worden
	 * @param	time
	 * 				de algemene tijd
	 */
	public PatientRepository(Condition condition, EpidemicHandler epidemicHandler, Time time) {
		this.registeredPatients = new ArrayList<Patient>();
		this.condition = condition;
		if (epidemicHandler == null)
			this.observer = null;
		else
			this.observer = epidemicHandler.getObserver();
		this.time = time;
	}
	
	/**
	 * Zelfde constructor, er wordt geen epidemicHandler gebruikt
	 */
	public PatientRepository(Condition condition,Time time) {
		this(condition,null,time);
	}
	
	/**
	 * Zelfde constructor, er wordt geen epidemicHandler noch condition gebruikt
	 * @param time
	 */
	public PatientRepository(Time time) {
		this(null,null,time);
	}

	/**
	 * Getter voor alle patienten die ooit geregistreerd zijn
	 * 
	 * @return registeredPatients
	 *         Alle patienten die ooit geregistreerd zijn
	 */
	public List<Patient> getRegisteredPatients() {
		return Collections.unmodifiableList(registeredPatients);
	}

	/**
	 * Getter voor de patienten die ontslaan zijn
	 * 
	 * @return dischargedPatients
	 *         De patienten die ontslaan zijn
	 */
	public List<Patient> getDischargedPatients() {
		List<Patient> dischargedPatients = new ArrayList<Patient>();
		for (Patient registeredPatient : registeredPatients) {
			if (registeredPatient.isDischarged())
				dischargedPatients.add(registeredPatient);
		}
		return Collections.unmodifiableList(dischargedPatients);
	}
	
	/**
	 * Getter voor de patienten die opgenomen zijn
	 * 
	 * @return nonDischargedPatients
	 *         De patienten die opgenomen zijn
	 */
	public List<Patient> getNonDischargedPatients() {
		List<Patient> nonDischargedPatients = new ArrayList<Patient>();
		for (Patient registeredPatient : registeredPatients) {
			if (!registeredPatient.isDischarged())
				nonDischargedPatients.add(registeredPatient);
		}
		return nonDischargedPatients;
	}

	/**
	 * Methode om een patient te registeren
	 * 
	 * @param patient
	 *        De te registreren patient
	 *        
	 * @throws	IllegalOperationException
	 * 			Als de conditie niet voldaan is (lockdown)
	 */
	public void addPatient(Patient patient) {
		if (this.condition != null && !this.condition.check(null))
			throw new IllegalOperationException("The patient could not be added, campus in lockdown.");
		
		if (this.registeredPatients.contains(patient) && patient.isDischarged()) {
			patient.register();
		} else {
			if (observer != null) 
				patient.getPatientFile().addObserver(observer);
		
			this.registeredPatients.add(patient);
		}
	}
	
	/**
	 * Methode om een patient uit het ziekenhuis te ontslaan
	 * 
	 * @param patient
	 *        De te ontslagen patient
	 *        
	 * @throws 	IllegalArgumentException
	 * 			Als de patient niet geregistreerd is
	 * 
	 * @throws	IllegalOperationException
	 * 			Als de conditie niet voldaan is (lockdown)
	 */
	public void dischargePatient(Patient patient) {
		if (!this.registeredPatients.contains(patient))
			throw new IllegalArgumentException();
		if (this.condition != null && !this.condition.check(null))
			throw new IllegalOperationException("The patient could not be discharged, campus in lockdown.");
		
		patient.discharge(time.getTime());
	}

	/**
	 * Nodig voor het plannen
	 * Methode die een collectie van patienten van een bepaald type teruggeeft
	 * 
	 * @param type
	 *        Het type patient
	 * @return resourceList
	 *         De verzameling patienten van het opgegeven type
	 */
	@Override
	public List<ScheduleResource> getResources(ResourceType type) {
		List<ScheduleResource> resourceList = new ArrayList<ScheduleResource>();
		for (ScheduleResource registeredPatient : registeredPatients){
				if(registeredPatient.getResourceType() == type)
				resourceList.add(registeredPatient);
		}
		return Collections.unmodifiableList(resourceList);
	}
}
