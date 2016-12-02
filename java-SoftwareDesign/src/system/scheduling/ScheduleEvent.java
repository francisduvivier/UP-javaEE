package system.scheduling;
import java.util.List;

import client.IOperation;


import system.campus.Campus;
import system.campus.CampusId;
import system.exceptions.SchedulingException;
import system.repositories.ResourceType;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.warehouse.Warehouse;
import annotations.SystemAPI;


/**
 * Deze interface zorgt voor alle functionaliteit van zaken die gescheduled
 * moeten worden. Wanneer een klasse deze interface implementeert kan deze
 * gescheduled worden.
 *
 */
@SystemAPI
public interface ScheduleEvent extends IOperation {
	/**
	 * 
	 * @return	De duur van de planbare actie (in Millis)
	 */
	public TimeDuration getDuration();
	
	/**
	 * 
	 * @return	De periode waarop de actie gescheduled is
	 * 			Dit wordt ingesteld door de Scheduler
	 */
	@SystemAPI
	public TimePeriod getScheduledPeriod(); 
	
	/**
	 * Als de schedulable specifieke resources nodig heeft, bv. een bepaalde 
	 * dokter of verpleegster, dan komen deze in de onderstaande List.
	 * @return		De lijst met specifieke resources die nodig zijn.
	 */
	public List<ScheduleResource> neededSpecificResources();
	
	/**
	 * 
	 * @return	Een lijst met de nodige resources.
	 */
	public List<ResourceType> neededResources();
	
	/**
	 * Zet de periode wanneer de actie gescheduled is.
	 * @param scheduledPeriod	De nieuwe geplande periode
	 * @param usesResources	De resources die gebruikt worden bij het plannen
	 * @param campus De campus die gebruikt wordt bij het plannen
	 * @throws SchedulingException Als er niet gepland kan worden
	 */
	public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> usesResources, Campus campus) throws SchedulingException;
		
	/**
	 * Een methode om te controleren of de events behorende bij een warenhuis gepland kunnen worden.
	 * @param warehouse
	 * 		  Het warenhuis waar gepland moet worden
	 * @return true
	 * 		   Als er gepland kan worden
	 * @return false
	 * 		   Als er niet gepland kan worden
	 */
	public boolean canBeScheduled(Warehouse warehouse) ;
	
	/**
	 * Een methode om een warehuis te updaten. Met de booleanse waarde
	 * kan de actie ongedaan gemaakt worden.
	 * 
	 * @param warehouse
	 * 		  Het warenhuis geupdate moet worden
	 * @param inverse
	 * 		  De soort update
	 */
	public void updateWarehouse(Warehouse warehouse, boolean inverse) ;
	
	/**
	 * Een methode om de prioriteit op te vragen.
	 * @return
	 */
	public Priority getPriority() ;	
	
	/**
	 * Methode die de campus waarop deze actie wordt uitgevoerd opslaat, zodat
	 * ze later kan gereproduceerd worden wanneer er geannuleerd werd.
	 * @param 	campus
	 * 			De CampusId van de campus die moet opgeslagen worden
	 */
	public void setHandlingCampus(CampusId campus) ;
	
	/**
	 * Methode die de opgeslagen campus terug opvraagt.
	 * @return	De opgeslagen campus
	 */
	public CampusId getHandlingCampus();
	
	/**
	 * 
	 * @return event type
	 */
	@SystemAPI
	public EventType getEventType();
	
	/**
	 * @return start event
	 */
	public Event getStart();
	
	/**
	 * 
	 * @return stop event
	 */
	public Event getStop();
}

