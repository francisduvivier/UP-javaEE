package client;

import annotations.SystemAPI;

/**
 * Deze klasse stelt een patient voor.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public interface IPatient {
	/**
	 * Getter voor de naam van de patient
	 * 
	 * @return name De naam van de patient
	 */
	@SystemAPI
	String getName();
}
