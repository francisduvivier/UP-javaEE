package system.scheduling;

import client.IScheduleResource;
import system.repositories.ResourceType;

/** 
 * Deze interface zorgt voor alle functionaliteit van resources die nodig
 * is voor de scheduler. Wanneer een klasse deze interface implementeert
 * kan deze gebruikt worden als resource door de scheduler.
 *
 */

public interface ScheduleResource extends IScheduleResource {	
	/**
	 * Het type van resource wordt meegegeven als een enumeratie. 
	 * @return Het type van resource, zie ook ResourceType enumeratie
	 */
	public ResourceType getResourceType(); 	
	
	/**
	 * De planning van deze resource.
	 * @return De planning van deze resource.
	 */
	public Schedule getSchedule();	
}
