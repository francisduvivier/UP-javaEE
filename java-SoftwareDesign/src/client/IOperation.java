package client;

import system.scheduling.EventType;
import annotations.SystemAPI;

/**
 * Deze interface stelt een actie die men op een patient kan uitvoeren voor.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public interface IOperation {
	
	/**
	 * 
	 * @return
	 * 			Het type actie dat ondernomen wordt.
	 */
	@SystemAPI
	EventType getEventType();
}
