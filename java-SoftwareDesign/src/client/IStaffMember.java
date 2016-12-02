package client;

import system.repositories.ResourceType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een personeelslid van het ziekenhuis voor
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public interface IStaffMember {
	/**
	 * Een methode om het stafftype te krijgen.
	 */
	@SystemAPI
	ResourceType getResourceType();
}
