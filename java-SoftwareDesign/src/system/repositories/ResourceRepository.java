package system.repositories;

import java.util.List;

import system.scheduling.ScheduleResource;


/**
 * Nodig voor het plannen 
 * Deze interface wordt gebruikt om elke repositoryklasse
 * te verplichten de methode getResources te implementeren
 * 
 * @author SWOP Team 10
 */
public interface ResourceRepository {
	public List<ScheduleResource> getResources(ResourceType type) ;
}
